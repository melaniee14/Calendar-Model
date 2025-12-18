package controller;

import java.time.LocalDate;
import java.util.List;


/**
 * Interface that represents the controller for the GUI.
 */
public interface CalendarControllerGUI extends CalendarController {

  /**
   * Handles viewing the schedule of events from a specific date.
   *
   * @param date The date from which to view the schedule.
   */
  public void viewSchedule(LocalDate date);

  /**
   * Handles adding a new event to the currently active calendar.
   * @param subject subject of the new event
   * @param startWithDate start time of event
   * @param endWithDate end time of event
   * @param location location of event
   * @param description description of event
   * @param status status of event
   */
  public void addEvent(String subject, String startWithDate, String endWithDate, String location,
                       String description, String status);

  /**
   * Handles editing an event within the currently active calendar.
   * @param eventName name of event
   * @param changeType type of change
   * @param change change being made
   */
  public void editEvent(String eventName, String changeType, String change);

  /**
   * Switches to a different calendar in the multiple model.
   *
   * @param calendarName The name of the calendar to switch to.
   */
  public void switchCalendar(String calendarName);

  /**
   * Creates a new calendar and switches to it.
   *
   * @param name     The name of the new calendar.
   * @param timezone The timezone of the new calendar.
   */
  public void createNewCalendar(String name, String timezone);

  /**
   * Retrieves the list of all available calendar names.
   *
   * @return A list of calendar names.
   */
  public List<String> getAvailableCalendarNames();

  /**
   * Retrieves a list of all events.
   * @return A list of all events.
   */
  public List<String> getAllEventNames();


}
