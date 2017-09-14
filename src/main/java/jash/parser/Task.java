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
    /** 任务说属于的类别 */
    private Header header;
    private LocalDate projectStartDate;
    /** task name */
    private String name;
    /** task owner */
    private String owner;
    /** half man days */
    private int cost;
    /** progress */
    private int progress;
    /** offset from ProjectStartDate(Unit: HalfDay) */
    private int startOffset;
    private int endOffset;

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
        return progress * cost / 100;
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
        return header.getDisplay() + " :: " + this.name;
    }
}