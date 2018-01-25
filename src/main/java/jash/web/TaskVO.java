package jash.web;

import lombok.Data;

@Data
public class TaskVO {
    /** 唯一ID */
    private int id;
    /** 父任务Id */
    private int parentId;
    /** 是任务还是一个任务集合 */
    private boolean isComposite;
    private int cost;
    private String manDays;
    /** task name */
    private String name;
    /** task owner */
    private String owner;
    /** 开始时间 */
    private String startDate;
    /** 结束时间 */
    private String endDate;
    /** 进度 */
    private String progress;
    /** 进度 */
    private int rawProgress;
    /** 显示颜色 */
    private String bgColorClass;
    /** 行号 */
    private int lineNumber;
}
