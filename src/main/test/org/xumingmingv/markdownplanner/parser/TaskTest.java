package org.xumingmingv.markdownplanner.parser;

import java.time.LocalDate;

import org.xumingmingv.markdownplanner.model.Header;
import org.xumingmingv.markdownplanner.model.HalfDayPrecisionDate;
import org.xumingmingv.markdownplanner.model.task.CompositeTask;
import org.xumingmingv.markdownplanner.model.task.Task;
import org.xumingmingv.markdownplanner.model.task.AtomicTask;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TaskTest {
    @Test
    public void getFinishedCost() throws Exception {
        AtomicTask task = new AtomicTask("test", "james", 10, 40);
        assertEquals(4, task.getFinishedCost(), 0.1);

        task = new AtomicTask("test", "james", 1, 40);
        assertEquals(0.4, task.getFinishedCost(), 0.01);
    }

    @Test
    public void getExpectedProgress() throws Exception {
        AtomicTask task = new AtomicTask("test", "james", 10, 40);
        task.setUsedCost(3);
        assertEquals(30, task.getExpectedProgress());

        task = new AtomicTask("test", "james", 5, 40);
        task.setUsedCost(2);
        assertEquals(40, task.getExpectedProgress());
    }

    @Test
    public void isComposite() throws Exception {
        AtomicTask task = new AtomicTask("test", "james", 10);
        assertFalse(task.isComposite());

        Task compositeTask = new CompositeTask(Header.create(), "composite");
        assertTrue(compositeTask.isComposite());
    }

    @Test
    public void isCompleted() throws Exception {
        AtomicTask task = new AtomicTask("test", "james", 10, 100);
        assertTrue(task.isCompleted());

        task = new AtomicTask("test", "james", 10, 90);
        assertFalse(task.isCompleted());
    }

    @Test
    public void isStarted() throws Exception {
        AtomicTask task = new AtomicTask("test", "james", 10, 0);
        assertFalse(task.isStarted());

        task = new AtomicTask("test", "james", 10, 1);
        assertTrue(task.isStarted());
    }

    @Test
    public void isDelayed() throws Exception {
        AtomicTask task = new AtomicTask("test", "james", 10, 20);
        task.setUsedCost(1);
        assertFalse(task.isDelayed());

        task = new AtomicTask("test", "james", 10, 10);
        task.setUsedCost(1);
        assertFalse(task.isDelayed());

        task = new AtomicTask("test", "james", 10, 10);
        task.setUsedCost(2);
        assertTrue(task.isDelayed());
    }

    @Test
    public void getStartDate() throws Exception {
        AtomicTask task = new AtomicTask("test", "james", 10, 40);
        task.setProjectStartDate(LocalDate.of(2017, 10, 10));
        task.setStartOffset(2);
        assertEquals(
            new HalfDayPrecisionDate(LocalDate.of(2017, 10, 11)),
            task.getStartDate()
        );

        task = new AtomicTask("test", "james", 11, 40);
        task.setProjectStartDate(LocalDate.of(2017, 10, 10));
        task.setStartOffset(3);
        assertEquals(
            new HalfDayPrecisionDate(LocalDate.of(2017, 10, 11), true),
            task.getStartDate()
        );
    }

    @Test
    public void getEndDate() throws Exception {
        AtomicTask task = new AtomicTask("test", "james", 4, 40);
        task.setProjectStartDate(LocalDate.of(2017, 10, 10));
        task.setStartOffset(0);
        task.setEndOffset(4);
        assertEquals(
            new HalfDayPrecisionDate(LocalDate.of(2017, 10, 12)),
            task.getEndDate()
        );

        task = new AtomicTask("test", "james", 5, 40);
        task.setProjectStartDate(LocalDate.of(2017, 10, 10));
        task.setStartOffset(0);
        task.setEndOffset(5);
        assertEquals(
            new HalfDayPrecisionDate(LocalDate.of(2017, 10, 12), true),
            task.getEndDate()
        );
    }
}