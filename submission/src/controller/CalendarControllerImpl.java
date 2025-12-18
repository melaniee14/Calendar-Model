package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import model.CalendarModelAllHelpers;
import model.MultipleCalendarModel;
import view.CalendarView;

/**
 * An implementation of the Controller.CalendarController interface that facilitates
 * interaction between the
 * input the calendar model and the calendar view. This class processes user input and provides
 * commands to the appropriate methods in the model for execution and interacts with the view
 * to provide feedback to the user.
 */
public class CalendarControllerImpl implements CalendarController {
  private final Readable in;
  private final MultipleCalendarModel model;
  private final CalendarView view;
  private final CommandParser parser;
  private final MultipleCommandParser calParser;

  /**
   * Creates a CalendarController object.
   *
   * @param in    the input
   * @param view  the view of the calendar
   * @param model the model of the calendar
   */
  public CalendarControllerImpl(Readable in, CalendarView view, MultipleCalendarModel model) {
    if (in == null || model == null || view == null) {
      throw new IllegalArgumentException("inputs cannot be null");
    }
    this.in = in;
    this.model = model;
    this.view = view;
    this.parser = new EventCommandParser();
    this.calParser = new CalendarCommandParser();
  }

  @Override
  public void run() {
    Scanner scanner = new Scanner(this.in);
    CalendarModelAllHelpers currentCal = null;
    String input = null;
    while (scanner.hasNextLine()) {
      input = scanner.nextLine();
      try {
        if (input.equalsIgnoreCase("quit")
                || input.equalsIgnoreCase("exit")) {
          break;
        }

        MultipleCalendarCommand calCommand = calParser.parse(input);
        if (calCommand instanceof UseCalendar) {
          currentCal = model.useCalendar(input.split(" ")[3]);
          continue;
        }
        if (calCommand == null) {
          if (currentCal != null) {
            CalendarCommand command = parser.parse(input);
            runCommand(command, input);
          } else {
            view.renderMessage("Invalid command: Calendar not in use.");
          }
        } else {
          calCommand.execute(model);
          view.renderMessage("Command executed successfully");
        }
      } catch (Exception e) {
        view.renderMessage("Error: " + e.getMessage());
      }
    }
    assert input != null;
    didExit(input);
  }

  private void didExit(String input) {
    assert input != null;
    if ((!(input.equalsIgnoreCase("exit")
            || input.equalsIgnoreCase("quit")))) {
      view.renderMessage("Error: Did not exit or quit");
      System.exit(0);
    }
  }

  private void runCommand(CalendarCommand command,
                          String input) {
    String currentCalName = model.getCurrentCalendarName();
    CalendarModelAllHelpers currentCal = model.useCalendar(currentCalName);
    DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    if (command == null) {
      view.renderMessage("Invalid command: " + input);
    } else {
      command.execute(currentCal);
      view.renderMessage("Command executed successfully");
      if (command instanceof GetEventsOnDate) {
        view.renderEvents(currentCal.getEventsOnDate(
                LocalDate.parse(input.split(" ")[3], formatter)));
      } else if (command instanceof GetEventsBetween) {
        view.renderEvents(currentCal.getEventsBetween(
                LocalDateTime.parse(input.split(" ")[3], formatTime),
                LocalDateTime.parse(input.split(" ")[5], formatTime)));

      } else if (command instanceof ShowStatus) {
        view.renderMessage(currentCal.getStatusMessage(
                LocalDateTime.parse(input.split(" ")[3], formatTime)));
      }
    }
  }
}