package jash.parser;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
      double manDays = Double.parseDouble(matcher.group(2));
      String owner = "TODO";
      if (matcher.group(4) != null) {
        owner = matcher.group(4).trim();
      }

      int progress = 0;
      if (matcher.group(6) != null) {
        progress = Integer.parseInt(matcher.group(6).trim());
      }

      Task task = new Task();
      task.setName(name);
      task.setOwner(owner);
      task.setCost(Double.valueOf(manDays * 2).intValue());
      task.setProgress(progress);

      return task;
    }

    return null;
  }

  public LocalDate parseProjectStartDate(String str) {
    Matcher matcher = PROJECT_START_DATE_PATTERN.matcher(str);
    if (matcher.matches()) {
      return LocalDate.parse(matcher.group(1));
    }

    return null;
  }

  public static LeveledHeader parseHeader(String str) {
    Matcher matcher = HEADER_PATTERN.matcher(str);
    if (matcher.matches()) {
      int level = matcher.group(1).trim().length();
      String header = matcher.group(2).trim();

      return new LeveledHeader(level, header);
    }

    return null;
  }

  public Vacation parseVacation(String str) {
      Matcher matcher = VACATION_PATTERN.matcher(str);
      if (matcher.matches()) {
          Vacation v = new Vacation(
              matcher.group(1).trim(),
              LocalDate.parse(matcher.group(2).trim()),
              LocalDate.parse(matcher.group(4).trim())
          );
          return v;
      }

      return null;
  }

  public Duration addDuration(Duration start, int numberOfHalfDays) {
    return start.plusHours(numberOfHalfDays * 12);
  }

  public boolean isEffectiveLine(String line) {
    return TASK_LINE_PATTERN.matcher(line).matches()
        || VACATION_PATTERN.matcher(line).matches()
        || HEADER_PATTERN.matcher(line).matches()
        || PROJECT_START_DATE_PATTERN.matcher(line).matches();
  }
  public Project parse(String content) {
    String[] lines = content.split("\n");
    return parse(Arrays.stream(lines).collect(Collectors.toList()));
  }

  public Project parse(List<String> lines) {
    List<Task> tasks = new ArrayList<>();
    List<Vacation> vacations = new ArrayList<>();
    LocalDate projectStartDate = null;

    Header header = Header.create();
    for (String line : lines) {
      Task task = parseTaskLine(line);
      if (task != null) {
        task.setHeader(header);
        tasks.add(task);
        continue;
      }

      Vacation vacation = parseVacation(line);
      if (vacation != null) {
        vacations.add(vacation);
        continue;
      }

      LocalDate date = parseProjectStartDate(line);
      if (date != null) {
        projectStartDate = date;
        continue;
      }

      LeveledHeader leveledHeader = parseHeader(line);
      if (leveledHeader != null) {
        header = header.addLeveledHeader(leveledHeader);
        continue;
      }
    }

    return new Project(projectStartDate, tasks, vacations);
  }
}
