import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import model.CalendarEvent;
import model.CalendarModelGUISupport;
import model.Event;
import model.Identifier;
import model.TypingChange;

/**
 * Represents a mock model.
 */
class MockModel implements CalendarModelGUISupport {
  private StringBuilder sb;
  private Map<LocalDate, List<Event>> events;

  /**
   * Builds a mock model.
   *
   * @param stringbuilder log kept
   */
  public MockModel(StringBuilder stringbuilder) {
    sb = stringbuilder;
  }

  /**
   * Adds an event to the calendar.
   *
   * @param event given event
   */

  @Override
  public void createEvent(Event event) {
    sb.append("addEvent has been called, event added \n");
  }

  /**
   * Adds a series of events to the calendar.
   *
   * @param events given list of events
   */

  @Override
  public void createEvents(List<Event> events) {
    sb.append("addEvents has been called, events added \n");
  }

  /**
   * Edits an event on the calendar.
   *
   * @param id     id of the event
   * @param change change being made to event
   */

  @Override
  public Event editEvent(Identifier id, TypingChange change, boolean isSeries) {
    sb.append("editEvent has been called, event edited \n");
    return null;
  }

  /**
   * Edits a series of events.
   */

  @Override
  public void editEvents(List<Event> event, TypingChange change) {
    sb.append("editEvents has been called, events edited \n");

  }

  @Override
  public ArrayList<Event> getEventsOnDate(LocalDate date) {
    sb.append("getEventsOnDate has been called, dates shown \n");
    return new ArrayList<>();
  }

  @Override
  public void editSeries(Long seriesId, TypingChange change) {
    sb.append("editSeries has been called, event edited \n");
  }

  @Override
  public ArrayList<Event> getEventsBetween(LocalDateTime dateFrom, LocalDateTime dateTo) {
    sb.append("getEventsBetween has been called, dates shown \n");
    return new ArrayList<>();
  }

  @Override
  public String getStatusMessage(LocalDateTime date) {
    sb.append("showStatus has been called, status shown \n");
    return sb.toString();
  }

  @Override
  public ZoneId getTimezone() {
    return null;
  }

  @Override
  public void setTimezone(String timezone) {
    // empty because unneeded

  }

  /**
   * Retrieves the name of the calendar.
   *
   * @return the name of the calendar as a String
   */
  @Override
  public String getName() {
    return "";
  }


  @Override
  public void parseEditEvents(LocalDateTime date, String subject, TypingChange change) {
    sb.append("edit events has been called \n");
  }

  @Override
  public void createAllEvents(Event startEvent, String repeatWhen, int repeatInterval) {
    sb.append("createAllEvents has been called, event created \n");
  }

  @Override
  public void parseEditEventSeries(String subject, LocalDateTime date, TypingChange change) {
    sb.append("parseEditEventSeries has been called, event edited \n");
  }

  @Override
  public List<Event> eventsToBeShown(LocalDate date) {
    Event event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(LocalDateTime.now())
            .setEndTime(LocalDateTime.now())
            .build();
    return new ArrayList<>(Collections.singletonList(event));
  }

  @Override
  public List<String> allEvents() {
    return List.of();
  }

  @Override
  public Event findEvent(String eventName) {
    Event event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(LocalDateTime.now())
            .setEndTime(LocalDateTime.now())
            .build();
    return event;
  }
}