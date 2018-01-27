package org.xumingmingv.markdownplanner.parser;

import java.time.LocalDate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.xumingmingv.markdownplanner.model.Header;
import org.xumingmingv.markdownplanner.model.Project;
import org.xumingmingv.markdownplanner.model.Vacation;
import org.xumingmingv.markdownplanner.model.task.CompositeTask;
import org.xumingmingv.markdownplanner.model.task.Task;
import org.xumingmingv.markdownplanner.model.task.AtomicTask;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * {@link Project}的单元测试。
 */
public class ProjectTest {
    @Test
    public void getMen() throws Exception {
        Project project = new Project(
            "hello",
            LocalDate.of(2017, 10, 10),
            Lists.newArrayList(
                new AtomicTask("hello", "james", 1, 1),
                new AtomicTask("hello", "bond", 1, 1),
                new AtomicTask("hello", "james", 1, 1),
                new AtomicTask("hello", "lucy", 1, 1)
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
    public void testInitAtomicTasks() throws Exception {
        Project project = new Project(
            LocalDate.of(2017, 9, 11),
            Lists.newArrayList(
                new AtomicTask("task1", "james", 5, 10),
                new AtomicTask("task2", "bond", 5, 100),
                new AtomicTask("task3", "james", 2, 100),
                new AtomicTask("task4", "bond", 2, 100)
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

    @Test
    public void testCalculateActualCost() {
        Project project = new Project(
            LocalDate.of(2017, 9, 11),
            ImmutableList.of(),
            Lists.newArrayList(
                new Vacation(
                    "james",
                    LocalDate.of(2017, 9, 11),   // 周一
                    LocalDate.of(2017, 9, 12)    // 周二
                ),
                new Vacation(
                    "james",
                    LocalDate.of(2017, 9, 14),   // 周四
                    LocalDate.of(2017, 9, 15)    // 周五
                ),
                new Vacation(
                    "james",
                    LocalDate.of(2017, 9, 18),   // 星期一
                    LocalDate.of(2017, 9, 18)
                )
            )
        );

        int actualCost = project.calculateActualCost(
            "james",
            LocalDate.of(2017, 9, 11),
            LocalDate.of(2017, 9, 12)
        );
        assertEquals(0, actualCost);

        actualCost = project.calculateActualCost(
            "james",
            LocalDate.of(2017, 9, 11),
            LocalDate.of(2017, 9, 13)
        );
        assertEquals(2, actualCost);

        actualCost = project.calculateActualCost(
            "james",
            LocalDate.of(2017, 9, 11),
            LocalDate.of(2017, 9, 14)
        );
        assertEquals(2, actualCost);

        actualCost = project.calculateActualCost(
            "james",
            LocalDate.of(2017, 9, 11),
            LocalDate.of(2017, 9, 15)
        );
        assertEquals(2, actualCost);

        actualCost = project.calculateActualCost(
            "james",
            LocalDate.of(2017, 9, 11),
            LocalDate.of(2017, 9, 16)
        );
        assertEquals(2, actualCost);

        actualCost = project.calculateActualCost(
            "james",
            LocalDate.of(2017, 9, 11),
            LocalDate.of(2017, 9, 17)
        );
        assertEquals(2, actualCost);

        actualCost = project.calculateActualCost(
            "james",
            LocalDate.of(2017, 9, 11),
            LocalDate.of(2017, 9, 18)
        );
        assertEquals(2, actualCost);

        actualCost = project.calculateActualCost(
            "james",
            LocalDate.of(2017, 9, 11),
            LocalDate.of(2017, 9, 19)
        );
        assertEquals(4, actualCost);

        actualCost = project.calculateActualCost(
            "james",
            LocalDate.of(2017, 9, 19),
            LocalDate.of(2017, 9, 19)
        );
        assertEquals(2, actualCost);
    }

    @Test
    public void testGetTotalCost() {
        // 没有“复合”任务的
        Project project = new Project(
            LocalDate.of(2017, 9, 11),
            ImmutableList.of(
                new AtomicTask("task1", "james", 5),
                new AtomicTask("task2", "bond", 5),
                new AtomicTask("task3", "james", 2),
                new AtomicTask("task4", "bond", 2)
            )
        );

        assertEquals(14, project.getTotalCost());

        // 有复合任务的
        CompositeTask compositeTask = new CompositeTask(Header.create(), "all");
        compositeTask.addOwnerCost("james", 50, 5);
        compositeTask.addOwnerCost("bond", 50, 3);

        project = new Project(
            LocalDate.of(2017, 9, 11),
            ImmutableList.of(
                new AtomicTask("task1", "james", 5),
                new AtomicTask("task2", "bond", 5),
                new AtomicTask("task3", "james", 2),
                new AtomicTask("task4", "bond", 2),
                compositeTask
            )
        );
        assertEquals(14, project.getTotalCost());
    }

    @Test
    public void testGetFinishedCost() {
        // 没有“复合”任务的
        Project project = new Project(
            LocalDate.of(2017, 9, 11),
            ImmutableList.of(
                new AtomicTask("task1", "james", 5, 50),
                new AtomicTask("task2", "bond", 5, 50),
                new AtomicTask("task3", "james", 2, 50),
                new AtomicTask("task4", "bond", 2, 50)
            )
        );

        assertEquals(7, project.getFinishedCost(), 0.1);

        // 有复合任务的
        CompositeTask compositeTask = new CompositeTask(Header.create(), "all");
        compositeTask.addOwnerCost("james", 50, 25);
        compositeTask.addOwnerCost("bond", 50, 25);

        project = new Project(
            LocalDate.of(2017, 9, 11),
            ImmutableList.of(
                new AtomicTask("task1", "james", 5, 50),
                new AtomicTask("task2", "bond", 5, 50),
                new AtomicTask("task3", "james", 2, 50),
                new AtomicTask("task4", "bond", 2, 50),
                compositeTask
            )
        );
        assertEquals(7, project.getFinishedCost(), 0.1);
    }
}