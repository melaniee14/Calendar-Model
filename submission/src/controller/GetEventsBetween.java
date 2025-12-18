package controller;

import java.time.LocalDateTime;

import model.CalendarModelAllHelpers;

/**
 * Represents a command to retrieve events occurring between two specified dates
 * in a calendar model. This command queries the calendar model for events within
 * the specified date range.
 */
public class GetEventsBetween implements CalendarCommand {
  private final LocalDateTime dateFrom;
  private final LocalDateTime dateTo;

  /**
   * Constructs a Controller.GetEventsBetween command to retrieve events within
   * the specified date range.
   *
   * @param dateFrom the start date of the range
   * @param dateTo   the end date of the range
   */
  public GetEventsBetween(LocalDateTime dateFrom, LocalDateTime dateTo) {
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
  }

  /**
   * Executes the Controller.GetEventsBetween command by querying the calendar model for events.
   */
  @Override
  public void execute(CalendarModelAllHelpers model) {
    model.getEventsBetween(dateFrom, dateTo);
  }

}
