package org.xumingmingv.markdownplanner.model;

import java.time.LocalDate;
import java.util.List;

import org.xumingmingv.markdownplanner.model.ProjectStat.UserStat;
import org.xumingmingv.markdownplanner.model.task.CompositeTask;
import org.xumingmingv.markdownplanner.model.task.Task;

public interface IProject {
    /**
     * 项目的名字
     * @return
     */
    String getName();
    /**
     * 获取项目开始时间
     * @return
     */
    LocalDate getProjectStartDate();

    /**
     * 获取项目结束时间
     * @return
     */
    LocalDate getProjectEndDate();

    /**
     * 获取参与项目的所有人员
     * @return
     */
    List<String> getMen();

    /**
     * 获取整个项目的统计信息
     * @return
     */
    UserStat getTotalStat();

    /**
     * 获取每个人的统计信息
     * @param user
     * @return
     */
    UserStat getUserStat(String user);

    /**
     * 获取根任务
     * @return
     */
    CompositeTask getRootTask();

    /**
     * 获取项目里面的所有任务
     * @return
     */
    List<Task> getTasks();

    /**
     * 获取所有的请假安排
     * @return
     */
    List<Vacation> getVacations();

    ProjectStat getStat();
}
