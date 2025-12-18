package view;

import java.util.List;

import model.Event;

/**
 * An interface defining the contract for rendering calendar related information.
 * Provides methods for rendering messages, calendar events, and status updates.
 */
public interface CalendarView {

  /**
   * Renders a message by appending it to the associated output stream.
   *
   * @param message the message to be rendered must not be null
   * @throws IllegalStateException if writing to the output stream fails
   */
  void renderMessage(String message);

  /**
   * Renders a list of events by appending their details to the associated output stream.
   * If the list is null or empty then a message indicating no events are found will be appended.
   *
   * @param events the list of Model.CalendarEvent objects to be rendered and checks if they may
   *               be null or
   *               empty. Each event contains details such as subject start time end time
   *               location status and description.
   * @throws IllegalStateException if writing to the output stream fails.
   */
  void renderEvents(List<Event> events);

  /**
   * Appends the given status to the associated output stream.
   *
   * @param status the status message to be rendered; must not be null
   * @throws IllegalStateException if writing to the output stream fails
   */
  void renderStatus(String status);

  /**
   * Returns the Appendable associated with this View.CalendarViewImpl.
   *
   * @return the Appendable associated with this View.CalendarViewImpl
   */
  public Appendable getOut();
}