package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import model.CalendarEvent;
import model.Event;
import model.EventIdentifier;
import model.Identifier;
import model.PropertyChange;
import model.PropertyType;
import model.TypingChange;

/**
 * A parser for converting string commands into executable calendar commands.
 * The Controller.EventCommandParser parses specific commands related to calendar events
 * and their associated properties or behaviors and returns appropriate
 * Controller.CalendarCommand implementations.
 */
public class EventCommandParser implements CommandParser {
  public EventCommandParser() {
    // no argument constructor
  }

  /**
   * Parses the input string to create an appropriate Controller
   * .CalendarCommand instance based on the command type.
   * The supported commands include:
   * * addEvent: Creates a single calendar event.
   * * addEventSeries: Creates a series of related calendar events.
   * * editEvent: Edits attributes of a single calendar event.
   * * editEventSeries: Edits a series of related calendar events.
   * * getEventsOnDate: Retrieves all events scheduled on a particular date.
   * * getEventsBetween: Retrieves all events scheduled between two dates.
   * * getStatus: Retrieves the status for a specific date.
   *
   * @param input the input string containing the command and its arguments
   * @return a Controller.CalendarCommand instance corresponding to the parsed command
   * @throws NullPointerException     if the input string is null
   * @throws IllegalArgumentException if the command type is unknown or invalid
   */
  public CalendarCommand parse(String input) {
    CalendarCommand cmd = null;
    if (input == null) {
      throw new IllegalArgumentException("command is null");
    }
    String[] args = input.split(" ");
    String command = args[0].concat(" ").concat(args[1]);
    String[] commandArgs = Arrays.copyOfRange(args, 2, args.length);
    DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    switch (command) {
      case "create event":
        if (commandArgs.length <= 6) {
          Event e = commandToEvent(commandArgs);
          cmd = new CreateEvent(e);
        }
        else {
          cmd = commandToEventSeries(commandArgs);
        }
        break;
      case "edit event":
        Identifier id = commandToEventID(Arrays.copyOfRange(commandArgs, 1, 6));
        TypingChange change = commandToProperty(commandArgs[0].toUpperCase(),
            commandArgs[commandArgs.length - 1]);
        cmd = new EditEvent(id, change);
        break;
      case "edit events":
        cmd = commandToEditEvents(commandArgs);
        break;
      case "edit series":
        cmd = commandToEditSeries(commandArgs);
        break;
      case "print events":
        cmd = commandToPrint(commandArgs);
        break;
      case "show status":
        LocalDateTime day = LocalDateTime.parse(commandArgs[1], formatTime);
        cmd = new ShowStatus(day);
        break;
      default:
    }
    return cmd;
  }

  private Event commandToEvent(String[] commandArgs) {
    String startTime;
    String endTime;
    CalendarEvent e;

    if (commandArgs.length == 5) {
      startTime = commandArgs[2];
      endTime = commandArgs[4];
    }
    else {
      startTime = commandArgs[2].concat("T").concat("08:00");
      endTime = commandArgs[2].concat("T").concat("17:00");
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    LocalDateTime start = LocalDateTime.parse(startTime, formatter);
    LocalDateTime end = LocalDateTime.parse(endTime, formatter);

    e = new CalendarEvent.EventBuilder()
      .setSubject(commandArgs[0])
      .setStartTime(start)
      .setEndTime(end)
      .build();

    return e;
  }

  private Identifier commandToEventID(String[] strings) {
    Event event = commandToEvent(strings);

    EventIdentifier eID = new EventIdentifier(event.getSubject(), event.getStartTime(),
        event.getStartTime());
    return eID;
  }

  private TypingChange commandToProperty(String property, String newValue) {
    PropertyType type = PropertyType.valueOf(property);
    PropertyChange change = new PropertyChange(type, newValue);
    return change;
  }

  private CalendarCommand commandToEventSeries(String[] commandArgs) {
    Event startEvent = null;
    String repeatWhen = "";
    int repeatInterval = 0;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter formatWithTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    if (commandArgs.length == 7 || commandArgs.length == 8) {
      startEvent = commandToEvent(Arrays.copyOfRange(commandArgs, 0,
        3));
      if (commandArgs.length == 7) {
        LocalDate until = LocalDate.parse(commandArgs[commandArgs.length - 1], formatter);
        repeatInterval = Math.toIntExact(ChronoUnit.WEEKS.between(
          startEvent.getStartTime().toLocalDate(), until));
      }
      else {
        repeatInterval = Integer.parseInt(commandArgs[commandArgs.length - 2]);
      }
      repeatWhen = commandArgs[4];
    }

    if (commandArgs.length >= 9) {
      startEvent = commandToEvent(Arrays.copyOfRange(commandArgs, 0, 5));
      if (commandArgs.length == 11 || commandArgs[commandArgs.length - 2].equals("until")) {
        LocalDate until = LocalDate.parse(commandArgs[commandArgs.length - 1], formatter);
        repeatInterval = Math.toIntExact(ChronoUnit.WEEKS.between(
          startEvent.getStartTime().toLocalDate(), until));
      }
      else {
        repeatInterval = Integer.parseInt(commandArgs[commandArgs.length - 2]);
      }
      repeatWhen = commandArgs[6];
    }
    return new CreateEventSeries(startEvent, repeatWhen, repeatInterval);
  }

  private CalendarCommand commandToEditEvents(String[] commandArgs) {
    DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    String prop = commandArgs[0].toUpperCase();
    String subject = commandArgs[1];
    String startDate = commandArgs[3];
    LocalDateTime start = LocalDateTime.parse(startDate, formatTime);
    String val = commandArgs[commandArgs.length - 1];
    TypingChange changeMade = commandToProperty(prop, val);

    return new EditEvents(start, subject, changeMade);
  }

  private CalendarCommand commandToEditSeries(String[] commandArgs) {
    String subject = commandArgs[1];
    DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    LocalDateTime date = LocalDateTime.parse(commandArgs[3], formatTime);
    TypingChange newChange = commandToProperty(commandArgs[0].toUpperCase(),
        commandArgs[commandArgs.length - 1]);
    return new EditEventSeries(subject, date, newChange);
  }

  private CalendarCommand commandToPrint(String[] commandArgs) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    if (commandArgs[0].equals("on")) {
      LocalDate dateOn = LocalDate.parse(commandArgs[1], formatter);
      return new GetEventsOnDate(dateOn);
    }
    else {
      LocalDateTime dateStart = LocalDateTime.parse(commandArgs[1], formatTime);
      LocalDateTime dateEnd = LocalDateTime.parse(commandArgs[3], formatTime);
      return new GetEventsBetween(dateStart, dateEnd);
    }
  }


}

