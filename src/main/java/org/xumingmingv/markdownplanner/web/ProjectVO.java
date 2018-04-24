package org.xumingmingv.markdownplanner.web;

import java.util.List;
import java.util.Map;

import lombok.Data;
import org.xumingmingv.markdownplanner.model.UserStat;

@Data
public class ProjectVO {
    private List<TaskVO> tasks;
    private List<String> men;
    private int totalManDays;
    private String projectStartDate;
    private String projectEndDate;
    /** 项目统计信息 */
    private Map<String, UserStat> userStats;
    private TaskVO rootTask;

    public TaskVO getRootTask() {
        return this.tasks.stream()
            .filter(t -> t.getId() == 0)
            .findFirst().get();
    }

    public UserStat getUserStat(String user) {
        return userStats.get(user);
    }

    /**
     * 获取整个项目的统计信息
     * @return
     */
    public UserStat getTotalStat() {
        UserStat totalStat = new UserStat();
        totalStat.setUser("-- 总记 --");
        List<String> users = getMen();
        users.stream()
            .forEach(user -> {
                UserStat stat = getUserStat(user);
                totalStat.addTotalCost(stat.getTotalCost());
                totalStat.addFinishedCost(stat.getFinishedCost());
            });

        return totalStat;
    }
}
