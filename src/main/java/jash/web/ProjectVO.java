package jash.web;

import java.util.List;

import jash.model.ProjectStat;
import jash.model.ProjectStat.UserStat;
import jash.model.task.CompositeTask;
import jash.model.task.Task;
import lombok.Data;

@Data
public class ProjectVO {
    private List<TaskVO> tasks;
    private List<String> men;
    private int totalManDays;
    private String projectStartDate;
    private String projectEndDate;
    /** 项目统计信息 */
    private ProjectStat stat;
    private TaskVO rootTask;

    public TaskVO getRootTask() {
        return this.tasks.stream()
            .filter(t -> t.getId() == 0)
            .findFirst().get();
    }

    public UserStat getUserStat(String user) {
        return stat.getUserStat(user);
    }

    public UserStat getTotalStat() {
        return stat.getTotalStat();
    }
}
