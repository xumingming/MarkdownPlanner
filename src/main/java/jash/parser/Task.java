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
        this.name = name;
        this.owner = owner;
        this.cost = cost;
        this.progress = progress;
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
}