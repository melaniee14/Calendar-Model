package controller;


import model.MultipleCalendarModel;

/**
 * Represents a command that can be executed on a calendar model.
 */
public interface MultipleCalendarCommand {

  /**
   * Executes a specific action or command on the provided calendar model.
   *
   * @param model the calendar model on which the command will be executed
   */
  void execute(MultipleCalendarModel model);
}
