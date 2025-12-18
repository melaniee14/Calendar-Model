package controller;


import model.CalendarModelAllHelpers;
import model.Event;

/**
 * Represents a command to create a series of events in a calendar model.
 * This command adds a series of events associated with a specific series ID
 * to the given calendar model.
 */
public class CreateEventSeries implements CalendarCommand {
  private final Event startEvent;
  private final String repeatWhen;
  private final int repeatInterval;

  /**
   * Constructs a CreateEventSeries command that represents a recurring series
   * of events in the calendar model.
   *
   * @param startEvent the initial event that defines the basis for the series
   * @param repeatWhen a string indicating the recurrence rule (e.g., "daily", "weekly")
   * @param repeatInterval an integer specifying the interval for the recurrence
   */
  public CreateEventSeries(Event startEvent, String repeatWhen, int repeatInterval) {
    this.startEvent = startEvent;
    this.repeatWhen = repeatWhen;
    this.repeatInterval = repeatInterval;
  }

  /**
   * Executes the Controller.CreateEventSeries command by adding a series of events to the
   * given calendar model.
   *
   * @param model the calendar model to which the series of events will be added
   */
  @Override
  public void execute(CalendarModelAllHelpers model) {
    model.createAllEvents(startEvent, repeatWhen, repeatInterval);
  }
}
