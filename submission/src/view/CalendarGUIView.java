package view;

import controller.CalendarControllerGUI;
import model.Event;

import java.util.List;


/**
 * Represents an interface for the GUI view of a calendar application.
 * Provides methods for displaying the interface, updating event schedules, and handling user
 * feedback messages.
 */
public interface CalendarGUIView {
  /**
   * Displays the GUI window and makes it visible.
   */
  void display();

  /**
   *  Update the view with the current schedule.
   * @param events the list of events to update with
   */
  void updateScheduleView(List<Event> events);

  /**
   * Shows an error message to the user.
   * @param message The error message to display
   */
  void showErrorMessage(String message);

  /**
   * Shows a success message to the user.
   * @param message The success message to display
   */
  void showSuccessMessage(String message);

  /**
   * Sets the controller for this view.
   * @param controller The controller to handle GUI events
   */
  void setController(CalendarControllerGUI controller);
}