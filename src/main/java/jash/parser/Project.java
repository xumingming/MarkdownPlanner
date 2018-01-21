package jash.parser;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jash.parser.ProjectStat.UserStat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 *
 */
@Getter
@ToString
@EqualsAndHashCode(exclude = {"stat"})
public class Project {
    /** 名字 */
    private String name;
    /** 项目开始时间 */
    private LocalDate projectStartDate;
    /** 所有任务 */
    private List<Task> tasks;
    /** 所有的请假安排 */
    private List<Vacation> vacations;
    /** 项目统计信息 */
    private ProjectStat stat;

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

    public double getTotalCost() {
        return tasks.stream().mapToDouble(Task::getCost).sum();
    }

    public double getFinishedCost() {
        return tasks.stream().mapToDouble(Task::getFinishedCost).sum();
    }

    public boolean isInVacation(String user, LocalDate date) {
        return this.vacations.stream().filter(x -> x.getUser().equals(user) && x.contains(date))
            .findFirst().isPresent();
    }

    public boolean skip(String user, LocalDate date) {
        return isWeekend(date) || isInVacation(user, date);
    }

    public boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private void init() {
        Map<String, List<Task>> user2Tasks = new HashMap<>();
        Map<String, UserStat> userStats = new HashMap<>();
        this.tasks.stream().filter(task -> !task.isComposite()).forEach(task -> {
            task.setProjectStartDate(projectStartDate);

            String user = task.getOwner();
            if (!user2Tasks.containsKey(user)) {
                user2Tasks.put(user, new ArrayList<>());
            }

            if (!userStats.containsKey(user)) {
                userStats.put(user, new UserStat());
            }

            UserStat stat = userStats.get(user);
            stat.setUser(user);
            stat.addTotalCost(task.getCost());
            stat.addFinishedCost(task.getCost() * task.getProgress() / 100);
            user2Tasks.get(user).add(task);
        });


        stat = new ProjectStat(userStats);

        user2Tasks.keySet().stream()
            .forEach(user -> {
            List<Task> tasks = user2Tasks.get(user);
            int lastOffset = 0;
            for(Task task : tasks) {
                if (!task.isComposite()) {
                    int newOffset = calculateEndOffset(lastOffset, task.getCost(), task.getOwner());
                    task.setStartOffset(lastOffset);
                    task.setEndOffset(newOffset);

                    lastOffset = newOffset + 1;
                }
            }
        });

        // 初始化所有组合任务
        List<Task> notInitedTasks = tasks.stream().filter(t1 -> t1.getEndOffset() <= 0).collect(Collectors.toList());
        int initCount = 0;
        while (!notInitedTasks.isEmpty() && initCount < 5) {
            for (Task task : notInitedTasks) {
                int taskId = task.getId();
                Optional<Integer> startOffset = tasks.stream().filter(t -> t.getParentId() == taskId)
                    .map(Task::getStartOffset)
                    .min(Comparator.comparingInt(x -> x));
                Optional<Integer> endOffset = tasks.stream().filter(t -> t.getParentId() == taskId)
                    .map(Task::getEndOffset)
                    .max(Comparator.comparingInt(x -> x));
                task.setStartOffset(startOffset.isPresent() ? startOffset.get() : 0);
                task.setEndOffset(endOffset.isPresent() ? endOffset.get() : 0);
            }

            notInitedTasks = tasks.stream().filter(t1 -> t1.getEndOffset() <= 0).collect(Collectors.toList());
            initCount++;
        }
    }

    public int calculateEndOffset(int lastOffset, int numOfHalfDays, String owner) {
        int actualCost = 0;
        int count = 0;

        LocalDate currentDate = new HalfDayDuration(lastOffset).addToDate(projectStartDate).getDate();
        while (count < numOfHalfDays / 2) {
            // skip weekends & vacations first
            while (skip(owner, currentDate)) {
                currentDate = currentDate.plusDays(1);
                actualCost += 2;
            }

            count++;
            actualCost += 2;
            currentDate = currentDate.plusDays(1);
        }

        boolean hasHalfDay = (numOfHalfDays % 2 == 1);
        if (hasHalfDay) {
            // skip weekends & vacations first
            while (skip(owner, currentDate)) {
                currentDate = currentDate.plusDays(1);
                actualCost += 2;
            }

            actualCost++;
        }

        return actualCost + (lastOffset - 1);
    }

    public Project hideCompleted() {
        return new Project(
            name,
            projectStartDate,
            this.tasks.stream().filter(x -> !x.isCompleted()).collect(Collectors.toList()),
            vacations
        );
    }

    public Project hideNotCompleted() {
        return new Project(
            name,
            projectStartDate,
            this.tasks.stream().filter(x -> x.isCompleted()).collect(Collectors.toList()),
            vacations
        );
    }

    public Project onlyShowTaskForUser(String user) {
        return new Project(
            name,
            projectStartDate,
            this.tasks.stream().filter(x -> x.getOwner().equals(user)).collect(Collectors.toList()),
            vacations
        );
    }

    public Project filterKeyword(String keyword) {
        return filterKeyword(keyword, false);
    }

    public Project filterKeywords(List<String> keywords, boolean reverse) {
        return new Project(
            name,
            projectStartDate,
            this.tasks.stream()
                .filter(x -> {
                    boolean tmp = true;
                    for (String keyword : keywords) {
                        tmp = x.getName().toLowerCase().contains(keyword.toLowerCase());
                        if (tmp) {
                            break;
                        }
                    }

                    if (reverse) {
                        return !tmp;
                    } else {
                        return tmp;
                    }
                })
                .collect(Collectors.toList()),
            vacations
        );
    }

    public Project filterKeyword(String keyword, boolean reverse) {
        return new Project(
            name,
            projectStartDate,
            this.tasks.stream()
                .filter(x -> {
                    boolean tmp = x.getName().toLowerCase().contains(keyword.toLowerCase());
                    if (reverse) {
                        return !tmp;
                    } else {
                        return tmp;
                    }
                })
                .collect(Collectors.toList()),
            vacations
        );
    }
    public UserStat getUserStat(String user) {
        return stat.getUserStat(user);
    }

    public UserStat getTotalStat() {
        return stat.getTotalStat();
    }

    public LocalDate getProjectEndDate() {
        Optional<JashDate> ret = this.tasks.stream()
            .map(Task::getEndDate)
            .max(Comparator.comparing(JashDate::getDate));

        return ret.isPresent() ? ret.get().getDate() : null;
    }
}