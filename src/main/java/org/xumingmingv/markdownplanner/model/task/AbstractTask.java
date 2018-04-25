package org.xumingmingv.markdownplanner.model.task;

import java.time.LocalDate;

import org.xumingmingv.markdownplanner.model.Header;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractTask implements Task {
    /** 任务的ID */
    protected Integer id;
    /** 父任务的ID */
    protected Integer parentId = 0;
    /** 任务说属于的类别 */
    protected Header header;
    /** 项目开始时间 */
    protected LocalDate projectStartDate;
    /** task name */
    protected String name;
    /** offset from ProjectStartDate(Unit: HalfDay) */
    protected int startOffset;
    protected int endOffset;
    /** 已经消耗掉的Cost */
    protected int usedCost;
}
