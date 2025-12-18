package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Implementation of the CalendarModel interface, providing functionality
 * to manage calendar events including adding, editing, and organizing recurring series.
 * The class ensures data integrity by validating event details and preventing duplicates
 * or conflicts.
 */
public class CalendarModelImpl implements CalendarModelGUISupport {
  private final Map<LocalDate, List<Event>> allEvents;
  private final Map<Long, List<Event>> recurringSeries;
  private long seriesId;
  private final String name;
  private ZoneId timezone;


  /**
   * Constructs a new instance of CalendarModelImpl.
   * Initializes the required data structures for managing calendar events and recurring series.
   */
  public CalendarModelImpl(String name, String timezone) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be null or empty");
    }
    this.name = name;
    if (timezone == null || timezone.trim().isEmpty()) {
      this.timezone = ZoneId.systemDefault();
    } else {
      try {
        this.timezone = ZoneId.of(timezone);
      } catch (Exception e) {
        throw new IllegalArgumentException("Invalid timezone format");
      }
    }
    this.allEvents = new HashMap<>();
    this.recurringSeries = new HashMap<>();
    this.seriesId = 1;
  }

  public CalendarModelImpl(String name) {
    this(name, ZoneId.systemDefault().getId());
  }


  /**
   * Adds an event to the calendar. The method ensures that the provided event has valid start and
   * end times. If the times are missing then default values will be used. The event is checked for
   * duplicates in the calendar before being added. If either the start time or end time of the
   * event is null then default times are assigned signifying an all day event.
   * The event's subject start time and end time must not conflict with existing events in the
   * calendar.
   *
   * @param event the event to be added to the calendar.
   * @throws IllegalArgumentException if the event is null or if both start and end times are null
   *                                  or if a duplicate event exists in the calendar.
   */
  @Override
  public void createEvent(Event event) {
    if (event == null) {
      throw new IllegalArgumentException("Model.CalendarEvent cannot be null");
    }

    Event calendarEvent;
    if (event instanceof CalendarEvent) {
      calendarEvent = event.newTimezone(this.timezone);
    } else {
      CalendarEvent.EventBuilder builder = new CalendarEvent.EventBuilder()
              .setSubject(event.getSubject())
              .setStartTime(event.getStartTime())
              .setEndTime(event.getEndTime())
              .setDesc(event.getDesc())
              .setSeriesId(event.getSeriesId())
              .setTimezone(this.timezone);

      if (event.getLocation() != null) {
        builder.setLocation(event.getLocation().toString());
      }
      if (event.getStatus() != null) {
        builder.setStatus(event.getStatus().toString());
      }
      calendarEvent = builder.build();
    }

    Event update = createAllDay(calendarEvent);
    addEventToMap(update);

  }

  private Event createAllDay(Event calendarEvent) {
    if (calendarEvent.getEndTime() == null || calendarEvent.getStartTime() == null) {
      LocalDate date;
      if (calendarEvent.getStartTime() != null) {
        date = calendarEvent.getStartTime().toLocalDate();
      } else if (calendarEvent.getEndTime() != null) {
        date = calendarEvent.getEndTime().toLocalDate();
      } else {
        throw new IllegalArgumentException("Either start time or end time must be non-null");
      }

      calendarEvent = new CalendarEvent.EventBuilder()
              .setSubject(calendarEvent.getSubject())
              .setStartTime(LocalDateTime.of(date, LocalTime.of(8, 0)))
              .setEndTime(LocalDateTime.of(date, LocalTime.of(17, 0)))
              .setSeriesId(calendarEvent.getSeriesId())
              .setTimezone(this.timezone)
              .build();
    }
    return calendarEvent;
  }

  private void addEventToMap(Event calendarEvent) {
    LocalDate startDate = calendarEvent.getStartTime().toLocalDate();
    LocalDate endDate = calendarEvent.getEndTime().toLocalDate();

    LocalDate currentDate = startDate;
    while (!currentDate.isAfter(endDate)) {
      List<Event> dateEvents = this.allEvents.get(currentDate);
      if (dateEvents != null) {
        for (Event e : dateEvents) {
          if (e.getSubject().equals(calendarEvent.getSubject())
                  && e.getStartTime().equals(calendarEvent.getStartTime())
                  && e.getEndTime().equals(calendarEvent.getEndTime())) {
            throw new IllegalArgumentException("Event exists already.");
          }
        }
      }
      currentDate = currentDate.plusDays(1);
    }

    currentDate = startDate;
    while (!currentDate.isAfter(endDate)) {
      if (!this.allEvents.containsKey(currentDate)) {
        this.allEvents.put(currentDate, new ArrayList<>());
      }
      this.allEvents.get(currentDate).add(calendarEvent);
      currentDate = currentDate.plusDays(1);
    }

  }


  @Override
  public ZoneId getTimezone() {
    return timezone;
  }

  @Override
  public void setTimezone(String timezone) {
    if (timezone == null || timezone.trim().isEmpty()) {
      throw new IllegalArgumentException("Timezone cannot be null or empty");
    }
    try {
      ZoneId newTimezone = ZoneId.of(timezone);
      for (List<Event> events : allEvents.values()) {
        for (int i = 0; i < events.size(); i++) {
          Event event = events.get(i);
          if (event instanceof CalendarEvent) {
            events.set(i, event.newTimezone(newTimezone));
          }
        }
      }
      this.timezone = newTimezone;
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid timezone format");
    }
  }

  @Override
  public void parseEditEvents(LocalDateTime date, String subject, TypingChange change) {
    List<Event> eventsOnDate = getEventsOnDate(date.toLocalDate());
    List<Event> eventsToEdit = new ArrayList<>();
    Event firstEvent = null;
    for (Event event : eventsOnDate) {
      if (event.getStartTime().isEqual(date) && event.getSubject().equals(subject)) {
        firstEvent = event;
        break;
      }
    }

    assert firstEvent != null;
    eventsToEdit.add(firstEvent);
    Long seriesId = firstEvent.getSeriesId();
    List<Event> events = recurringSeries.get(seriesId);

    for (Event ev : events) {
      if (ev.getStartTime().isAfter(date)) {
        eventsToEdit.add(ev);
      }

    }
    editEvents(eventsToEdit, change);
  }

  @Override
  public void parseEditEventSeries(String subject, LocalDateTime date, TypingChange change) {
    List<Event> eventsOnDate = getEventsOnDate(date.toLocalDate());
    Long seriesId = 0L;

    for (Event event : eventsOnDate) {
      if (event.getStartTime().isEqual(date) && event.getSubject().equals(subject)) {
        seriesId = event.getSeriesId();
        break;
      }
    }
    assert seriesId != 0L;
    editSeries(seriesId, change);
  }

  @Override
  public void createAllEvents(Event startEvent, String repeatWhen,
                              int repeatInterval) {
    ArrayList<Event> events = new ArrayList<>();
    ArrayList<Character> days = new ArrayList<Character>(
            Arrays.asList('M', 'T', 'W', 'R', 'F', 'S', 'U'));
    LocalDateTime start = startEvent.getStartTime();
    LocalDateTime end = startEvent.getEndTime();
    events.add(startEvent);

    for (int i = 0; i < repeatInterval; i++) {
      LocalDateTime startOfWeek = start.plusWeeks(i);
      for (int j = 0; j < repeatWhen.length(); j++) {
        if (days.contains(repeatWhen.charAt(j))) {
          int increase = getIncreaseDaysBy(start, String.valueOf(repeatWhen.charAt(j)));
          LocalDateTime eventStart = startOfWeek.plusDays(increase);
          LocalDateTime eventEnd = end.plusWeeks(i).plusDays(increase);
          if (eventStart.isEqual(start)) {
            continue;
          }
          CalendarEvent newEvent = new CalendarEvent.EventBuilder()
                  .setSubject(startEvent.getSubject())
                  .setStartTime(eventStart)
                  .setEndTime(eventEnd)
                  .build();
          events.add(newEvent);
        } else {
          throw new IllegalArgumentException("Invalid day");
        }
      }
    }
    this.createEvents(events);
  }


  /**
   * Adds a list of events to the calendar as a recurring series. Each event must occur
   * on the same day and the start and end times must not span multiple days.
   * If validation fails for any event in the series no events are added to the calendar.
   *
   * @param events a list of events to be added as a series to the calendar
   * @throws IllegalArgumentException if the events list is null empty or if the events do not
   *                                  meet validation requirements
   */
  @Override
  public void createEvents(List<Event> events) {
    validateRecurringEventList(events);

    LocalDateTime startTime = events.get(0).getStartTime();
    long seriesId = this.seriesId++;
    ArrayList<Event> addedEvents = new ArrayList<>();

    try {
      for (Event e : events) {
        CalendarEvent updated = buildRecurringEvent(e, seriesId);
        safelyAddEvent(updated, addedEvents);
      }
      recurringSeries.put(seriesId, addedEvents);
    } catch (Exception ex) {
      rollbackAddedEvents(addedEvents);
      throw ex;
    }
  }


  /**
   * Edits an existing event in the calendar by updating its properties based on the given change.
   * The method identifies the event to be edited using the id and ensures
   * that the updated event does not conflict with other events in the calendar on the same date.
   *
   * @param id     the id of the event.
   * @param change the property change to be applied to the event specifying the field to
   *               change and its new value
   * @throws IllegalArgumentException if the event identifier or change is null,
   *                                  the event does not exist,
   *                                  or the updated event conflicts with another event
   */
  @Override
  public Event editEvent(Identifier id, TypingChange change, boolean isSeries) {
    if (id == null || change == null) {
      throw new IllegalArgumentException("ID and Change cannot be null");
    }

    List<Event> eventsOnDate = getEventsOnDate(id.getStartTime().toLocalDate());
    for (int i = 0; i < eventsOnDate.size(); i++) {
      Event e = eventsOnDate.get(i);

      if (e.getStartTime().equals(change.getNewValue())) {
        throw new IllegalArgumentException("Time already exists");
      } else if ((e.getSubject().equals(id.getSubject())
              && e.getStartTime().equals(id.getStartTime()))) {
        Event updatedEvent = changeEventProperty(e, change, isSeries);

        if (change.getType() == PropertyType.START || change.getType() == PropertyType.END) {
          allEvents.remove(id.getStartTime().toLocalDate());
          eventsOnDate.remove(i);
          id = new EventIdentifier(id.getSubject(), updatedEvent.getStartTime(),
                  updatedEvent.getEndTime());
          List<Event> newDate = getEventsOnDate(id.getStartTime().toLocalDate());
          allEvents.put(id.getStartTime().toLocalDate(), newDate);
          newDate.add(updatedEvent);
        } else {
          eventsOnDate.set(i, updatedEvent);
          allEvents.replace(id.getStartTime().toLocalDate(), eventsOnDate);
        }
        return updatedEvent;
      }
    }

    throw new IllegalArgumentException("Event does not exist");
  }

  /**
   * Edits all events in a series by updating their properties based on the provided change.
   *
   * @param seriesId the ID of the series to edit
   * @param change   the change to be applied to all events in the series
   * @throws IllegalArgumentException if the series ID is null or no events found for the series
   */
  public void editSeries(Long seriesId, TypingChange change) {
    if (seriesId == null) {
      throw new IllegalArgumentException("Series ID cannot be null");
    }

    List<Event> seriesEvents = recurringSeries.get(seriesId);
    if (seriesEvents == null || seriesEvents.isEmpty()) {
      throw new IllegalArgumentException("No events found for the given series ID");
    }

    for (Event event : seriesEvents) {
      EventIdentifier id = new EventIdentifier(event.getSubject(),
              event.getStartTime(), event.getEndTime(), event.getSeriesId());
      editEvent(id, change, true);
    }
  }

  /**
   * Edits a list of existing events in the calendar by updating their properties
   * based on the provided values. The method ensures that the updated event data is
   * consistent with the existing events in the calendar.
   *
   * @param events a list of events containing the updated details to be applied to
   *               existing calendar events
   * @throws IllegalArgumentException if the provided list of events is null empty
   *                                  or any event does not exist
   */
  @Override
  public void editEvents(List<Event> events, TypingChange change) {
    if (events == null || events.isEmpty()) {
      throw new IllegalArgumentException("Events cannot be empty or have no value");
    }

    Event firstEvent = events.get(0);
    LocalDate startDate = firstEvent.getStartTime().toLocalDate();
    Long seriesId = events.get(0).getSeriesId();
    editEventsHelper(events, change, seriesId, startDate);
  }

  private void editEventsHelper(List<Event> events, TypingChange change, long seriesId,
                                LocalDate startDate) {
    Event firstEvent = events.get(0);
    Event originalEvent = null;
    List<Event> eventsOnDate = getEventsOnDate(startDate);
    for (Event e : eventsOnDate) {
      if (e.getSubject().equals(firstEvent.getSubject())
              && e.getStartTime().getHour() == firstEvent.getStartTime().getHour()) {
        originalEvent = e;
        break;
      }
    }

    if (originalEvent == null) {
      throw new IllegalArgumentException("Event does not exist");
    }
    if (events.size() == 1) {
      EventIdentifier id = new EventIdentifier(originalEvent.getSubject(),
              originalEvent.getStartTime(), originalEvent.getEndTime());
      editEvent(id, change, true);
    } else {
      List<Event> updatedEvents = recurringSeries.get(seriesId);
      for (Event event : events) {
        EventIdentifier id = new EventIdentifier(event.getSubject(),
                event.getStartTime(), event.getEndTime());
        Event updated = editEvent(id, change, true);
        updatedEvents.remove(event);
        updatedEvents.add(updated);
      }
    }
  }


  private Event changeEventProperty(Event og, TypingChange change, boolean isSeries) {
    CalendarEvent.EventBuilder eventBuilder = new CalendarEvent.EventBuilder()
            .setSubject(og.getSubject())
            .setStartTime(og.getStartTime())
            .setEndTime(og.getEndTime())
            .setDesc(og.getDesc())
            .setSeriesId(og.getSeriesId());

    if (og.getLocation() != null) {
      eventBuilder.setLocation(og.getLocation().toString());
    }

    if (og.getStatus() != null) {
      eventBuilder.setStatus(og.getStatus().toString());
    }
    switch (change.getType()) {
      case SUBJECT:
        if (change.getNewValue().toString().isEmpty()) {
          throw new IllegalArgumentException("Subject can't be empty.");
        }
        eventBuilder.setSubject((String) change.getNewValue());
        break;
      case START:
        LocalDateTime newStartTime = changeTime(og, change, isSeries);
        eventBuilder.setStartTime(newStartTime);
        break;
      case END:
        LocalDateTime endTime = changeTime(og, change, isSeries);
        eventBuilder.setEndTime(endTime);
        break;
      case LOCATION:
        eventBuilder.setLocation((String) change.getNewValue());
        break;
      case STATUS:
        eventBuilder.setStatus((String) change.getNewValue());
        break;
      case DESCRIPTION:
        eventBuilder.setDesc((String) change.getNewValue());
        break;
      default:
        throw new IllegalArgumentException("Unknown event type");
    }

    return eventBuilder.build();
  }

  private LocalDateTime changeTime(Event og, TypingChange change, boolean isSeries) {
    DateTimeFormatter formatTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    LocalDateTime newTime = null;
    if (isSeries) {
      if (change.getType() == PropertyType.START) {
        LocalDateTime changeStart = LocalDateTime.parse((String) change.getNewValue(), formatTime);
        newTime = og.getStartTime().toLocalDate().atTime(changeStart.getHour(),
                changeStart.getMinute());
      } else if (change.getType() == PropertyType.END) {
        LocalDateTime changeEnd = LocalDateTime.parse((String) change.getNewValue(), formatTime);
        newTime = og.getEndTime().toLocalDate().atTime(changeEnd.getHour(), changeEnd.getMinute());
      }

    } else {
      try {
        newTime = LocalDateTime.parse((String) change.getNewValue(), formatTime);
      } catch (Exception e) {
        throw new IllegalArgumentException("Incorrect date format. Please use yyyy-MM-dd " +
                "for dates " + "and HH:mm for time");
      }
    }

    return newTime;
  }


  /**
   * Retrieves a list of events scheduled on the specified date. Events are included
   * if the specified date falls within their start and end dates.
   *
   * @param date the date for which events should be retrieved
   * @return a list of events on the date or an empty list if no matching events are found
   * @throws IllegalArgumentException if the provided date is null
   */
  @Override
  public List<Event> getEventsOnDate(LocalDate date) {
    if (date == null) {
      throw new IllegalArgumentException("Date cannot be null");
    }
    ArrayList<Event> allEventsOnThisDate = new ArrayList<>();
    List<Event> events = this.allEvents.get(date);
    if (events != null) {
      for (Event e : events) {
        LocalDate startDate = e.getStartTime().toLocalDate();
        LocalDate endDate = e.getEndTime().toLocalDate();
        if (!(date.isBefore(startDate) || date.isAfter(endDate))) {
          allEventsOnThisDate.add(e);
        }
      }
    }
    return allEventsOnThisDate;
  }

  @Override
  public List<Event> getEventsBetween(LocalDateTime dateFrom, LocalDateTime dateTo) {
    if (dateFrom == null || dateTo == null) {
      throw new IllegalArgumentException("Date cannot be null");
    }

    ArrayList<Event> allEventsBetween = new ArrayList<>();
    for (LocalDateTime date = dateFrom; date.isBefore(dateTo.plusDays(1));
         date = date.plusDays(1)) {
      List<Event> eventsToAdd = getEventsOnDate(date.toLocalDate());

      for (Event e : eventsToAdd) {
        if ((e.getStartTime().isEqual(dateFrom) || e.getStartTime().isAfter(dateFrom))
                && (e.getEndTime().isEqual(dateTo) || e.getEndTime().isBefore(dateTo))) {
          allEventsBetween.add(e);
        }
      }
    }
    return allEventsBetween;
  }


  @Override
  public String getStatusMessage(LocalDateTime date) {
    if (date == null) {
      throw new IllegalArgumentException("Date cannot be null");
    }

    List<Event> events = this.allEvents.get(date.toLocalDate());
    if (events != null && !events.isEmpty()) {
      for (Event event : events) {
        if (!event.getEndTime().isBefore(date) && !event.getStartTime().isAfter(date)) {
          return "Busy.";
        }
      }
    }
    return "Available.";
  }

  private int getIncreaseDaysBy(LocalDateTime startTime, String repeatWhen) {
    DayOfWeek currentWeekDay = startTime.getDayOfWeek();
    int number;
    DayOfWeek newDay = null;
    ArrayList<Character> days = new ArrayList<Character>(
            Arrays.asList('M', 'T', 'W', 'R', 'F', 'S', 'U'));

    if (days.contains(repeatWhen.charAt(0))) {
      if (repeatWhen.charAt(0) == 'M') {
        newDay = DayOfWeek.MONDAY;
      } else if (repeatWhen.charAt(0) == 'T') {
        newDay = DayOfWeek.TUESDAY;
      } else if (repeatWhen.charAt(0) == 'W') {
        newDay = DayOfWeek.WEDNESDAY;
      } else if (repeatWhen.charAt(0) == 'R') {
        newDay = DayOfWeek.THURSDAY;
      } else if (repeatWhen.charAt(0) == 'F') {
        newDay = DayOfWeek.FRIDAY;
      } else if (repeatWhen.charAt(0) == 'S') {
        newDay = DayOfWeek.SATURDAY;
      } else if (repeatWhen.charAt(0) == 'U') {
        newDay = DayOfWeek.SUNDAY;
      }
    } else {
      throw new IllegalArgumentException("Not a valid day.");
    }
    assert newDay != null;
    if (currentWeekDay.equals(newDay)) {
      number = 0;
    } else {
      number = (newDay.getValue() - currentWeekDay.getValue()) % 7;
    }
    return number;
  }

  public String getName() {
    return name;
  }


  private void validateRecurringEventList(List<Event> events) {
    if (events == null || events.isEmpty()) {
      throw new IllegalArgumentException("Events list cannot be null or empty");
    }

    LocalDateTime startTime = events.get(0).getStartTime();
    for (Event e : events) {
      if (!e.getStartTime().toLocalDate().equals(e.getEndTime().toLocalDate())) {
        throw new IllegalArgumentException("Event spans multiple days");
      } else if (e.getStartTime().getHour() != startTime.getHour() &&
              e.getStartTime().getMinute() == startTime.getMinute()) {
        throw new IllegalArgumentException("Start times must all be the same");
      }
    }
  }

  private CalendarEvent buildRecurringEvent(Event e, long seriesId) {
    CalendarEvent.EventBuilder builder = new CalendarEvent.EventBuilder()
            .setSubject(e.getSubject())
            .setStartTime(e.getStartTime())
            .setEndTime(e.getEndTime())
            .setSeriesId(seriesId)
            .setDesc(e.getDesc());

    if (e.getLocation() != null) {
      builder.setLocation(e.getLocation().toString());
    }

    if (e.getStatus() != null) {
      builder.setStatus(e.getStatus().toString());
    }

    return builder.build();
  }

  private void safelyAddEvent(CalendarEvent updated, List<Event> addedEvents) {
    try {
      this.createEvent(updated);
      addedEvents.add(updated);
    } catch (IllegalArgumentException ex) {
      rollbackAddedEvents(addedEvents);
      throw new IllegalArgumentException("Duplicate exists in series");
    }
  }

  private void rollbackAddedEvents(List<Event> addedEvents) {
    for (Event addedEvent : addedEvents) {
      List<Event> dateEvents = allEvents.get(addedEvent.getStartTime().toLocalDate());
      if (dateEvents != null) {
        dateEvents.remove(addedEvent);
        if (dateEvents.isEmpty()) {
          allEvents.remove(addedEvent.getStartTime().toLocalDate());
        }
      }
    }
  }


  @Override
  public List<Event> eventsToBeShown(LocalDate date) {
    List<Event> events = getEventsBetween(date.atStartOfDay(),
            LocalDateTime.of(9999, 12, 31, 0, 0));
    if (events.size() >= 10) {
      LocalDateTime endTime = events.get(9).getEndTime();
      return getEventsBetween(date.atStartOfDay(), endTime);
    } else {
      return events;
    }
  }

  @Override
  public List<String> allEvents() {

    ArrayList<String> events = new ArrayList<>();
    for (List<Event> allEvents : allEvents.values()) {
      for (Event event : allEvents) {
        events.add(event.getSubject() + " " + event.getStartTime().toString());
      }
    }
    return events;
  }

  @Override
  public Event findEvent(String eventName) {
    for (List<Event> allEvents : allEvents.values()) {
      for (Event event : allEvents) {
        if (event.getSubject().equals(eventName)) {
          return event;
        }
      }
    }
    throw new IllegalArgumentException("Event not found");
  }
}
