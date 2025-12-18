package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Implementation of MultipleCalendarModel interface that manages multiple calendars
 * and their events, supporting different timezones and event copying between calendars.
 */
public class MultipleCalendarModelImpl implements MultipleCalendarModelAllNames {
  private final Map<String, CalendarModelImpl> calendars;
  private String currentCalendarName;

  public MultipleCalendarModelImpl() {
    this.calendars = new HashMap<>();
    this.currentCalendarName = null;
  }

  @Override
  public void createCalendar(String name, String timezone) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be null or empty");
    }
    if (calendars.containsKey(name)) {
      throw new IllegalArgumentException("Calendar already exists");
    }
    try {
      ZoneId.of(timezone);
      calendars.put(name, new CalendarModelImpl(name, timezone));
      if (currentCalendarName == null) {
        currentCalendarName = name;
      }
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid timezone format");
    }
  }

  @Override
  public CalendarModelGUISupport useCalendar(String name) {
    if (!calendars.containsKey(name)) {
      throw new IllegalArgumentException("Calendar does not exist");
    }
    currentCalendarName = name;
    return calendars.get(name);
  }

  @Override
  public String getCurrentCalendarName() {
    return currentCalendarName;
  }

  @Override
  public ZoneId getCurrentCalendarTimezone() {
    CalendarModelAllHelpers current = getCurrentCalendar();
    if (current == null) {
      throw new IllegalStateException("No calendar currently selected");
    }
    return current.getTimezone();
  }

  @Override
  public void editCalendar(String name, PropertyType property, String value) {
    if (!calendars.containsKey(name)) {
      throw new IllegalArgumentException("Calendar does not exist");
    }
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("New value cannot be null or empty");
    }

    CalendarModelImpl calendar = calendars.get(name);
    if (property.equals(PropertyType.TIMEZONE)) {
      try {
        calendar.setTimezone(value);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid timezone: " + value);
      }
    } else if (property.equals(PropertyType.CALENDARNAME)) {
      if (calendars.containsKey(value)) {
        throw new IllegalArgumentException("Calendar already exists");
      }
      calendars.remove(name);
      calendars.put(value, calendar);
      if (currentCalendarName.equals(name)) {
        currentCalendarName = value;
      }
    } else {
      throw new IllegalArgumentException("Invalid property");
    }
  }

  @Override
  public void copyEvent(String eventName, LocalDateTime eventDateTime,
                        String targetCalendar, LocalDateTime targetDateTime) {
    CalendarModelGUISupport sourceCalendar = getCurrentCalendar();
    CalendarModelGUISupport targetCalendarModel = calendars.get(targetCalendar);
    validateCalendars(sourceCalendar, targetCalendarModel, targetCalendar);

    Event eventToCopy = findEvent(sourceCalendar, eventName, eventDateTime);
    if (eventToCopy == null) {
      throw new IllegalArgumentException("Event not found");
    }


    if (!(eventToCopy instanceof CalendarEvent)) {
      throw new IllegalArgumentException("Invalid event type");
    }

    CalendarEvent.EventBuilder builder = new CalendarEvent.EventBuilder()
            .setSubject(eventToCopy.getSubject())
            .setStartTime(targetDateTime)
            .setEndTime(targetDateTime.withHour(
                            eventToCopy.getEndTime().getHour())
                    .withMinute(eventToCopy.getEndTime().getMinute()))
            .setTimezone(targetCalendarModel.getTimezone())
            .setSeriesId(eventToCopy.getSeriesId());

    if (!eventToCopy.getDesc().isEmpty()) {
      builder.setDesc(eventToCopy.getDesc());
    }

    if (eventToCopy.getLocation() != null) {
      builder.setLocation(eventToCopy.getLocation().toString());
    }

    Event newEvent = builder.build();
    targetCalendarModel.createEvent(newEvent);
  }

  @Override
  public void copyEvents(LocalDate startDate, LocalDate endDate,
                         String targetCalendar, LocalDate targetStartDate) {
    if (startDate == null || endDate == null || targetStartDate == null) {
      throw new IllegalArgumentException("Dates cannot be null");
    }
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    }

    CalendarModel sourceCalendar = getCurrentCalendar();
    CalendarModelAllHelpers targetCalendarModel = calendars.get(targetCalendar);
    validateCalendars(sourceCalendar, targetCalendarModel, targetCalendar);

    List<Event> eventsToCopy = sourceCalendar.getEventsBetween(
            startDate.atStartOfDay(), endDate.atTime(23, 59));
    long daysBetween = targetStartDate.toEpochDay() - startDate.toEpochDay();

    Map<Long, List<Event>> seriesGroups = new HashMap<>();
    List<Event> nonSeriesEvents = new ArrayList<>();
    separateSeriesAndNonSeries(eventsToCopy, seriesGroups, nonSeriesEvents);

    copySeriesEvents(seriesGroups, daysBetween, targetCalendarModel);
    copyNonSeriesEvents(nonSeriesEvents, daysBetween, targetCalendarModel);
  }

  private CalendarModelGUISupport getCurrentCalendar() {
    if (currentCalendarName == null) {
      return null;
    }
    return calendars.get(currentCalendarName);
  }

  private void validateCalendars(CalendarModel source, CalendarModel target, String targetName) {
    if (source == null) {
      throw new IllegalStateException("No calendar is currently selected");
    }
    if (target == null) {
      throw new IllegalArgumentException("Target calendar does not exist");
    }
  }

  private Event findEvent(CalendarModel calendar, String eventName, LocalDateTime eventDateTime) {
    List<Event> events = calendar.getEventsOnDate(eventDateTime.toLocalDate());
    for (Event event : events) {
      if (event.getSubject().equals(eventName) &&
              event.getStartTime().equals(eventDateTime)) {
        return event;
      }
    }
    return null;
  }

  private void separateSeriesAndNonSeries(List<Event> eventsToCopy,
                                          Map<Long, List<Event>> seriesGroups,
                                          List<Event> nonSeriesEvents) {
    for (Event event : eventsToCopy) {
      if (event.getSeriesId() != null && event.getSeriesId() != 0) {
        seriesGroups.computeIfAbsent(event.getSeriesId(), k -> new ArrayList<>()).add(event);
      } else {
        nonSeriesEvents.add(event);
      }
    }
  }

  private void copySeriesEvents(Map<Long, List<Event>> seriesGroups, long daysBetween,
                                CalendarModelAllHelpers targetCalendarModel) {
    for (List<Event> seriesEvents : seriesGroups.values()) {
      List<Event> newSeriesEvents = new ArrayList<>();
      for (Event event : seriesEvents) {
        Event convertedEvent = event.newTimezone(targetCalendarModel.getTimezone());
        LocalDateTime newStartTime = convertedEvent.getStartTime().plusDays(daysBetween);
        LocalDateTime newEndTime = convertedEvent.getEndTime().plusDays(daysBetween);

        CalendarEvent.EventBuilder builder = new CalendarEvent.EventBuilder()
                .setSubject(convertedEvent.getSubject())
                .setStartTime(newStartTime)
                .setEndTime(newEndTime)
                .setDesc(convertedEvent.getDesc())
                .setSeriesId(convertedEvent.getSeriesId())
                .setTimezone(targetCalendarModel.getTimezone());

        if (convertedEvent.getLocation() != null) {
          builder.setLocation(convertedEvent.getLocation().toString());
        }
        if (convertedEvent.getStatus() != null) {
          builder.setStatus(convertedEvent.getStatus().toString());
        }

        newSeriesEvents.add(builder.build());
      }
      targetCalendarModel.createEvents(newSeriesEvents);
    }
  }

  private void copyNonSeriesEvents(List<Event> nonSeriesEvents, long daysBetween,
                                   CalendarModelAllHelpers targetCalendarModel) {
    for (Event event : nonSeriesEvents) {
      Event convertedEvent = event.newTimezone(targetCalendarModel.getTimezone());
      LocalDateTime newStartTime = convertedEvent.getStartTime().plusDays(daysBetween);
      LocalDateTime newEndTime = convertedEvent.getEndTime().plusDays(daysBetween);

      CalendarEvent.EventBuilder builder = new CalendarEvent.EventBuilder()
              .setSubject(convertedEvent.getSubject())
              .setStartTime(newStartTime)
              .setEndTime(newEndTime)
              .setDesc(convertedEvent.getDesc())
              .setTimezone(targetCalendarModel.getTimezone());

      if (convertedEvent.getLocation() != null) {
        builder.setLocation(convertedEvent.getLocation().toString());
      }
      if (convertedEvent.getStatus() != null) {
        builder.setStatus(convertedEvent.getStatus().toString());
      }

      targetCalendarModel.createEvent(builder.build());
    }
  }

  @Override
  public List<String> getAllNames() {
    return new ArrayList<String>(calendars.keySet());
  }
}