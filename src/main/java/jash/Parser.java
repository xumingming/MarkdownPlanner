package jash;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Value;

/**
 * Parser of yash files.
 */
public class Parser {
  private static final Pattern TASK_LINE_PATTERN =
      Pattern.compile("\\*(.+)--\\s*([0-9]+\\.?[0-9]?)\\s*(\\[([^\\[\\]]+)])?(\\[([0-9]+)%\\s*\\])??\\s*$");

  private static final Pattern PROJECT_START_DATE_PATTERN =
      Pattern.compile(".*?ProjectStartDate:\\s*([0-9]{4}-[0-9]{2}-[0-9]{2})");

  private static final Pattern HEADER_PATTERN =
      Pattern.compile("^(#{2,})(.*)");

  private static final Pattern VACATION_PATTERN =
      Pattern.compile("^\\*(.+?)--\\s*([0-9]{4}-[0-9]{2}-[0-9]{2})(\\s*-\\s*([0-9]{4}-[0-9]{2}-[0-9]{2}))?\\s*$");


  public Task parseTaskLine(String line) {
    Matcher matcher = TASK_LINE_PATTERN.matcher(line);
    if (matcher.matches()) {
      String name = matcher.group(1).trim();
      int manDays = Integer.parseInt(matcher.group(2));
      String owner = "TODO";
      if (matcher.group(4) != null) {
        owner = matcher.group(4).trim();
      }

      int progress = 0;
      if (matcher.group(6) != null) {
        progress = Integer.parseInt(matcher.group(6).trim());
      }

      //Task task = new Task(name, owner, manDays, progress);
      Task task;


      System.out.println(matcher.group(1));
      return task;
    }

    return null;
  }

  public Date parseProjectStartDate(String str) {
    Matcher matcher = PROJECT_START_DATE_PATTERN.matcher(str);
    if (matcher.matches()) {
      return parseDate(matcher.group(1));
    }

    return null;
  }

  public String parseHeader(String str) {
    Matcher matcher = HEADER_PATTERN.matcher(str);
    if (matcher.matches()) {
      return matcher.group(2).trim();
    }

    return null;
  }

  public Vacation parseVacation(String str) {
    Matcher matcher = VACATION_PATTERN.matcher(str);
    if (matcher.matches()) {
      Vacation v = new Vacation(
          matcher.group(1).trim(),
          parseLocalDate(matcher.group(2).trim()),
          parseLocalDate(matcher.group(4).trim())
      );
      return v;
    }

    return null;
  }

  private Date parseDate(String str) {
    try {
      return new SimpleDateFormat("yyyy-MM-dd").parse(str);
    } catch (Exception e) {
      return null;
    }
  }

  private LocalDate parseLocalDate(String str) {
    return LocalDate.parse(str);
  }

  public boolean isWeekend(LocalDate date) {
    DayOfWeek dayOfWeek = date.getDayOfWeek();

    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
  }

  public Duration addDuration(Duration start, int numberOfHalfDays) {
    return start.plusHours(numberOfHalfDays * 12);
  }

  @Value
  public static class Vacation {
    private String user;
    private LocalDate startDate;
    private LocalDate endDate;
  }

  @Setter
  @Getter
  @EqualsAndHashCode
  @ToString
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Task {
    /** task name */
    private String name;
    /** task owner */
    private String owner;
    /** man days */
    private double manDays;
    /** progress */
    private int progress;
    /** offset from ProjectStartDate(Unit: HalfDay) */
    private int offset;

    public double getFinishedManDays() {
      return progress * manDays / 100;
    }

    public int getCost() {
      return Double.valueOf(manDays * 2).intValue();
    }

    public LocalDate getStartDate(LocalDate projectStartDate) {
      return projectStartDate.plus(offset * 12, ChronoUnit.HOURS);
    }

    public LocalDate getEndDate(LocalDate projectStartDate) {
      return getStartDate(projectStartDate).plus(getCost() * 12, ChronoUnit.HOURS);
    }
  }

  @Value
  public static class Project {
    private LocalDate projectStartDate;
    private List<Task> tasks;
    private List<Vacation> vacations;

    public List<String> getMen() {
      return tasks.stream().map(Task::getOwner).collect(Collectors.toList());
    }

    public double getProgress() {
      return getFinishedManDays() * 100 / getTotalManDays();
    }

    public double getTotalManDays() {
      return tasks.stream().mapToDouble(Task::getManDays).sum();
    }

    public double getFinishedManDays() {
      return tasks.stream().mapToDouble(Task::getFinishedManDays).sum();
    }

    public boolean isInVacation(Instant time, String user) {

    }

    public Instant addDuration(Instant startTime, int numOfHalfDays, String owner) {
      int count = 0;
      while (count < numOfHalfDays) {

      }
      startTime.plusSeconds(numOfHalfDays * 12 * 3600);
      return null;
    }
  }

  /**
   * Represents the duration of half a day(12 Hours)
   */
  public static class HalfDay {

  }
}
