package controller;

import model.CalendarEvent;
import model.CalendarModelGUISupport;
import model.Event;
import model.EventIdentifier;
import model.Identifier;
import model.MultipleCalendarModelAllNames;

import model.PropertyChange;
import model.PropertyType;
import model.TypingChange;
import view.CalendarGUIView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * A controller implementation specifically designed for GUI-based interaction with
 * the calendar application. This controller integrates both a multiple calendar model
 * (to manage multiple calendars) and a single calendar model
 * (for interacting with individual calendars).
 */
public class CalendarControllerGUIImpl implements CalendarControllerGUI {
  private final MultipleCalendarModelAllNames multipleModel;
  private CalendarModelGUISupport activeCalendarModel;
  private final CalendarGUIView view;

  /**
   * Constructs a CalendarControllerGUIImpl, connecting the models and view.
   *
   * @param multipleModel The multiple calendar model for managing multiple calendars.
   * @param view          The GUI view to render data and capture user input.
   */
  public CalendarControllerGUIImpl(MultipleCalendarModelAllNames multipleModel,
                                   CalendarGUIView view) {
    if (multipleModel == null || view == null) {
      throw new IllegalArgumentException("Models and View cannot be null.");
    }
    this.multipleModel = multipleModel;
    this.view = view;
    this.view.setController(this);

    String currentCalendarName = multipleModel.getCurrentCalendarName();

    if (currentCalendarName == null || currentCalendarName.isEmpty()) {
      createDefaultCalendar();
      currentCalendarName = multipleModel.getCurrentCalendarName();
    }
    this.activeCalendarModel = multipleModel.useCalendar(currentCalendarName);
  }

  @Override
  public void run() {
    view.display();
  }

  @Override
  public void viewSchedule(LocalDate date) {
    if (activeCalendarModel == null) {
      view.showErrorMessage("No active calendar selected. Please switch to a calendar first.");
      return;
    }
    try {
      List<Event> events = activeCalendarModel.eventsToBeShown(date);
      view.updateScheduleView(events);
    } catch (Exception e) {
      view.showErrorMessage("Failed to retrieve schedule: " + e.getMessage());
    }
  }


  @Override
  public void addEvent(String subject, String startWithDate, String endWithDate,
                       String location, String description, String status) {
    if (activeCalendarModel == null) {
      view.showErrorMessage("No active calendar selected. Please switch to a calendar first.");
      return;
    }
    try {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
      Event event;
      CalendarEvent.EventBuilder builder = new CalendarEvent.EventBuilder()
              .setSubject(subject)
              .setStartTime(LocalDateTime.parse(startWithDate, formatter))
              .setEndTime(LocalDateTime.parse(endWithDate, formatter))
              .setTimezone(activeCalendarModel.getTimezone());
      if (!location.equals("N/A")) {
        builder.setLocation(location);
      }
      if (!status.equals("N/A")) {
        builder.setStatus(status);
      }
      if (!description.isEmpty() && !description.equals("A description is optional.")) {
        builder.setDesc(description);
      }
      event = builder.build();
      activeCalendarModel.createEvent(event);
      view.showSuccessMessage("Event added successfully!");
      List<Event> events = activeCalendarModel.eventsToBeShown(
              LocalDate.of(0, 1, 1));
      view.updateScheduleView(activeCalendarModel.eventsToBeShown(
              events.get(0).getStartTime().toLocalDate()));
    } catch (Exception e) {
      view.showErrorMessage("Failed to add event: " + e.getMessage());
    }
  }

  @Override
  public void editEvent(String eventName, String changeType, String change) {
    if (activeCalendarModel == null) {
      view.showErrorMessage("No active calendar selected. Please switch to a calendar first.");
      return;
    }
    try {
      Event eventToEdit = activeCalendarModel.findEvent(eventName);
      if (eventToEdit == null) {
        view.showErrorMessage("Event not found: " + eventName);
        return;
      }

      Identifier eventId = new EventIdentifier(eventToEdit.getSubject(),
              eventToEdit.getStartTime(), eventToEdit.getEndTime());

      TypingChange changeToMake = changeIfDate(changeType, change, eventToEdit);
      activeCalendarModel.editEvent(eventId, changeToMake, false);

      view.showSuccessMessage("Event updated successfully!");
      List<Event> events = activeCalendarModel.eventsToBeShown(
              LocalDate.of(0, 1, 1));
      view.updateScheduleView(activeCalendarModel.eventsToBeShown(
              events.get(0).getStartTime().toLocalDate()));
    } catch (Exception e) {
      view.showErrorMessage("Failed to edit event: " + e.getMessage());
    }
  }

  @Override
  public void switchCalendar(String calendarName) {
    try {
      activeCalendarModel = multipleModel.useCalendar(calendarName);
      view.showSuccessMessage("Switched to calendar: " + calendarName);
      view.updateScheduleView(
              activeCalendarModel.eventsToBeShown(LocalDate.of(0, 1, 1)));


    } catch (Exception e) {
      view.showErrorMessage("Failed to switch calendar: " + e.getMessage());
    }
  }

  @Override
  public void createNewCalendar(String name, String timezone) {
    try {
      multipleModel.createCalendar(name, timezone);
      activeCalendarModel = multipleModel.useCalendar(name);
      view.showSuccessMessage("Created calendar: " + name);

    } catch (Exception e) {
      view.showErrorMessage("Failed to create calendar: " + e.getMessage());
    }
  }

  @Override
  public List<String> getAvailableCalendarNames() {
    return multipleModel.getAllNames();
  }

  public List<String> getAllEventNames() {
    return activeCalendarModel.allEvents();
  }


  private TypingChange changeIfDate(String changeType, String change, Event eventToEdit) {
    if (changeType.equals("START TIME") || changeType.equals("END TIME")) {
      if (changeType.equals("START TIME")) {
        change = eventToEdit.getStartTime().toLocalDate().toString() + "T" + change;
        changeType = "START";
      } else {
        change = eventToEdit.getEndTime().toLocalDate().toString() + "T" + change;
        changeType = "END";
      }
    } else if (changeType.contains("DATE")) {
      String hourAndMinuteString = "";
      if (changeType.equals("START DATE")) {
        hourAndMinuteString = getHourAndMinuteString(eventToEdit, true);
        changeType = "START";
      } else if (changeType.equals("END DATE")) {
        hourAndMinuteString = getHourAndMinuteString(eventToEdit, false);
        changeType = "END";
      }
      change = change + "T" + hourAndMinuteString;
    }
    return new PropertyChange(PropertyType.valueOf(changeType), change);
  }

  private String getHourAndMinuteString(Event eventToEdit, boolean isStart) {
    String hourAndMinuteString;
    if (isStart) {
      hourAndMinuteString = eventToEdit.getStartTime().getHour() + ":"
              + eventToEdit.getStartTime().getMinute();
      if (eventToEdit.getStartTime().getMinute() < 10
              && eventToEdit.getStartTime().getHour() < 10) {
        hourAndMinuteString = "0" + eventToEdit.getStartTime().getHour() + ":"
                + "0" + eventToEdit.getStartTime().getMinute();
      } else if (eventToEdit.getStartTime().getHour() < 10) {
        hourAndMinuteString = "0" + eventToEdit.getStartTime().getHour()
                + ":" + eventToEdit.getStartTime().getMinute();
      } else if (eventToEdit.getStartTime().getMinute() < 10) {
        hourAndMinuteString = eventToEdit.getStartTime().getHour()
                + ":0" + eventToEdit.getStartTime().getMinute();
      }
    } else {
      hourAndMinuteString = eventToEdit.getEndTime().getHour()
              + ":" + eventToEdit.getEndTime().getMinute();
      if (eventToEdit.getEndTime().getMinute() < 10 && eventToEdit.getEndTime().getHour() < 10) {
        hourAndMinuteString = "0" + eventToEdit.getEndTime().getHour()
                + ":0" + eventToEdit.getEndTime().getMinute();
      } else if (eventToEdit.getEndTime().getHour() < 10) {
        hourAndMinuteString = "0" + eventToEdit.getEndTime().getHour()
                + ":" + eventToEdit.getEndTime().getMinute();
      } else if (eventToEdit.getEndTime().getMinute() < 10) {
        hourAndMinuteString = eventToEdit.getEndTime().getHour()
                + ":0" + eventToEdit.getEndTime().getMinute();
      }
    }

    return hourAndMinuteString;
  }


  private void createDefaultCalendar() {
    String defaultCalendarName = "Default Calendar";
    String systemTimezone = ZoneId.systemDefault().toString();
    multipleModel.createCalendar(defaultCalendarName, systemTimezone);
  }


}