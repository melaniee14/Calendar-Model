package controller;

import java.time.LocalDate;

import model.CalendarModelAllHelpers;

/**
 * Represents a command to retrieve all events on a specific date from the calendar model.
 * The command encapsulates a specific date and executes the get events on date method
 * to list all events scheduled on that date.
 */
public class GetEventsOnDate implements CalendarCommand {
  private final LocalDate date;

  /**
   * Constructs a Controller.GetEventsOnDate command to retrieve all events on a specific date.
   *
   * @param localDate the date for which events will be retrieved
   */
  public GetEventsOnDate(LocalDate localDate) {
    this.date = localDate;
  }

  /**
   * Executes the Controller.GetEventsOnDate command by querying the calendar model for events.
   *
   * @param model the calendar model on which the command will be executed
   */
  @Override
  public void execute(CalendarModelAllHelpers model) {
    model.getEventsOnDate(date);
  }
}
