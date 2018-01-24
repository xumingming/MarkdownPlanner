package jash.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.LocalDate;

import com.google.common.collect.Lists;
import jash.model.Header;
import jash.model.LeveledHeader;
import jash.model.Project;
import jash.model.Vacation;
import jash.model.task.AtomicTask;
import org.junit.Test;

/**
 * Unit Test for {@link Parser}.
 *
 * @author xumingmingv
 */
public class ParserTest {
  private Parser parser = new Parser();

  @Test
  public void testParseTaskLine_withOnlyManDays() {
    String str = "* this is a task -- 0.5";

    AtomicTask expectedTask = new AtomicTask("this is a task", "TODO", 1, 0);
    AtomicTask actualTask = parser.parseTaskLine(str);
    assertEquals(expectedTask, actualTask);
  }

  @Test
  public void testParseTaskLine_withManDaysAndOwner() {
    String str = "* this is a task -- 1[james]";
    AtomicTask expectedTask = new AtomicTask("this is a task", "james", 2, 0);
    AtomicTask actualTask = parser.parseTaskLine(str);
    assertEquals(expectedTask, actualTask);
  }

  @Test
  public void testParseTaskLine_full() {
    String str = "* this is a task -- 1[james][10%]";
    AtomicTask expectedTask = new AtomicTask("this is a task", "james", 2, 10);
    AtomicTask actualTask = parser.parseTaskLine(str);
    assertEquals(expectedTask, actualTask);
  }

  @Test
  public void testParseProjectStartDate() throws Exception {
    String str = "* ProjectStartDate: 2011-01-02";
    assertEquals(
        LocalDate.parse("2011-01-02"),
        parser.parseProjectStartDate(str)
    );
  }

  @Test
  public void testParseHeader() throws Exception {
    String str = "## hello world ";
    assertEquals(
        new LeveledHeader(1, "hello world"),
        parser.parseHeader(str)
    );
    str = "### hello world ";
    assertEquals(
        new LeveledHeader(2, "hello world"),
        parser.parseHeader(str)
    );
  }

  @Test
  public void testParseVacation() throws Exception {
    String str = "* james -- 1986-09-09 - 1986-09-10";
    assertEquals(
        new Vacation(
            "james",
            LocalDate.parse("1986-09-09"),
            LocalDate.parse("1986-09-10")
        ),
        parser.parseVacation(str)
    );
  }

  @Test
  public void testAddDuration() throws Exception {
    Duration d = Duration.ZERO;
    assertEquals(Duration.ofHours(12), parser.addDuration(d, 1));
    assertEquals(Duration.ofHours(24), parser.addDuration(d, 2));
    assertEquals(Duration.ofHours(36), parser.addDuration(d, 3));
  }

  @Test
  public void testParse() throws Exception {
    String content = "# hello\n"
        + "* ProjectStartDate: 2017-09-11\n"
        + "* james -- 2017-09-11 - 2017-09-12\n"
        + "* james -- 2017-09-13 - 2017-09-14\n"
        + "* james -- 2017-09-18 - 2017-09-18\n"
        + "# 任务细分\n"
        + "* task1 -- 2.5[james][50%]\n"
        + "## A\n"
        + "* task2 -- 2.5[bond]\n"
        + "### B\n"
        + "* task3 -- 1[james]\n"
        + "## C\n"
        + "* task4 -- 1[bond]";

    Parser parser = new Parser();
    Project project = parser.parse(content);

    LocalDate expectedProjectStartDate = LocalDate.of(2017, 9, 11);
    Project expectedProject = new Project(
        "hello",
        LocalDate.of(2017, 9, 11),
        Lists.newArrayList(
            new AtomicTask(Header.create("任务细分"), expectedProjectStartDate, "task1", "james", 5, 50, 0, 18),
            new AtomicTask(Header.create("任务细分", "A"), expectedProjectStartDate, "task2", "bond", 5, 0, 0, 4),
            new AtomicTask(Header.create("任务细分", "A", "B"), expectedProjectStartDate, "task3", "james", 2, 0, 19, 20),
            new AtomicTask(Header.create("任务细分", "C"), expectedProjectStartDate, "task4", "bond", 2, 0, 5, 6)
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

    assertEquals(expectedProject, project);
  }
}
