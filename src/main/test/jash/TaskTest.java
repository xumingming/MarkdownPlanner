package jash;

import java.time.LocalDate;

import jash.parser.JashDate;
import jash.parser.Task;
import org.junit.Test;

import static org.junit.Assert.*;

public class TaskTest {
    @Test
    public void getFinishedCost() throws Exception {
        Task task = new Task("test", "james", 10, 40);
        assertEquals(4, task.getFinishedCost(), 0.1);
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
}