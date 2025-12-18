package model;

/**
 * Represents a change in a specific property of an object.
 * This interface defines the contract for encapsulating the type of property
 * being changed and the new value to be assigned to it.
 */
public interface TypingChange {

  /**
   * Returns the type being changed.
   *
   * @return the type being changed
   */
  public PropertyType getType();

  /**
   * Returns the new value being assigned.
   *
   * @return the new value being assigned.
   */
  public Object getNewValue();

}
