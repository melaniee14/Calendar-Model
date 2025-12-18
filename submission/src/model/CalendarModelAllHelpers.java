package model;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Interface that extends the functionality by adding methods related
 * to parsing and creating events, as well as managing recurring events in the calendar.
 */
public interface CalendarModelAllHelpers extends CalendarModel {


  /**
   * Parses and processes edit events within the calendar for a specific date,
   * subject, and change in properties.
   *
   * @param date    the date and time of the event to be edited
   * @param subject the subject of the event to be edited
   * @param change  the property change to apply to the event
   */
  public void parseEditEvents(LocalDateTime date, String subject, TypingChange change);

  /**
   * Creates a series of events based on the specified starting event,
   * repetition rule, and interval. The method handles the creation
   * of recurring events in the calendar model.
   *
   * @param startEvent     the initial event from which the recurring events are created
   * @param repeatWhen     the recurrence rule specifying how the events are repeated
   * @param repeatInterval the interval at which the events are repeated,
   *                      based on the recurrence rule
   */
  public void createAllEvents(Event startEvent, String repeatWhen, int repeatInterval);

  /**
   * Parses and processes a series of edit events within the calendar for the specified
   * subject, date, and property change. This method modifies recurring events
   * based on the provided parameters.
   *
   * @param subject the subject of the events in the series to be edited
   * @param date    the date and time of the events in the series to be edited
   * @param change  the property change to apply to the events in the series
   */
  public void parseEditEventSeries(String subject, LocalDateTime date, TypingChange change);

  /**
   * Gets the timezone of the calendar.
   *
   * @return timezone of the calendar
   */
  public ZoneId getTimezone();

  /**
   * Sets the timezone.
   *
   * @param timezone the timezone to set to
   */
  public void setTimezone(String timezone);

  /**
   * Retrieves the name of the calendar.
   *
   * @return the name of the calendar as a String
   */
  public String getName();
}
