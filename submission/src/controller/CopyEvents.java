package controller;

import java.time.LocalDate;

import model.MultipleCalendarModel;

/**
 * Represents a command to copy multiple events from a specified date range in one calendar
 * to another calendar starting from a target date.
 */
public class CopyEvents implements MultipleCalendarCommand {
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final LocalDate targetDate;
  private final String calName;

  /**
   * Constructs a CopyEvents command.
   *
   * @param startDate the starting date of the range of events to be copied
   * @param endDate   the ending date of the range of events to be copied
   * @param calName   the name of the calendar from which events will be copied
   * @param targetDate the date in the target calendar where copied events will start
   */
  public CopyEvents(LocalDate startDate, LocalDate endDate, String calName, LocalDate targetDate) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.targetDate = targetDate;
    this.calName = calName;
  }

  @Override
  public void execute(MultipleCalendarModel model) {
    model.copyEvents(startDate, endDate, calName, targetDate);
  }
}
