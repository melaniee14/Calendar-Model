package controller;

import model.MultipleCalendarModel;

/**
 * Represents a command to create a new calendar within a calendar model.
 * This command is responsible for adding a calendar with a specified name and timezone.
 * It executes the action on a provided model that supports multiple calendars.
 */
public class CreateCalendar implements MultipleCalendarCommand {
  private final String name;
  private final String timezone;

  /**
   * Constructs a new Controller.CreateCalendar command.
   *
   * @param name     the name of the calendar to be created
   * @param timezone the timezone of the calendar to be created
   */
  public CreateCalendar(String name, String timezone) {
    this.name = name;
    this.timezone = timezone;
  }

  @Override
  public void execute(MultipleCalendarModel model) {
    model.createCalendar(name, timezone);
  }

}
