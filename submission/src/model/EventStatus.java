package model;

/**
 * Represents the visibility status of an event within the calendar system.
 * An event can either be public or private.
 */
public enum EventStatus {
  PUBLIC,
  PRIVATE;

  /**
   * Returns the name of the event status.
   */
  @Override
  public String toString() {
    return name();
  }
}