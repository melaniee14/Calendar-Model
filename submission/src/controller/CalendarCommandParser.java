package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import model.PropertyType;

/**
 * This class is responsible for recognizing different types of calendar commands
 * and parsing the associated arguments to construct valid command objects.
 */
public class CalendarCommandParser implements MultipleCommandParser {

  /**
   * Constructs a command parser.
   */
  public CalendarCommandParser() {
    // Empty Constructor just used for parsing commands

  }

  @Override
  public MultipleCalendarCommand parse(String input) {
    MultipleCalendarCommand cmd;
    if (input == null) {
      throw new IllegalArgumentException("Input is null");
    }

    String[] parts = input.split(" ");
    String[] commandArgs = Arrays.copyOfRange(parts, 2, parts.length);
    String command = parts[0].concat(" ").concat(parts[1]);
    switch (command) {
      case "create calendar":
        if (commandArgs.length < 4) {
          throw new IllegalArgumentException("Invalid, please input name or timezone "
                  + "of calendar");
        }
        cmd = new CreateCalendar(commandArgs[1], commandArgs[3]);
        break;
      case "edit calendar":
        PropertyType type = PropertyType.valueOf(commandArgs[3]);
        cmd = new EditCalendar(commandArgs[1], type, commandArgs[4]);
        break;
      case "copy event":
        cmd = commandToCopyEvent(commandArgs);
        break;
      case "copy events":
        cmd = commandToCopyEvents(commandArgs);
        break;
      case "use calendar":
        cmd = new UseCalendar(commandArgs[1]);
        break;
      default:
        cmd = null;
        break;
    }
    return cmd;
  }

  private MultipleCalendarCommand commandToCopyEvents(String[] commandArgs) {
    DateTimeFormatter formatNoTime = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    String calName = "";
    LocalDate startDate = null;
    LocalDate endDate = null;
    LocalDate targDate = null;
    if (commandArgs[0].equals("on")) {
      calName = commandArgs[3];
      startDate = LocalDate.parse(commandArgs[1], formatNoTime);
      endDate = LocalDate.parse(commandArgs[1], formatNoTime);
      targDate = LocalDate.parse(commandArgs[5], formatNoTime);
    } else if (commandArgs[0].equals("between")) {
      calName = commandArgs[5];
      startDate = LocalDate.parse(commandArgs[1], formatNoTime);
      endDate = LocalDate.parse(commandArgs[3], formatNoTime);
      targDate = LocalDate.parse(commandArgs[commandArgs.length - 1], formatNoTime);
    }
    return new CopyEvents(startDate, endDate, calName, targDate);
  }

  private MultipleCalendarCommand commandToCopyEvent(String[] commandArgs) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    String sourceCal = commandArgs[0];
    String eventTime = commandArgs[2];
    String targetCal = commandArgs[4];
    String targetTime = commandArgs[6];
    LocalDateTime eventT = LocalDateTime.parse(eventTime, formatter);
    LocalDateTime targetT = LocalDateTime.parse(targetTime, formatter);
    return new CopyEvent(sourceCal, eventT, targetCal, targetT);
  }
}
