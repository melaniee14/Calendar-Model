package controller;

import model.MultipleCalendarModel;
import model.PropertyType;

/**
 * Represents a command to edit properties of a specific calendar within a calendar model.
 * The command updates a specified property of the calendar using the provided new value.
 */
public class EditCalendar implements MultipleCalendarCommand {
  private final String name;
  private final PropertyType property;
  private final String newValue;

  /**
   * Constructs a new Controller.EditCalendar command.
   *
   * @param name     the name of the calendar to be edited
   * @param property the property to be edited
   * @param newValue the new value to be assigned to the property
   */
  public EditCalendar(String name, PropertyType property, String newValue) {
    this.name = name;
    this.property = property;
    this.newValue = newValue;
  }

  @Override
  public void execute(MultipleCalendarModel currentCal) {
    currentCal.editCalendar(name, property, newValue);
  }
}
