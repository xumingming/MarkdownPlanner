package jash.parser;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class Task implements ITask {
    protected int id;
    protected int parentId;
    /** 任务说属于的类别 */
    protected Header header;
    protected LocalDate projectStartDate;
    /** task name */
    protected String name;
    /** task owner */
    protected String owner;
    /** half man days */
    protected int cost;
    /** progress */
    protected int progress;
    /** offset from ProjectStartDate(Unit: HalfDay) */
    protected int startOffset;
    protected int endOffset;
    /** 已经消耗掉的Cost */
    protected int usedCost;

    public Task(Header header, LocalDate projectStartDate, String name, String owner, int cost, int progress, int startOffset, int endOffset) {
        this.header = header;
        this.projectStartDate = projectStartDate;
        this.name = name;
        this.owner = owner;
        this.cost = cost;
        this.progress = progress;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    public Task(String name, String owner, int cost) {
        this(name, owner, cost, 0);
    }

    public Task(String name, String owner, int cost, int progress) {
        this(null, name, owner, cost, progress, 0, 0);
    }

    public Task(LocalDate projectStartDate, String name, String owner, int cost, int progress,
        int startOffset, int endOffset) {
        this.projectStartDate = projectStartDate;
        this.name = name;
        this.owner = owner;
        this.cost = cost;
        this.progress = progress;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    public double getFinishedCost() {
        return 1.0 * getProgress() * getCost() / 100;
    }

    public JashDate getStartDate() {
        return new HalfDayDuration(startOffset).addToDate(projectStartDate);
    }

    public JashDate getEndDate() {
        return new HalfDayDuration(endOffset).addToDate(projectStartDate);
    }

    public boolean isStarted() {
        return getProgress() > 0;
    }

    public boolean isDelayed() {
        return getProgress() < getExpectedProgress();
    }

    public boolean isCompleted() {
        return getProgress() == 100;
    }

    public boolean isComposite() {
        return false;
    }

    public int getExpectedProgress() {
        double ret = 1.0 * getUsedCost() * 100 / getCost();

        int intRet = Double.valueOf(ret).intValue();
        return intRet > 100 ? 100 : intRet;
    }

    public boolean isFullyPopulated() {
        return this.endOffset > 0;
    }
}