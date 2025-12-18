package model;

import java.util.List;

/**
 * Interface that extends MultipleCalendarModel and provides functionality for getting all
 * calendar names.
 */
public interface MultipleCalendarModelAllNames extends  MultipleCalendarModel {

  /**
   * Gets all calendar names.
   * @return a list of all calendar names.
   */
  List<String> getAllNames();
}
