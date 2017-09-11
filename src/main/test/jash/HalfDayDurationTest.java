package jash;

import java.time.LocalDate;

import jash.parser.HalfDayDuration;
import jash.parser.JashDate;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit Test of {@link HalfDayDuration}.
 */
public class HalfDayDurationTest {
    @Test
    public void plus() throws Exception {
        HalfDayDuration d = new HalfDayDuration(10);
        assertEquals(11, d.plus(1).getCount());
    }

    @Test
    public void plusDay() throws Exception {
        HalfDayDuration d = new HalfDayDuration(10);
        assertEquals(12, d.plusDay(1).getCount());
    }

    @Test
    public void addToDate() throws Exception {
        assertEquals(
            new JashDate(LocalDate.of(2017, 10, 15), false),
            new HalfDayDuration(10).addToDate(LocalDate.of(2017, 10, 10))
        );
    }
}