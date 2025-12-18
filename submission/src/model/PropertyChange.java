package model;

/**
 * Represents a change in one of the properties of an event.
 * Encapsulates details about the type of property being changed
 * and the new value being assigned.
 */
public class PropertyChange implements TypingChange {
  private final PropertyType type;
  private final Object newValue;

  /**
   * Constructs a new Model.PropertyChange object with the specified type and new value.
   *
   * @param type     the type of property being changed
   * @param newValue the new value being assigned to the property
   */
  public PropertyChange(PropertyType type, Object newValue) {
    this.type = type;
    this.newValue = newValue;
  }

  /**
   * Returns the type of property being changed.
   *
   * @return the type of property being changed
   */
  public PropertyType getType() {
    return type;
  }

  /**
   * Returns the new value being assigned to the property.
   *
   * @return the new value being assigned to the property
   */
  public Object getNewValue() {
    return newValue;
  }
}
