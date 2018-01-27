package org.xumingmingv.markdownplanner.model;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class HalfDayPrecisionDate {
    private final LocalDate date;
    private final boolean hasHalfDay;

    public HalfDayPrecisionDate(LocalDate date) {
        this(date, false);
    }

    public HalfDayPrecisionDate(LocalDate date, boolean hasHalfDay) {
        this.date = date;
        this.hasHalfDay = hasHalfDay;
    }

    @Override
    public String toString() {
        return date.toString() + (hasHalfDay ? "/PM" : "/AM");
    }
}
