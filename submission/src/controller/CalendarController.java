package controller;

/**
 * Orchestrates command parsing, model invocation, and view output.
 */

public interface CalendarController {

  /**
   * Runs the given controller to implement the functionality of the program.
   * Grabs data from the view and provides information inputted from the user to
   * the model.
   */
  void run();
}
