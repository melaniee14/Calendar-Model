

import model.CalendarModelGUISupport;
import model.MultipleCalendarModelAllNames;
import model.PropertyType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A mock implementation of MultipleCalendarModelAllNames for testing purposes.
 */
public class MockGUIMultipleModel implements MultipleCalendarModelAllNames {
  private final StringBuilder log;
  private final HashMap<String, CalendarModelGUISupport> calendars;
  private String currentCalendarName;

  /**
   * Creates a MockGUIMultipleModel object.
   * @param log string builder used to keep track of methods called
   */
  public MockGUIMultipleModel(StringBuilder log) {
    this.log = log;
    this.currentCalendarName = "Default Calendar";
    this.calendars = new HashMap<>();
  }


  @Override
  public List<String> getAllNames() {
    log.append("getAllNames called\n");
    List<String> names = new ArrayList<>();
    names.add("TestCalendar");
    return names;
  }

  @Override
  public void createCalendar(String name, String timezone) {
    ZoneId zoneId = ZoneId.of(timezone);
    if (name.isEmpty()) {
      throw new IllegalArgumentException("name cannot be empty");
    }
    log.append("createCalendar called with name: ").append(name)
            .append(" timezone: ").append(timezone).append("\n");
    currentCalendarName = name;
    calendars.put(name, new MockModel(log));
  }

  @Override
  public CalendarModelGUISupport useCalendar(String name) {
    log.append("useCalendar called with name: ").append(name).append("\n");
    return calendars.get(name);
  }

  @Override
  public String getCurrentCalendarName() {
    log.append("getCurrentCalendarName called\n");
    return currentCalendarName;
  }

  @Override
  public ZoneId getCurrentCalendarTimezone() {
    log.append("getCurrentCalendarTimezone called\n");
    return ZoneId.systemDefault();

  }

  @Override
  public void editCalendar(String name, PropertyType property, String value) {
    log.append("editCalendar called with name: ").append(name)
            .append(" property: ").append(property)
            .append(" value: ").append(value).append("\n");
    if (property == PropertyType.CALENDARNAME) {
      currentCalendarName = value;
    }

  }

  @Override
  public void copyEvent(String eventName, LocalDateTime eventDateTime, String targetCalendar,
                        LocalDateTime targetDateTime) {
    log.append("copyEvent called with event: ").append(eventName)
            .append(" from: ").append(eventDateTime)
            .append(" to calendar: ").append(targetCalendar)
            .append(" at: ").append(targetDateTime).append("\n");

  }

  @Override
  public void copyEvents(LocalDate startDate, LocalDate endDate, String targetCalendar,
                         LocalDate targetStartDate) {
    log.append("copyEvents called from: ").append(startDate)
            .append(" to: ").append(endDate)
            .append(" target calendar: ").append(targetCalendar)
            .append(" target start: ").append(targetStartDate).append("\n");


  }

  public String getLog() {
    return log.toString();
  }
}