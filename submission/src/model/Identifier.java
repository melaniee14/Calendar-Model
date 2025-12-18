package model;

import java.time.LocalDateTime;

/**
 * Represents a unique identifier for objects within a system.
 * Classes implementing this interface define specific equality checks
 * and hash code computation logic to identify and distinguish objects.
 */
public interface Identifier {

  /**
   * Compares this Model.Identifier object to another object to determine equality.
   *
   * @param o the object to compare with this Model.EventIdentifier
   * @return true if the specified object is equal to this Model.EventIdentifier otherwise false
   */
  public boolean equals(Object o);

  /**
   * Computes the hash code for this Model.Identifier object using its fields.
   * The hash code is calculated based on the hash codes of the subject
   * startTime and endTime fields with an additional constant value added.
   *
   * @return an integer representing the hash code of this Model.EventIdentifier
   */
  public int hashCode();

  /**
   * Gets the start time of this event id.
   *
   * @return the start time
   */
  public LocalDateTime getStartTime();

  /**
   * Gets the end time of this event id.
   *
   * @return the end time
   */
  public LocalDateTime getEndTime();

  /**
   * Gets the subject of this event id.
   *
   * @return the subject
   */
  public String getSubject();

}
