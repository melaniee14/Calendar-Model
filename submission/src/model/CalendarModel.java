package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface for the class that handles adding, editing, and retrieving events.
 */
public interface CalendarModel {

  /**
   * Adds an event to the calendar.
   *
   * @param event given event
   */
  public void createEvent(Event event);

  /**
   * Adds a series of events to the calendar.
   *
   * @param events given list of events
   */
  public void createEvents(List<Event> events);

  /**
   * Edits an event on the calendar.
   *
   * @param id     id of the event
   * @param change change being made to event
   */
  public Event editEvent(Identifier id, TypingChange change, boolean isSeries);

  /**
   * Edits a series of events.
   *
   * @param events given list of events.
   */
  public void editEvents(List<Event> events, TypingChange change);

  /**
   * Gets events on a date.
   *
   * @param date to get events on
   * @return the events on date.
   */
  public List<Event> getEventsOnDate(LocalDate date);


  /**
   * Edits all events in a series by updating their properties based on the provided change.
   *
   * @param seriesId the ID of the series to edit
   * @param change   the change to be applied to all events in the series
   * @throws IllegalArgumentException if the series ID is null or no events found for the series
   */
  public void editSeries(Long seriesId, TypingChange change);


  /**
   * Retrieves all events that occur between the specified start and end dates, inclusive.
   * The method traverses each date within the range and gathers the events scheduled for those
   * dates.
   *
   * @param dateFrom the starting date of the range
   * @param dateTo   the ending date of the range
   * @return a list of events between the specified dates or an empty list if no events are found
   * @throws IllegalArgumentException if either dateFrom or dateTo is null
   */
  public List<Event> getEventsBetween(LocalDateTime dateFrom, LocalDateTime dateTo);

  /**
   * Returns the status message for a specified date based on the events scheduled on that date.
   * If there are no events scheduled the status is available or it is busy.
   *
   * @param date the date for which the status message is to be retrieved
   * @return either available or busy depending on the number of events
   *     scheduled on the specified date
   * @throws IllegalArgumentException if the provided date is null
   */
  public String getStatusMessage(LocalDateTime date);




}
