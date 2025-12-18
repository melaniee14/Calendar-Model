import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;


import model.CalendarModelAllHelpers;
import model.CalendarModelGUISupport;
import model.MultipleCalendarModel;
import model.PropertyType;

/**
 * Represents a mock multiple model for the multiple calendar impl class.
 */
public class MockMultipleModel implements MultipleCalendarModel {
  StringBuilder string;
  private final Map<String, CalendarModelAllHelpers> calendars;

  /**
   * Constructs a mock model.
   *
   * @param string that is being built
   */
  public MockMultipleModel(StringBuilder string) {
    this.string = string;
    this.calendars = new HashMap<>();
  }

  /**
   * Creates a new calendar with specified name and timezone.
   *
   * @param name     unique name for the calendar
   * @param timezone timezone of a calendar
   * @throws IllegalArgumentException if name is not unique or timezone is invalid
   */
  @Override
  public void createCalendar(String name, String timezone) {
    string.append("create calendar called \n");
    calendars.put(name, new MockModel(string));
  }

  /**
   * Sets the current active calendar for operations.
   *
   * @param name name of the calendar to use
   * @return the current calculator that is now in use
   * @throws IllegalArgumentException if calendar with given name doesnt exist
   */
  @Override
  public CalendarModelGUISupport useCalendar(String name) {
    string.append("use calendar called \n");
    return (CalendarModelGUISupport)calendars.get(name);

  }

  /**
   * Gets the name of the currently active calendar.
   *
   * @return name of current calendar or null if no calendar is selected
   */
  @Override
  public String getCurrentCalendarName() {
    string.append("get calendar name called \n");
    return "";
  }

  /**
   * Gets the timezone of the currently active calendar.
   *
   * @return timezone of current calendar or system default if no calendar is selected
   */
  @Override
  public ZoneId getCurrentCalendarTimezone() {
    string.append("calendar timezone called \n");
    return null;
  }

  /**
   * Edits properties of an existing calendar.
   *
   * @param name     name of the calendar to edit
   * @param property property to edit name or "timezone
   * @param value    new value for the property
   * @throws IllegalArgumentException if calendar doesn't exist or property value is invalid
   */
  @Override
  public void editCalendar(String name, PropertyType property, String value) {
    string.append("edit calendar called \n");

  }

  /**
   * Copies a specific event to another calendar.
   *
   * @param eventName      name of the event to copy
   * @param eventDateTime  start time of the event to copy
   * @param targetCalendar name of the target calendar
   * @param targetDateTime target start time in target calendars timezone
   * @throws IllegalArgumentException if event calendar not found or operation invalid
   */
  @Override
  public void copyEvent(String eventName, LocalDateTime eventDateTime, String targetCalendar,
                        LocalDateTime targetDateTime) {
    string.append("copy event called \n");
  }

  /**
   * Copies events between specified dates to another calendar.
   *
   * @param startDate       start of the date range
   * @param endDate         end of the date range
   * @param targetCalendar  name of the target calendar
   * @param targetStartDate target start date in target calendars timezone
   * @throws IllegalArgumentException if calendars not found or dates invalid
   */
  @Override
  public void copyEvents(LocalDate startDate, LocalDate endDate, String targetCalendar,
                         LocalDate targetStartDate) {
    string.append("copy events called \n");
  }


}
