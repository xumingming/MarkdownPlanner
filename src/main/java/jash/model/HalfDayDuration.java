package jash.model;

import java.time.LocalDate;

import lombok.Getter;

/**
 * Duration calculated using time unit of {@code Half of a Day}, i.e. 12 HOURs.
 */
@Getter
public class HalfDayDuration {
    private int count;

    public HalfDayDuration(int count) {
        this.count = count;
    }

    public HalfDayDuration plus(int amountToAdd) {
        return new HalfDayDuration(this.count + amountToAdd);
    }

    public HalfDayDuration plusDay(int amountToAdd) {
        return new HalfDayDuration(this.count + amountToAdd * 2);
    }

    public JashDate addToDate(LocalDate fromDate) {
        int days = count / 2;
        int hours = (days * 2 == count) ? 0 : 12;
        return new JashDate(fromDate.plusDays(days), hours > 0);
    }
}