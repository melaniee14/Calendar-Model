package controller;

import java.time.LocalDateTime;


import model.CalendarModelAllHelpers;
import model.TypingChange;

/**
 * Represents a command to edit a series of events in the calendar.
 * This command modifies multiple events at once based on the provided list.
 */
public class EditEventSeries implements CalendarCommand {
  private final LocalDateTime date;
  private final String subject;
  private final TypingChange change;

  /**
   * Constructs an Controller.EditEventSeries command with a list of events to be edited.
   *
   * @param date the list of events that are part of the series to be modified
   */
  public EditEventSeries(String subject, LocalDateTime date, TypingChange change) {
    this.date = date;
    this.subject = subject;
    this.change = change;
  }

  @Override
  public void execute(CalendarModelAllHelpers model) {

    model.parseEditEventSeries(subject, date, change);
  }
}
