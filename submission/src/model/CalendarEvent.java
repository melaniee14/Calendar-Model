package model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Represents an event with specific details such as subject, timing, location,
 * status, timezone and description.
 */
public class CalendarEvent implements Event {
  private final String subject;
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;
  private final EventLocation location;
  private final Long seriesId;
  private final EventStatus status;
  private final String desc;
  private final ZoneId timezone;

  private CalendarEvent(EventBuilder builder) {
    this.subject = builder.subject;
    this.startTime = builder.startTime;
    this.endTime = builder.endTime;
    this.location = builder.location;
    this.seriesId = builder.seriesId;
    this.status = builder.status;
    this.desc = builder.desc;
    this.timezone = builder.timezone;
  }

  /**
   * A builder class for constructing Model.CalendarEvent objects with configurable properties.
   */
  public static class EventBuilder {
    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EventLocation location;
    private Long seriesId;
    private EventStatus status;
    private String desc;
    private ZoneId timezone;


    /**
     * Creates a new EventBuilder object.
     */
    public EventBuilder() {
      this.subject = "";
      this.startTime = null;
      this.endTime = null;
      this.location = null;
      this.seriesId = null;
      this.status = null;
      this.desc = "";
      ZoneId timezone = null;
    }

    /**
     * Sets the subject of the event.
     *
     * @param subject the given subject
     * @return the updated object
     */
    public EventBuilder setSubject(String subject) {
      if (subject.isEmpty()) {
        throw new IllegalArgumentException("Subject cannot be empty.");
      }
      this.subject = subject;
      return this;
    }

    /**
     * Sets the start time of the event.
     *
     * @param startTime given start time of the event
     * @return the updated object
     */
    public EventBuilder setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
      return this;
    }

    /**
     * Sets the end time of the event.
     *
     * @param endTime given end time of event
     * @return the updated object
     */
    public EventBuilder setEndTime(LocalDateTime endTime) {
      this.endTime = endTime;
      return this;
    }

    /**
     * Sets the location of the event.
     *
     * @param location where the event is located
     * @return the updated object
     */
    public EventBuilder setLocation(String location) {
      if (location != null && !location.isEmpty()) {
        try {
          this.location = EventLocation.valueOf(location.toUpperCase());
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("Invalid location type. Must be ONLINE or PHYSICAL");
        }
      }
      return this;
    }


    /**
     * Sets the id of an event if it is part of a series.
     *
     * @param seriesId id of the given series
     * @return the updated object
     */
    public EventBuilder setSeriesId(Long seriesId) {
      this.seriesId = seriesId;
      return this;
    }

    /**
     * Sets the status of the event.
     *
     * @param status the visibility status of the event, either "PUBLIC" or "PRIVATE"
     * @return the updated EventBuilder object
     * @throws IllegalArgumentException if the provided status is null, empty, or not a
     *                                  valid status type
     */
    public EventBuilder setStatus(String status) {
      if (status != null && !status.isEmpty()) {
        try {
          this.status = EventStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException("Invalid status type. Must be PUBLIC or PRIVATE");
        }
      }
      return this;
    }


    /**
     * Sets the description of the event.
     *
     * @param desc the description of the event
     * @return the updated EventBuilder object
     */
    public EventBuilder setDesc(String desc) {
      this.desc = desc;
      return this;
    }

    /**
     * Sets the timezone based on a specific ZoneId.
     *
     * @param timezone the timezone to be set to as an id
     * @return an event builder with the new timezone
     */
    public EventBuilder setTimezone(ZoneId timezone) {
      this.timezone = timezone;
      return this;
    }

    /**
     * Sets the timezone based on a timezone string given in proper format.
     *
     * @param timezone the timezone to be set to as a string
     * @return an event builder with the new timezone
     */
    public EventBuilder setTimezone(String timezone) {
      if (timezone != null && !timezone.isEmpty()) {
        try {
          this.timezone = ZoneId.of(timezone);
        } catch (Exception e) {
          throw new IllegalArgumentException("Invalid timezone format");
        }
      }
      return this;
    }

    /**
     * Builds the event.
     *
     * @return the new event
     */
    public CalendarEvent build() {
      if (endTime == null || startTime == null) {
        throw new IllegalArgumentException("End time or start time cannot be null.");
      }
      if (endTime.isBefore(startTime)) {
        throw new IllegalArgumentException("End time cannot be before start time");
      }
      return new CalendarEvent(this);
    }


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

  public ZoneId getTimezone() {
    return timezone;
  }

  @Override
  public EventLocation getLocation() {
    return location;
  }

  @Override
  public Long getSeriesId() {
    return seriesId;
  }

  @Override
  public EventStatus getStatus() {
    return status;
  }

  @Override
  public String getDesc() {
    return desc;
  }

  /**
   * Compares this Model.CalendarEvent object with another object to determine equality.
   * Two Model.CalendarEvent objects are considered equal if their start times end times
   * and subjects are equal.
   *
   * @param o the object to compare with this Model.CalendarEvent
   * @return true if the specified object is equal to this Model.CalendarEvent otherwise false
   */
  public boolean equals(Object o) {
    if (!(o instanceof CalendarEvent)) {
      return false;
    }
    CalendarEvent e = (CalendarEvent) o;

    return this.startTime.equals(e.startTime) && this.endTime.equals(e.endTime)
            && this.subject.equals(e.subject);
  }


  @Override
  public Event newTimezone(ZoneId newTimezone) {
    if (newTimezone == null) {
      throw new IllegalArgumentException("New timezone cannot be null");
    }

    ZoneId currentZone = this.timezone != null ? this.timezone : ZoneId.systemDefault();

    if (startTime == null || endTime == null) {
      return new EventBuilder()
              .setSubject(subject)
              .setStartTime(startTime)
              .setEndTime(endTime)
              .setLocation(location != null ? location.toString() : null)
              .setSeriesId(seriesId)
              .setStatus(status != null ? status.toString() : null)
              .setDesc(desc)
              .setTimezone(newTimezone)
              .build();
    }

    ZonedDateTime zonedStart = ZonedDateTime.of(startTime, currentZone);
    ZonedDateTime zonedEnd = ZonedDateTime.of(endTime, currentZone);

    LocalDateTime newStart = zonedStart.withZoneSameInstant(newTimezone).toLocalDateTime();
    LocalDateTime newEnd = zonedEnd.withZoneSameInstant(newTimezone).toLocalDateTime();

    return new EventBuilder()
            .setSubject(subject)
            .setStartTime(newStart)
            .setEndTime(newEnd)
            .setLocation(location != null ? location.toString() : null)
            .setSeriesId(seriesId)
            .setStatus(status != null ? status.toString() : null)
            .setDesc(desc)
            .setTimezone(newTimezone)
            .build();
  }


  /**
   * Computes the hash code for the Model.CalendarEvent object using its fields.
   * The hash code is calculated based on the hash codes of the
   * subject startTime endTime location and adds a constant value.
   *
   * @return the hash code of this Model.CalendarEvent
   */
  public int hashCode() {
    return subject.hashCode() + startTime.hashCode() * endTime.hashCode() + 1000;
  }

}
