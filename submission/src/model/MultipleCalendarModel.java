package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Interface for managing multiple calendars with their events, timezones, and properties.
 */
public interface MultipleCalendarModel {
  /**
   * Creates a new calendar with specified name and timezone.
   *
   * @param name     unique name for the calendar
   * @param timezone timezone of a calendar
   * @throws IllegalArgumentException if name is not unique or timezone is invalid
   */
  public void createCalendar(String name, String timezone);

  /**
   * Sets the current active calendar for operations.
   *
   * @param name name of the calendar to use
   * @return the current calculator that is now in use
   * @throws IllegalArgumentException if calendar with given name doesnt exist
   */
  public CalendarModelGUISupport useCalendar(String name);

  /**
   * Gets the name of the currently active calendar.
   *
   * @return name of current calendar or null if no calendar is selected
   */
  public String getCurrentCalendarName();

  /**
   * Gets the timezone of the currently active calendar.
   *
   * @return timezone of current calendar or system default if no calendar is selected
   */
  public ZoneId getCurrentCalendarTimezone();

  /**
   * Edits properties of an existing calendar.
   *
   * @param name     name of the calendar to edit
   * @param property property to edit name or "timezone
   * @param value    new value for the property
   * @throws IllegalArgumentException if calendar doesn't exist or property value is invalid
   */
  public void editCalendar(String name, PropertyType property, String value);

  /**
   * Copies a specific event to another calendar.
   *
   * @param eventName      name of the event to copy
   * @param eventDateTime  start time of the event to copy
   * @param targetCalendar name of the target calendar
   * @param targetDateTime target start time in target calendars timezone
   * @throws IllegalArgumentException if event calendar not found or operation invalid
   */
  public void copyEvent(String eventName, LocalDateTime eventDateTime,
                 String targetCalendar, LocalDateTime targetDateTime);

  /**
   * Copies events between specified dates to another calendar.
   *
   * @param startDate      start of the date range
   * @param endDate        end of the date range
   * @param targetCalendar name of the target calendar
   * @param targetStartDate target start date in target calendars timezone
   * @throws IllegalArgumentException if calendars not found or dates invalid
   */
  public void copyEvents(LocalDate startDate, LocalDate endDate,
                  String targetCalendar, LocalDate targetStartDate);
}