package controller;


import model.CalendarModelAllHelpers;
import model.Event;

/**
 * Represents a command to create a new event in the calendar model.
 * This class encapsulates the event creation logic.
 */
public class CreateEvent implements CalendarCommand {
  private final Event event;

  /**
   * Creates a new Controller.CreateEvent command.
   *
   * @param e the event to be created
   */
  public CreateEvent(Event e) {
    this.event = e;
  }

  /**
   * Executes the command by adding the event to the calendar model.
   *
   * @param model the calendar model on which the command will be executed
   */
  @Override
  public void execute(CalendarModelAllHelpers model) {
    model.createEvent(event);
  }
}
