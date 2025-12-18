package controller;

import model.MultipleCalendarModel;

/**
 * Represents a command to use a calendar with the provided name.
 */
public class UseCalendar implements MultipleCalendarCommand {
  private final String name;

  public UseCalendar(String name) {
    this.name = name;

  }

  /**
   * Executes a specific action or command on the provided calendar model.
   *
   * @param model the calendar model on which the command will be executed
   */
  @Override
  public void execute(MultipleCalendarModel model) {
    model.useCalendar(name);
  }
}
