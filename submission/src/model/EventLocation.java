package model;

/**
 * Represents the location of an event.
 * This enum defines two possible locations for events:
 * ONLINE for virtual events and PHYSICAL for in-person events.
 */
public enum EventLocation {
  ONLINE,
  PHYSICAL;

  /**
   * Returns the name of the event location.
   */
  @Override
  public String toString() {
    return name();
  }
}