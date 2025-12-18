package model;

import java.time.LocalDateTime;

/**
 * Identifies an event for editing.
 */
public class EventIdentifier implements Identifier {
  private final String subject;
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;
  private Long seriesId;

  /**
   * Constructs an Model.EventIdentifier object to uniquely identify an event for editing purposes.
   *
   * @param subject   the title or subject of the event
   * @param startTime the start time of the event
   * @param endTime   the end time of the event
   */
  public EventIdentifier(String subject, LocalDateTime startTime, LocalDateTime endTime) {
    this.subject = subject;
    this.startTime = startTime;
    this.endTime = endTime;
    this.seriesId = null;
  }

  /**
   * Constructs an EventIdentifier object to uniquely identify an event for editing purposes.
   *
   * @param subject   the title or subject of the event
   * @param startTime the start time of the event
   * @param endTime   the end time of the event
   * @param seriesId  the ID of the series the event belongs to,
   *                 or null if the event is not part of a series
   */
  public EventIdentifier(String subject, LocalDateTime startTime, LocalDateTime endTime,
                         Long seriesId) {
    this.subject = subject;
    this.startTime = startTime;
    this.endTime = endTime;
    this.seriesId = seriesId;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof EventIdentifier)) {
      return false;
    }

    EventIdentifier e = (EventIdentifier) o;
    return this.endTime.equals(e.endTime)
      && this.startTime.equals(e.startTime)
      && this.subject.equals(e.subject);
  }


  @Override
  public int hashCode() {
    return subject.hashCode() + startTime.hashCode() * endTime.hashCode() + 1000;
  }

  @Override
  public String getSubject() {
    return subject;
  }

  @Override
  public LocalDateTime getStartTime() {
    return startTime;
  }

  @Override
  public LocalDateTime getEndTime() {
    return endTime;
  }

}
