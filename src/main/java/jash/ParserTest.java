package jash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import org.junit.Test;

public class ParserTest {
  private Parser parser = new Parser();

  @Test
  public void testParseTaskLine_withOnlyManDays() {
    String str = "* this is a task -- 1";

    Parser.Task expectedTask = new Parser.Task("this is a task", "TODO", 1, 0, Duration.ZERO);
    Parser.Task actualTask = parser.parseTaskLine(str);
    assertEquals(expectedTask, actualTask);
  }

  @Test
  public void testParseTaskLine_withManDaysAndOwner() {
    String str = "* this is a task -- 1[james]";
    Parser.Task expectedTask = new Parser.Task("this is a task", "james", 1, 0, Duration.ZERO);
    Parser.Task actualTask = parser.parseTaskLine(str);
    assertEquals(expectedTask, actualTask);
  }

  @Test
  public void testParseTaskLine_full() {
    String str = "* this is a task -- 1[james][10%]";
    Parser.Task expectedTask = new Parser.Task("this is a task", "james", 1, 10, Duration.ZERO);
    Parser.Task actualTask = parser.parseTaskLine(str);
    assertEquals(expectedTask, actualTask);
  }

  @Test
  public void testParseProjectStartDate() throws Exception {
    String str = "* ProjectStartDate: 2011-01-02";
    assertEquals(
        parseDate("2011-01-02"),
        parser.parseProjectStartDate(str)
    );
  }

  private Date parseDate(String str) throws ParseException {
    return new SimpleDateFormat("yyyy-MM-dd").parse(str);
  }

  private LocalDate parseLocalDate(String str) throws ParseException {
    return LocalDate.parse(str);
  }

  @Test
  public void testParseHeader_2Sharp() throws Exception {
    String str = "## hello world ";
    assertEquals(
        "hello world",
        parser.parseHeader(str)
    );
  }

  @Test
  public void testParseHeader_3Sharp() throws Exception {
    String str = "### hello world ";
    assertEquals(
        "hello world",
        parser.parseHeader(str)
    );
  }

  @Test
  public void testParseVacation() throws Exception {
    String str = "* james -- 1986-09-09 - 1986-09-10";
    assertEquals(
        new Parser.Vacation(
            "james",
            parseLocalDate("1986-09-09"),
            parseLocalDate("1986-09-10")
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
  public void testIsWeekend() throws Exception {
    assertFalse(parser.isWeekend(parseLocalDate("2017-09-08")));
    assertTrue(parser.isWeekend(parseLocalDate("2017-09-09")));
    assertTrue(parser.isWeekend(parseLocalDate("2017-09-10")));
    assertFalse(parser.isWeekend(parseLocalDate("2017-09-11")));
  }
}
