package org.xumingmingv.markdownplanner;

import java.time.LocalDate;

import org.xumingmingv.markdownplanner.model.HalfDayDuration;
import org.xumingmingv.markdownplanner.model.HalfDayPrecisionDate;
import org.junit.Assert;
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
        Assert.assertEquals(
            new HalfDayPrecisionDate(LocalDate.of(2017, 10, 15), false),
            new HalfDayDuration(10).addToDate(LocalDate.of(2017, 10, 10))
        );
    }
}