package controller;


import model.CalendarModelAllHelpers;

/**
 * Represents a command that can be executed on a calendar model.
 */
public interface CalendarCommand {
  /**
   * Executes a specific action or command on the provided calendar model.
   *
   * @param model the calendar model on which the command will be executed
   */
  void execute(CalendarModelAllHelpers model);
}
