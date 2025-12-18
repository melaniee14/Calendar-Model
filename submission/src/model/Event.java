package model;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Defines the contract for an event within the calendar model.
 * Implementations of this interface represent specific details
 * about an event including its subject timing location
 * series association status and description.
 */
public interface Event {
  /**
   * Retrieves the subject of the event.
   *
   * @return the subject of the event as a String
   */
  public String getSubject();


  /**
   * Retrieves the start time of the event.
   *
   * @return the start time of the event as a LocalDateTime
   */
  public LocalDateTime getStartTime();


  /**
   * Retrieves the end time of the event.
   *
   * @return the end time of the event as a LocalDateTime
   */
  public LocalDateTime getEndTime();

  /**
   * Retrieves the location of the event.
   *
   * @return the location of the event as an Model.EventLocation
   */
  public EventLocation getLocation();

  /**
   * Retrieves the id of the event if it is part of a series.
   *
   * @return the id of the event as a Long
   */
  public Long getSeriesId();

  /**
   * Retrieves the status of the event.
   *
   * @return the status of the event as an Model.EventStatus
   */
  public EventStatus getStatus();

  /**
   * Retrieves the description of the event.
   *
   * @return the description of the event as a String
   */
  public String getDesc();

  /**
   * Sets a new timezone for a calendar event based on a timezone id.
   *
   * @param newTimezone the timezone to be changed to
   * @return a new CalendarEvent with the new timezone
   */
  public Event newTimezone(ZoneId newTimezone);

  public ZoneId getTimezone();
}