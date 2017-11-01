package jash.parser;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jash.parser.ProjectStat.UserStat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 *
 */
@Getter
@ToString
@EqualsAndHashCode(exclude = {"stat"})
public class Project {
    private LocalDate projectStartDate;
    private List<Task> tasks;
    private List<Vacation> vacations;
    private ProjectStat stat;


    public Project(LocalDate projectStartDate, List<Task> tasks, List<Vacation> vacations) {
        this.projectStartDate = projectStartDate;
        this.tasks = tasks;
        this.vacations = vacations;
        init();
    }

    public List<String> getMen() {
        return tasks.stream().map(Task::getOwner).distinct().collect(Collectors.toList());
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
        this.tasks.forEach(task -> {
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

        user2Tasks.keySet().stream().forEach(user -> {
            List<Task> tasks = user2Tasks.get(user);
            int lastOffset = 0;
            for(Task task : tasks) {
                int newOffset = calculateEndOffset(lastOffset, task.getCost(), task.getOwner());
                task.setStartOffset(lastOffset);
                task.setEndOffset(newOffset);

                lastOffset = newOffset + 1;
            }
        });
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
            projectStartDate,
            this.tasks.stream().filter(x -> !x.isCompleted()).collect(Collectors.toList()),
            vacations
        );
    }

    public Project hideNotCompleted() {
        return new Project(
            projectStartDate,
            this.tasks.stream().filter(x -> x.isCompleted()).collect(Collectors.toList()),
            vacations
        );
    }

    public Project onlyShowTaskForUser(String user) {
        return new Project(
            projectStartDate,
            this.tasks.stream().filter(x -> x.getOwner().equals(user)).collect(Collectors.toList()),
            vacations
        );
    }

    public Project filterKeyword(String keyword) {
        return filterKeyword(keyword, false);
    }

    public Project filterKeywords(List<String> keywords, boolean reverse) {
        Project ret = this;
        for (String keyword : keywords) {
            ret = ret.filterKeyword(keyword, reverse);
        }

        return ret;
    }

    public Project filterKeyword(String keyword, boolean reverse) {
        return new Project(
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
}