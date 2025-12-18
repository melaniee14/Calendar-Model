package controller;

import java.time.LocalDateTime;

import model.MultipleCalendarModel;

/**
 * Represents a command to copy an event from one calendar to another in the calendar model.
 * This command allows transferring an event with a specific name and start time to a target
 * calendar with a new start time.
 */
public class CopyEvent implements MultipleCalendarCommand {
  private final String eventName;
  private final LocalDateTime eventTime;
  private final String targetCal;
  private final LocalDateTime targetTime;

  /**
   * Constructs a CopyEvent object to represent a command for copying an event
   * from one calendar to another.
   *
   * @param eventName  the name of the event to be copied
   * @param eventTime  the original start time of the event
   * @param targetCal  the name of the target calendar where the event will be copied
   * @param targetTime the new start time of the copied event
   */
  public CopyEvent(String eventName, LocalDateTime
      eventTime, String targetCal, LocalDateTime targetTime) {
    this.eventName = eventName;
    this.eventTime = eventTime;
    this.targetCal = targetCal;
    this.targetTime = targetTime;
  }


  @Override
  public void execute(MultipleCalendarModel model) {
    model.copyEvent(eventName, eventTime, targetCal, targetTime);
  }
}
