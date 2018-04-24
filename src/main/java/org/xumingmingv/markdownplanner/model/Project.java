package org.xumingmingv.markdownplanner.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import org.xumingmingv.markdownplanner.model.task.CompositeTask;
import org.xumingmingv.markdownplanner.model.task.Task;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * 一个项目
 */
@Getter
@ToString
@EqualsAndHashCode(exclude = {"stat"})
public class Project implements IProject {
    /** 名字 */
    private String name;
    /** 项目开始时间 */
    private LocalDate projectStartDate;
    /** 所有任务 */
    private List<Task> tasks;
    /** 所有的请假安排 */
    private List<Vacation> vacations;
    /** 项目统计信息 */
    private Map<String, UserStat> userStats;

    public Project(String name, LocalDate projectStartDate, List<Task> tasks, List<Vacation> vacations) {
        this.name = name;
        this.projectStartDate = projectStartDate;
        this.tasks = tasks;
        this.vacations = vacations;
        init();
    }

    public Project(LocalDate projectStartDate, List<Task> tasks, List<Vacation> vacations) {
        this(null, projectStartDate, tasks, vacations);
    }

    public Project(LocalDate projectStartDate, List<Task> tasks) {
        this(projectStartDate, tasks, ImmutableList.of());
    }

    public List<String> getMen() {
        return tasks.stream()
            .filter(task -> !task.isComposite())
            .map(Task::getOwner)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
    }

    public double getProgress() {
        return getFinishedCost() * 100 / getTotalCost();
    }

    public int getTotalCost() {
        return tasks.stream()
            .filter(t -> !t.isComposite())
            .mapToInt(Task::getCost).sum();
    }

    public double getFinishedCost() {
        return tasks.stream()
            .filter(t -> !t.isComposite())
            .mapToDouble(Task::getFinishedCost).sum();
    }

    public boolean isInVacation(String user, LocalDate date) {
        return this.vacations.stream().filter(x -> x.getUser().equals(user) && x.contains(date))
            .findFirst().isPresent();
    }

    /**
     * 检查指定的日期对于指定的人来说是否是个实际有效的工作日(不是周末，这个人也没有请假)
     */
    public boolean skip(String user, LocalDate date) {
        return isWeekend(date) || isInVacation(user, date);
    }

    public boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private void init() {
        // 初始化一下 ProjectStartDate
        initProjectStartDateForTasks();
        // 按照用户对原子任务进行分组
        Map<String, List<Task>> user2Tasks = groupAtomicTasksByOwner();
        // 生成用户统计信息
        userStats = generateUserStat();
        // 初始化原子任务
        initAtomicTasks(user2Tasks);
        treenify();
        // 初始化复合任务
        initCompositeTasks();
    }

    void treenify() {
        List<Task> tasks = new ArrayList<>();

        // 先建一个“根”任务
        CompositeTask rootTask = new CompositeTask(Header.create(), this.name, this.projectStartDate);
        rootTask.setId(0);
        rootTask.setParentId(-1);
        Map<Header, CompositeTask> compositeTaskMap = new HashMap<>();
        compositeTaskMap.put(rootTask.getHeader(), rootTask);

        AtomicInteger idCounter = new AtomicInteger();
        for (Task task : this.tasks) {
            Header header = task.getHeader();
            addCompositeTaskIfNeeded(tasks, rootTask, compositeTaskMap, idCounter, header);

            CompositeTask parentTask = compositeTaskMap.get(task.getHeader());
            task.setId(idCounter.incrementAndGet());
            task.setParentId(parentTask.getId());
            tasks.add(task);

            addCosts(compositeTaskMap, header, task.getOwner(), task.getCost(), task.getFinishedCost());
        }
        tasks.add(0, rootTask);

        this.tasks = tasks;
    }

    void addCosts(Map<Header, CompositeTask> compositeTaskMap, Header header, String owner, int cost, double finishedCost) {
        for (int i = 0; i <= header.getHeaders().size(); i++) {
            Header currentHeader = new Header(header.getHeaders().subList(0, i));
            compositeTaskMap.get(currentHeader).addOwnerCost(owner, cost, finishedCost);
        }
    }

    void addCompositeTaskIfNeeded(List<Task> tasks, CompositeTask rootTask, Map<Header, CompositeTask> compositeTaskMap,
        AtomicInteger idCounter, Header header) {
        for (int i = 0; i < header.getHeaders().size(); i++) {
            Header currentHeader = new Header(header.getHeaders().subList(0, i + 1));

            if (!compositeTaskMap.containsKey(currentHeader)) {
                CompositeTask currentTask = new CompositeTask(
                    currentHeader, header.getHeaders().get(i).getDisplay(), rootTask.getProjectStartDate()
                );
                currentTask.setId(idCounter.incrementAndGet());
                if (i == 0) {
                    currentTask.setParentId(rootTask.getId());
                } else {
                    currentTask.setParentId(
                        compositeTaskMap.get(
                            new Header(header.getHeaders().subList(0, i))
                        ).getId()
                    );
                }
                compositeTaskMap.put(currentHeader, currentTask);
                tasks.add(currentTask);
            }
        }
    }

    void initCompositeTasks() {
        // 初始化所有组合任务
        List<Task> notInitedTasks = getNotInitedTasks();
        int initCount = 0;
        // 最多循环几次就好了，因为任务内嵌层级5层以上没太大意义。
        while (!notInitedTasks.isEmpty() && initCount < 10) {
            for (Task task : notInitedTasks) {
                int taskId = task.getId();
                List<Task> childrenTasks = getChildrenTasks(taskId);
                // 判断是不是它的所有儿子已经Ready(所有字段已经设置完毕)
                long readyChildTaskCount = childrenTasks.stream()
                    .filter(Task::isFullyPopulated)
                    .count();
                if (readyChildTaskCount == childrenTasks.size() && !task.isFullyPopulated()) {
                    Optional<Integer> startOffset = childrenTasks.stream()
                        .map(Task::getStartOffset)
                        .min(Comparator.naturalOrder());
                    Optional<Integer> endOffset = childrenTasks.stream()
                        .map(Task::getEndOffset)
                        .max(Comparator.naturalOrder());
                    Optional<Integer> usedCost = childrenTasks.stream()
                        .map(Task::getUsedCost)
                        .reduce((a, b) -> a + b);

                    task.setStartOffset(startOffset.get());
                    task.setEndOffset(endOffset.get());
                    task.setUsedCost(usedCost.get());
                }
            }

            notInitedTasks = getNotInitedTasks();
            initCount++;
        }
    }

    private List<Task> getChildrenTasks(int taskId) {
        return tasks.stream().filter(t -> t.getParentId() == taskId).collect(Collectors.toList());
    }

    private List<Task> getNotInitedTasks() {
        return tasks.stream()
                .filter(t1 -> t1.getEndOffset() == 0)
                .collect(Collectors.toList());
    }

    void initAtomicTasks(Map<String, List<Task>> user2Tasks) {
        user2Tasks.keySet().stream()
            .forEach(user -> {
            List<Task> tasks = user2Tasks.get(user);
            int lastOffset = 0;
            for(Task task : tasks) {
                if (!task.isComposite()) {
                    int newOffset = calculateEndOffset(lastOffset, task.getCost(), task.getOwner());
                    task.setStartOffset(lastOffset);
                    task.setEndOffset(newOffset);

                    // 这里我们有个假定，任务的endDate始终被正确的设置的。
                    // -- 如果任务延期了，那么endDate要及时延长一点
                    LocalDate endDate = task.getEndDate().getDate();
                    if (getCurrentDate().compareTo(endDate) < 0) {
                        endDate = getCurrentDate();
                    }
                    task.setUsedCost(calculateActualCost(task.getOwner(), task.getStartDate().getDate(), endDate));

                    lastOffset = newOffset + 1;
                }
            }
        });
    }

    LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    private Map<String, UserStat> generateUserStat() {
        Map<String, UserStat> userStats = new HashMap<>();
        this.tasks.stream().filter(task -> !task.isComposite()).forEach(task -> {
            String user = task.getOwner();
            if (!userStats.containsKey(user)) {
                userStats.put(user, new UserStat());
            }

            UserStat stat = userStats.get(user);
            stat.setUser(user);
            stat.addTotalCost(task.getCost());
            stat.addFinishedCost(task.getCost() * task.getProgress() / 100);
        });

        return userStats;
    }

    private Map<String, List<Task>> groupAtomicTasksByOwner() {
        Map<String, List<Task>> user2Tasks = new HashMap<>();
        this.tasks.stream().filter(task -> !task.isComposite()).forEach(task -> {
            String user = task.getOwner();
            if (!user2Tasks.containsKey(user)) {
                user2Tasks.put(user, new ArrayList<>());
            }

            user2Tasks.get(user).add(task);
        });

        return user2Tasks;
    }

    private void initProjectStartDateForTasks() {
        this.tasks.stream().forEach(task -> {
            task.setProjectStartDate(projectStartDate);
        });
    }

    @Override
    public UserStat getUserStat(String user) {
        return userStats.get(user);
    }

    public int calculateEndOffset(int lastOffset, int numOfHalfDays, String owner) {
        LocalDate currentDate = new HalfDayDuration(lastOffset).addToDate(projectStartDate).getDate();
        NextManDay nextManDay = advanceCost(owner, currentDate, numOfHalfDays);
        return nextManDay.getElapsedCost() + (lastOffset - 1);
    }

    /**
     * 计算指定人在指定的日期区间内实际可用的人日
     * @param owner
     * @param startDate
     * @param endDate
     * @return
     */
    public int calculateActualCost(String owner, LocalDate startDate, LocalDate endDate) {
       int ret = 0;
       LocalDate tmp = startDate;
       while(tmp.compareTo(endDate) <= 0) {
           NextManDay nextManDay = advanceCost(owner, tmp, endDate, 2);
           tmp = nextManDay.getDate();
           ret += nextManDay.getActualCost();
       }
       return ret;
    }

    /**
     * 为指定的人从指定的日期开始寻找下个有效的工作日
     */
    public NextManDay advanceCost(String owner, LocalDate currentDate, int cost) {
        return advanceCost(owner, currentDate, null, cost);
    }

    /**
     * 为指定的人从指定的日期开始寻找下个有效的工作日
     */
    public NextManDay advanceCost(String owner, LocalDate currentDate, LocalDate maxDate, int cost) {
        int elapsedCost = 0;
        int actualCost = 0;
        int count = 0;

        while (count < cost / 2) {
            // skip weekends & vacations first
            while (skip(owner, currentDate)) {
                currentDate = currentDate.plusDays(1);
                elapsedCost += 2;
            }

            if (maxDate != null && currentDate.compareTo(maxDate) > 0) {
                break;
            } else {
                count++;
                actualCost += 2;
                elapsedCost += 2;
                currentDate = currentDate.plusDays(1);
            }
        }

        boolean hasHalfDay = (cost % 2 == 1);
        if (hasHalfDay) {
            // skip weekends & vacations first
            while (skip(owner, currentDate)) {
                currentDate = currentDate.plusDays(1);
                elapsedCost += 2;
            }

            if (maxDate != null && currentDate.compareTo(maxDate) > 0) {
                // empty
            } else {
                elapsedCost++;
                actualCost++;
            }
        }

        NextManDay ret = new NextManDay();
        ret.setElapsedCost(elapsedCost);
        ret.setActualCost(actualCost);
        ret.setDate(currentDate);
        return ret;
    }

    @Data
    public static class NextManDay {
        /** 下一个工作日 */
        private LocalDate date;
        /** 实际跳过的Cost */
        private int elapsedCost;
        /** 实际有效的Cost(周末和请假不算) */
        private int actualCost;
    }
}