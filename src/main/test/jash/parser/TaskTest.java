package jash.parser;

import java.time.LocalDate;

import jash.parser.CompositeTask;
import jash.parser.Header;
import jash.parser.JashDate;
import jash.parser.Task;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TaskTest {
    @Test
    public void getFinishedCost() throws Exception {
        Task task = new Task("test", "james", 10, 40);
        assertEquals(4, task.getFinishedCost(), 0.1);

        task = new Task("test", "james", 1, 40);
        assertEquals(0.4, task.getFinishedCost(), 0.01);
    }

    @Test
    public void getExpectedProgress() throws Exception {
        Task task = new Task("test", "james", 10, 40);
        task.setUsedCost(3);
        assertEquals(30, task.getExpectedProgress());

        task = new Task("test", "james", 5, 40);
        task.setUsedCost(2);
        assertEquals(40, task.getExpectedProgress());
    }

    @Test
    public void isComposite() throws Exception {
        Task task = new Task("test", "james", 10);
        assertFalse(task.isComposite());

        Task compositeTask = new CompositeTask(Header.create(), "composite");
        assertTrue(compositeTask.isComposite());
    }

    @Test
    public void isCompleted() throws Exception {
        Task task = new Task("test", "james", 10, 100);
        assertTrue(task.isCompleted());

        task = new Task("test", "james", 10, 90);
        assertFalse(task.isCompleted());
    }

    @Test
    public void isStarted() throws Exception {
        Task task = new Task("test", "james", 10, 0);
        assertFalse(task.isStarted());

        task = new Task("test", "james", 10, 1);
        assertTrue(task.isStarted());
    }

    @Test
    public void isDelayed() throws Exception {
        Task task = new Task("test", "james", 10, 20);
        task.setUsedCost(1);
        assertFalse(task.isDelayed());

        task = new Task("test", "james", 10, 10);
        task.setUsedCost(1);
        assertFalse(task.isDelayed());

        task = new Task("test", "james", 10, 10);
        task.setUsedCost(2);
        assertTrue(task.isDelayed());
    }

    @Test
    public void getStartDate() throws Exception {
        Task task = new Task("test", "james", 10, 40);
        task.setProjectStartDate(LocalDate.of(2017, 10, 10));
        task.setStartOffset(2);
        assertEquals(
            new JashDate(LocalDate.of(2017, 10, 11)),
            task.getStartDate()
        );

        task = new Task("test", "james", 11, 40);
        task.setProjectStartDate(LocalDate.of(2017, 10, 10));
        task.setStartOffset(3);
        assertEquals(
            new JashDate(LocalDate.of(2017, 10, 11), true),
            task.getStartDate()
        );
    }

    @Test
    public void getEndDate() throws Exception {
        Task task = new Task("test", "james", 4, 40);
        task.setProjectStartDate(LocalDate.of(2017, 10, 10));
        task.setStartOffset(0);
        task.setEndOffset(4);
        assertEquals(
            new JashDate(LocalDate.of(2017, 10, 12)),
            task.getEndDate()
        );

        task = new Task("test", "james", 5, 40);
        task.setProjectStartDate(LocalDate.of(2017, 10, 10));
        task.setStartOffset(0);
        task.setEndOffset(5);
        assertEquals(
            new JashDate(LocalDate.of(2017, 10, 12), true),
            task.getEndDate()
        );
    }
}