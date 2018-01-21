package jash;

import java.time.LocalDate;

import com.google.common.collect.Lists;
import jash.parser.Project;
import jash.parser.Task;
import jash.parser.Vacation;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProjectTest {
    @Test
    public void getMen() throws Exception {
        Project project = new Project(
            "hello",
            LocalDate.of(2017, 10, 10),
            Lists.newArrayList(
                new Task("hello", "james", 1, 1),
                new Task("hello", "bond", 1, 1),
                new Task("hello", "james", 1, 1),
                new Task("hello", "lucy", 1, 1)
            ),
            Lists.newArrayList()
        );

        assertEquals(
            Lists.newArrayList("james", "bond", "lucy"),
            project.getMen()
        );
    }

    @Test
    public void isInVacation() throws Exception {
        Project project = new Project(
            LocalDate.of(2017, 10, 10),
            Lists.newArrayList(),
            Lists.newArrayList(
                new Vacation(
                    "james",
                    LocalDate.of(2017, 10, 10),
                    LocalDate.of(2017, 10, 11)
                ),
                new Vacation(
                    "bond",
                    LocalDate.of(2017, 10, 12),
                    LocalDate.of(2017, 10, 12)
                ),
                new Vacation(
                    "james",
                    LocalDate.of(2017, 10, 13),
                    LocalDate.of(2017, 10, 13)
                )
            )
        );

        assertTrue(project.isInVacation("james", LocalDate.of(2017, 10, 10)));
        assertTrue(project.isInVacation("james", LocalDate.of(2017, 10, 11)));
        assertFalse(project.isInVacation("james", LocalDate.of(2017, 10, 12)));
        assertTrue(project.isInVacation("bond", LocalDate.of(2017, 10, 12)));
        assertTrue(project.isInVacation("james", LocalDate.of(2017, 10, 13)));
    }

    @Test
    public void testIsWeekend() throws Exception {
        Project project = new Project(
            LocalDate.of(2017, 10, 10),
            Lists.newArrayList(),
            Lists.newArrayList()
        );

        assertFalse(project.isWeekend(LocalDate.parse("2017-09-08")));
        assertTrue(project.isWeekend(LocalDate.parse("2017-09-09")));
        assertTrue(project.isWeekend(LocalDate.parse("2017-09-10")));
        assertFalse(project.isWeekend(LocalDate.parse("2017-09-11")));
    }

    @Test
    public void testCalculateOffset() throws Exception {
        Project project = new Project(
            LocalDate.of(2017, 9, 11),
            Lists.newArrayList(),
            Lists.newArrayList(
                new Vacation(
                    "james",
                    LocalDate.of(2017, 9, 11),
                    LocalDate.of(2017, 9, 12)
                ),
                new Vacation(
                    "james",
                    LocalDate.of(2017, 9, 13),
                    LocalDate.of(2017, 9, 14)
                ),
                new Vacation(
                    "james",
                    LocalDate.of(2017, 9, 18),
                    LocalDate.of(2017, 9, 18)
                )
            )
        );

        assertEquals(
            18,
            project.calculateEndOffset(0, 5, "james")
        );
    }

    @Test
    public void testInit() throws Exception {
        Project project = new Project(
            LocalDate.of(2017, 9, 11),
            Lists.newArrayList(
                new Task("task1", "james", 5, 10),
                new Task("task2", "bond", 5, 100),
                new Task("task3", "james", 2, 100),
                new Task("task4", "bond", 2, 100)
            ),
            Lists.newArrayList(
                new Vacation(
                    "james",
                    LocalDate.of(2017, 9, 11),
                    LocalDate.of(2017, 9, 12)
                ),
                new Vacation(
                    "james",
                    LocalDate.of(2017, 9, 13),
                    LocalDate.of(2017, 9, 14)
                ),
                new Vacation(
                    "james",
                    LocalDate.of(2017, 9, 18),
                    LocalDate.of(2017, 9, 18)
                )
            )
        );

        assertEquals(4, project.getTasks().size());

        // projectStartDate are all set
        for (Task task : project.getTasks()) {
            assertEquals(LocalDate.of(2017, 9, 11), task.getProjectStartDate());
        }

        // 0
        assertEquals(0, project.getTasks().get(0).getStartOffset());
        assertEquals(18, project.getTasks().get(0).getEndOffset());

        // 1
        assertEquals(0, project.getTasks().get(1).getStartOffset());
        assertEquals(4, project.getTasks().get(1).getEndOffset());

        // 2
        assertEquals(19, project.getTasks().get(2).getStartOffset());
        assertEquals(20, project.getTasks().get(2).getEndOffset());

        // 3
        assertEquals(5, project.getTasks().get(3).getStartOffset());
        assertEquals(6, project.getTasks().get(3).getEndOffset());
    }
}