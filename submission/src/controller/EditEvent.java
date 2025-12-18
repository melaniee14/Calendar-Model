package controller;

import model.CalendarModelAllHelpers;
import model.Identifier;
import model.TypingChange;

/**
 * Represents a command to edit an existing event in the calendar model.
 * This command updates a specific property of an event identified by an Model.EventIdentifier.
 */
public class EditEvent implements CalendarCommand {
  private final Identifier identifier;
  private final TypingChange change;

  /**
   * Constructs an Controller.EditEvent command with the specified event identifier
   * and property change.
   *
   * @param identifier the identifier of the event to be edited
   * @param change     the property change to be applied to the event
   */
  public EditEvent(Identifier identifier, TypingChange change) {
    this.identifier = identifier;
    this.change = change;
  }

  /**
   * Executes the edit event command on the given calendar model.
   * This method updates the specified event with the provided property changes.
   *
   * @param model the calendar model on which the event editing operation will be performed
   */
  @Override
  public void execute(CalendarModelAllHelpers model) {
    model.editEvent(identifier, change, false);
  }
}
