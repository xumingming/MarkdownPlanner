package org.xumingmingv.markdownplanner.model.task;

import java.time.LocalDate;

import org.xumingmingv.markdownplanner.model.HalfDayDuration;
import org.xumingmingv.markdownplanner.model.Header;
import org.xumingmingv.markdownplanner.model.HalfDayPrecisionDate;

public interface Task {
    /**
     * 任务的ID
     * @return
     */
    int getId();

    /**
     * 设置ID
     */
    void setId(int id);

    /**
     * 父任务的ID
     * @return
     */
    int getParentId();

    /**
     * 设置父任务ID
     */
    void setParentId(int parentId);

    /**
     * 项目开始时间
     * @return
     */
    LocalDate getProjectStartDate();

    /**
     * 设置项目开始时间
     */
    void setProjectStartDate(LocalDate projectStartDate);

    /**
     * 这个任务所属的组
     * @return
     */
    Header getHeader();

    /**
     * 任务的名字
     * @return
     */
    String getName();

    /**
     * 任务的Owner
     * @return
     */
    String getOwner();

    /**
     * 任务需要的Cost
     * @return
     */
    int getCost();

    /**
     * 获取时间使用掉的时间
     * @return
     */
    int getUsedCost();

    /**
     * 设置useCost
     * @param usedCost
     */
    void setUsedCost(int usedCost);

    /**
     * 任务目前的进度
     * @return
     */
    int getProgress();

    /**
     * 任务开始节点
     */
    int getStartOffset();

    /**
     * 设置startOffset
     */
    void setStartOffset(int startOffset);

    /**
     * 设置endOffset
     */
    void setEndOffset(int endOffset);

    /**
     *  任务结束节点
     */
    int getEndOffset();

    /**
     * 是否是复合任务
     * @return
     */
    boolean isComposite();

    /**
     * 期望中的任务进度
     */
    default int getExpectedProgress() {
        double ret = 1.0 * getUsedCost() * 100 / getCost();

        int intRet = Double.valueOf(ret).intValue();
        return intRet > 100 ? 100 : intRet;
    }

    /**
     * 任务的属性是否已经完全设置好了
     */
    default boolean isFullyPopulated() {
        return getEndOffset() > 0;
    }

    /**
     * 任务是否已经开始
     */
    default boolean isStarted() {
        return getProgress() > 0;
    }

    /**
     * 任务是否有所延迟
     */
    default boolean isDelayed() {
        return getProgress() < getExpectedProgress();
    }

    /**
     * 任务是否已经完成
     */
    default boolean isCompleted() {
        return getProgress() == 100;
    }

    /**
     * 任务已经完成的进展
     */
    default double getFinishedCost() {
        return 1.0 * getProgress() * getCost() / 100;
    }

    /**
     * 任务的开始日期
     */
    default HalfDayPrecisionDate getStartDate() {
        return new HalfDayDuration(getStartOffset()).addToDate(getProjectStartDate());
    }

    /**
     * 任务的结束日期
     */
    default HalfDayPrecisionDate getEndDate() {
        return new HalfDayDuration(getEndOffset()).addToDate(getProjectStartDate());
    }
}
