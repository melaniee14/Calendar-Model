package model;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface that extends the functionality by adding methods related
 * to showing and finding events.
 */
public interface CalendarModelGUISupport extends CalendarModelAllHelpers {

  /**
   * Finds the total amount of events that needs to be shown to the view after a certain date.
   * @param date date that events should be shown after
   * @return events after that date
   */
  List<Event> eventsToBeShown(LocalDate date);

  /**
   * Gets all event names in the calendar.
   * @return the names of all the events in the calendar
   */
  List<String> allEvents();

  /**
   * Finds the event in the calendar using its subject.
   * @param eventName the name of the event
   * @return the matching event
   */
  Event findEvent(String eventName);
}
