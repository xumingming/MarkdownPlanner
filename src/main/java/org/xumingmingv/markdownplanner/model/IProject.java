package org.xumingmingv.markdownplanner.model;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.xumingmingv.markdownplanner.model.task.CompositeTask;
import org.xumingmingv.markdownplanner.model.task.Task;

/**
 * 项目
 */
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
    default LocalDate getProjectEndDate() {
        Optional<HalfDayPrecisionDate> ret = getTasks().stream()
            .map(Task::getEndDate)
            .max(Comparator.comparing(HalfDayPrecisionDate::getDate));

        return ret.isPresent() ? ret.get().getDate() : null;
    }

    /**
     * 获取参与项目的所有人员
     * @return
     */
    List<String> getMen();

    /**
     * 获取整个项目的统计信息
     * @return
     */
    default UserStat getTotalStat() {
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

    /**
     * 获取每个人的统计信息
     * @param user
     * @return
     */
    UserStat getUserStat(String user);

    /**
     *
     * @return
     */
    default Map<String, UserStat> getUserStats() {
        return getMen().stream()
            .map(x -> getUserStat(x))
            .collect(Collectors.toMap(UserStat::getUser, Function.identity()));
    }

    /**
     * 获取根任务
     * @return
     */
    default CompositeTask getRootTask() {
        return (CompositeTask) this.getTasks().stream()
            .filter(Task::isComposite)
            .filter(t -> t.getId() == 0)
            .findFirst().get();
    }

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

    /**
     * 隐藏掉已经完成的任务
     * @return
     */
    IProject hideCompleted();

    /**
     * 隐藏掉没有完成的任务
     * @return
     */
    IProject hideNotCompleted();

    /**
     * 过滤出指定用户的任务
     * @param user
     * @return
     */
    IProject filterUser(String user);

    /**
     * 过滤关键词
     * @param keywords
     * @param reverse
     * @return
     */
    IProject filterKeywords(List<String> keywords, boolean reverse);

    IProject filterKeyword(String keyword, boolean reverse);

    default IProject filterKeyword(String keyword) {
        return filterKeyword(keyword, false);
    }
}
