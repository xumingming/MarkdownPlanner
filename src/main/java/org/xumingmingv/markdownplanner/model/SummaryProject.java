package org.xumingmingv.markdownplanner.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.xumingmingv.markdownplanner.model.ProjectStat.UserStat;
import org.xumingmingv.markdownplanner.model.task.CompositeTask;
import org.xumingmingv.markdownplanner.model.task.Task;

public class SummaryProject implements IProject {
    private List<Project> projects = new ArrayList<>();

    public static final String NAME = "__总计划__";
    public static final String FILE_NAME = "__summary__.plan.md";

    public static boolean isSummaryProject(String filePath) {
        return filePath.endsWith(FILE_NAME);
    }

    public SummaryProject() {

    }

    @Override
    public String getName() {
        return NAME;
    }

    public void addProject(Project project) {
        projects.add(project);
        projects.sort(Comparator.comparing(Project::getName));
    }

    @Override
    public LocalDate getProjectStartDate() {
        return projects.stream()
            .map(IProject::getProjectStartDate)
            .min(Comparator.naturalOrder())
            .get();
    }

    @Override
    public LocalDate getProjectEndDate() {
        Optional<HalfDayPrecisionDate> ret = this.getTasks().stream()
            .map(Task::getEndDate)
            .max(Comparator.comparing(HalfDayPrecisionDate::getDate));

        return ret.isPresent() ? ret.get().getDate() : null;
    }

    @Override
    public List<String> getMen() {
        return projects.stream()
            .map(IProject::getMen)
            .flatMap(List::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    @Override
    public UserStat getTotalStat() {
        UserStat ret = new UserStat();

        projects.stream()
            .map(IProject::getTotalStat)
            .forEach(stat -> {
                ret.addTotalCost(stat.getTotalCost());
                ret.addFinishedCost(stat.getFinishedCost());
            });

        ret.setUser("-- 总计 --");
        return ret;
    }

    @Override
    public UserStat getUserStat(String user) {
        UserStat ret = new UserStat();

        projects.stream()
            .map(p -> p.getUserStat(user))
            .forEach(stat -> {
                ret.addTotalCost(stat.getTotalCost());
                ret.addFinishedCost(stat.getFinishedCost());
            });

        ret.setUser(user);
        return ret;
    }

    @Override
    public CompositeTask getRootTask() {
        return (CompositeTask) this.getTasks().stream()
            .filter(Task::isComposite)
            .filter(t -> t.getId() == 0)
            .findFirst().get();
    }

    @Override
    public List<Task> getTasks() {
        List<Task> ret = new ArrayList<>();
        int projectIdx = 1;
        List<Task> topTasks = new ArrayList<>();

        CompositeTask rootTask = new CompositeTask(Header.create(), NAME, getProjectStartDate());
        rootTask.setId(0);
        rootTask.setParentId(-1);

        for (Project project : projects) {
            for (Task task : project.getTasks()) {
                int origId = task.getId();
                // 重新分配ID: 每个任务的ID都加上1000 * projectIdx
                task.setId(task.getId() + projectIdx * 1000);
                task.setParentId(task.getParentId() + projectIdx * 1000);

                if (origId == 0) {
                    task.setParentId(0);
                    topTasks.add(task);
                }
                ret.add(task);

                if (!(task instanceof CompositeTask)) {
                    rootTask.addOwnerCost(task.getOwner(), task.getCost(), task.getFinishedCost());
                }
            }

            projectIdx++;
        }

        ret.add(0, rootTask);

        return ret;
    }

    @Override
    public List<Vacation> getVacations() {
        List<Vacation> ret = new ArrayList<>();
        projects.forEach(p -> ret.addAll(p.getVacations()));

        return ret;
    }

    @Override
    public ProjectStat getStat() {
        Map<String, UserStat> userStatMap = new HashMap<>();
        this.projects.forEach(p -> {
            Map<String, UserStat> tmpUserStatMap = p.getStat().getUserStatMap();
            for (String user : tmpUserStatMap.keySet()) {
                UserStat currentUserStat = tmpUserStatMap.get(user);
                if (!userStatMap.containsKey(user)) {
                    userStatMap.put(user, new UserStat());
                }

                UserStat userStat = userStatMap.get(user);
                userStat.addTotalCost(currentUserStat.getTotalCost());
                userStat.addFinishedCost(currentUserStat.getFinishedCost());
            }
        });
        return new ProjectStat(userStatMap);
    }
}
