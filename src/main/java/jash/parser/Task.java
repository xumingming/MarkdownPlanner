package jash.parser;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Task {
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
        return getProgress() * getCost() / 100;
    }

    public JashDate getStartDate() {
        return new HalfDayDuration(startOffset).addToDate(projectStartDate);
    }

    public JashDate getEndDate() {
        return new HalfDayDuration(endOffset).addToDate(projectStartDate);
    }

    public boolean isDelayed() {
        return progress < 100 && LocalDate.now().compareTo(getEndDate().getDate()) > 0;
    }

    public boolean isCompleted() {
        return progress == 100;
    }

    public String getName() {
        return this.name;
    }

    public boolean isComposite() {
        return false;
    }
}