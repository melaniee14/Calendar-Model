package controller;

import java.time.LocalDateTime;

import model.CalendarModelAllHelpers;

/**
 * Represents a command to display the status message of a specific date in a calendar model.
 * The status message is retrieved from the calendar model and corresponds to the provided date.
 */
public class ShowStatus implements CalendarCommand {
  private final LocalDateTime date;

  /**
   * Constructs a Controller.ShowStatus command to retrieve the status message for a specific date.
   *
   * @param date the date for which the status message will be retrieved
   */
  public ShowStatus(LocalDateTime date) {
    this.date = date;
  }

  /**
   * Executes the command to retrieve the status message for a specific date
   * from the given calendar model.
   *
   * @param model the calendar model from which the status message will be retrieved
   */
  @Override
  public void execute(CalendarModelAllHelpers model) {
    model.getStatusMessage(date);
  }
}
