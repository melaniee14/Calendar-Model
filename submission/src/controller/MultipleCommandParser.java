package controller;

/**
 * Interface for parsing string commands into executable commands.
 */
public interface MultipleCommandParser {
  /**
   * Parses a string input into a command that can be executed.
   *
   * @param input the string command to be parsed
   * @return a Controller.CalendarCommand object that can be executed on the model
   * @throws NullPointerException     if the input is null
   * @throws IllegalArgumentException if command isn't recognized or if format is invalid
   */
  MultipleCalendarCommand parse(String input);
}
