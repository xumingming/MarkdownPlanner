package jash.model;

import java.time.LocalDate;

import lombok.Value;

@Value
public class Vacation {
    private String user;
    private LocalDate startDate;
    private LocalDate endDate;

    public boolean contains(LocalDate date) {
        return startDate.compareTo(date) <= 0 &&
            endDate.compareTo(date) >=0;
    }
}