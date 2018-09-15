package org.xumingmingv.markdownplanner.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.xumingmingv.markdownplanner.model.task.CompositeTask;
import org.xumingmingv.markdownplanner.model.task.Task;

public class SummaryProject implements IProject {
    private List<IProject> projects = new ArrayList<>();

    public static final String NAME = "__总计划__";
    public static final String FILE_NAME = "__summary__.plan.md";

    public static boolean isSummaryProject(String filePath) {
        return filePath.endsWith(FILE_NAME);
    }

    public SummaryProject() {}

    public SummaryProject(List<IProject> projects) {
        projects.forEach(p -> addProject(p));
    }

    @Override
    public String getName() {
        return NAME;
    }

    public void addProject(IProject project) {
        projects.add(project);
        projects.sort(Comparator.comparing(IProject::getProjectStartDate));
    }

    @Override
    public LocalDate getProjectStartDate() {
        Optional<LocalDate> ret = projects.stream()
            .map(IProject::getProjectStartDate)
            .min(Comparator.naturalOrder());

        return ret.isPresent() ? ret.get() : null;
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
    public UserStat getUserStat(String user) {
        UserStat ret = new UserStat();

        projects.stream()
            .map(p -> p.getUserStat(user))
            .filter(x -> x != null)
            .forEach(stat -> {
                ret.addTotalCost(stat.getTotalCost());
                ret.addFinishedCost(stat.getFinishedCost());
            });

        ret.setUser(user);
        return ret;
    }

    @Override
    public List<Task> getTasks() {
        List<Task> ret = new ArrayList<>();
        int projectIdx = 1;

        CompositeTask rootTask = new CompositeTask(Header.create(), NAME, getProjectStartDate());
        rootTask.setId(0);
        rootTask.setParentId(-1);

        Integer rootStart = 0;
        Integer rootEnd = 0;

        for (IProject project : projects) {
            for (Task task : project.getTasks()) {
                int origId = task.getId();
                // 重新分配ID: 每个任务的ID都加上1000 * projectIdx
                task.setId(task.getId() + projectIdx * 1000);
                task.setParentId(task.getParentId() + projectIdx * 1000);

                if (origId == 0) {
                    task.setParentId(0);
                }
                ret.add(task);

                if (!(task instanceof CompositeTask)) {
                    rootTask.addOwnerCost(task.getOwner(), task.getCost(), task.getFinishedCost());
                }

                if (rootStart == 0 || task.getStartOffset() < rootStart) {
                    rootStart = task.getStartOffset();
                }

                if (task.getEndOffset() > rootEnd) {
                    rootEnd = task.getEndOffset();
                }
            }

            projectIdx++;
        }

        rootTask.setStartOffset(rootStart);
        rootTask.setEndOffset(rootEnd);

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
    public IProject filterUser(String user) {
        return new SummaryProject(this.projects.stream()
            .map(p -> p.filterUser(user))
            .collect(Collectors.toList())
        );
    }

    @Override
    public IProject filterKeyword(String keyword, boolean reverse) {
        return new SummaryProject(this.projects.stream()
            .map(p -> p.filterKeyword(keyword, reverse))
            .collect(Collectors.toList())
        );
    }

    @Override
    public IProject filterKeywords(List<String> keywords, boolean reverse) {
        return new SummaryProject(this.projects.stream()
            .map(p -> p.filterKeywords(keywords, reverse))
            .collect(Collectors.toList())
        );
    }

    @Override
    public IProject hideCompleted() {
        return new SummaryProject(this.projects.stream()
            .map(p -> p.hideCompleted())
            .collect(Collectors.toList())
        );
    }

    @Override
    public IProject hideNotCompleted() {
        return new SummaryProject(this.projects.stream()
            .map(p -> p.hideNotCompleted())
            .collect(Collectors.toList())
        );
    }
}
