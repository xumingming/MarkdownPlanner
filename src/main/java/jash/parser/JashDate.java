package jash.parser;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class JashDate {
    private final LocalDate date;
    private final boolean hasHalfDay;

    public JashDate(LocalDate date) {
        this(date, false);
    }

    public JashDate(LocalDate date, boolean hasHalfDay) {
        this.date = date;
        this.hasHalfDay = hasHalfDay;
    }

    @Override
    public String toString() {
        return date.toString() + (hasHalfDay ? "/PM" : "/AM");
    }
}
