
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import model.CalendarEvent;
import model.CalendarModel;
import model.CalendarModelAllHelpers;
import model.CalendarModelImpl;
import model.Event;
import model.EventIdentifier;
import model.EventLocation;
import model.EventStatus;
import model.MultipleCalendarModel;
import model.MultipleCalendarModelImpl;
import model.PropertyChange;
import model.PropertyType;
import model.TypingChange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Handles all tests for the calendar model.
 */
public class CalendarModelImplTest {
  CalendarModelAllHelpers cal;
  Event sampleEvent;
  LocalDateTime sampleStartTime;
  LocalDateTime sampleEndTime;
  private MultipleCalendarModel model;
  private final String TEST_CALENDAR = "TestCalendar";
  private final String TEST_TIMEZONE = "America/New_York";
  private final String ALTERNATIVE_TIMEZONE = "Europe/London";

  EventLocation location;
  EventLocation location2;
  EventStatus status1;
  EventStatus status2;

  @Before
  public void setUp() {
    cal = new CalendarModelImpl("Test");
    sampleStartTime = LocalDateTime.of(2025, 6, 2, 10, 0);
    sampleEndTime = LocalDateTime.of(2025, 6, 2, 11, 0);
    sampleEvent = new CalendarEvent.EventBuilder()
            .setSubject("Test Model.CalendarEvent")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();
    model = new MultipleCalendarModelImpl();
    location = EventLocation.ONLINE;
    location2 = EventLocation.PHYSICAL;
    status1 = EventStatus.PRIVATE;
    status2 = EventStatus.PUBLIC;
  }


  @Test
  public void testCreateEventSuccessfully() {
    cal.createEvent(sampleEvent);
    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(1, events.size());
    assertEquals(sampleEvent.getSubject(), events.get(0).getSubject());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddNullEvent() {
    cal.createEvent(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithInvalidLocation() {
    CalendarEvent eventWithInvalidLocation = new CalendarEvent.EventBuilder()
            .setSubject("Test Model.CalendarEvent")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("HYBRID")
            .setStatus("PUBLIC")
            .build();

    cal.createEvent(eventWithInvalidLocation);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithInvalidStatus() {
    CalendarEvent eventWithInvalidStatus = new CalendarEvent.EventBuilder()
            .setSubject("Test Model.CalendarEvent")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setStatus("PENDING")
            .build();

    cal.createEvent(eventWithInvalidStatus);
  }

  @Test
  public void testCreateEventWithValidLocationAndStatusValues() {
    CalendarEvent onlineEvent = new CalendarEvent.EventBuilder()
            .setSubject("Online Meeting")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setStatus("PRIVATE")
            .build();
    cal.createEvent(onlineEvent);

    CalendarEvent physicalEvent = new CalendarEvent.EventBuilder()
            .setSubject("Physical Meeting")
            .setStartTime(sampleStartTime.plusHours(2))
            .setEndTime(sampleEndTime.plusHours(2))
            .setLocation("PHYSICAL")
            .setStatus("PUBLIC")
            .build();
    cal.createEvent(physicalEvent);

    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(2, events.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithNullTimes() {
    CalendarEvent eventWithNullTimes = new CalendarEvent.EventBuilder()
            .setSubject("Test Model.CalendarEvent")
            .setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)))
            .setEndTime(null)
            .setLocation("ONLINE")
            .setStatus("PRIVATE")
            .build();

    cal.createEvent(eventWithNullTimes);
    List<Event> events = cal.getEventsOnDate(LocalDate.now());
    assertEquals(1, events.size());
    assertEquals(LocalTime.of(8, 0), events.get(0).getStartTime().toLocalTime());
    assertEquals(LocalTime.of(17, 0), events.get(0).getEndTime().toLocalTime());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddDuplicateEvent() {
    cal.createEvent(sampleEvent);
    cal.createEvent(sampleEvent);
  }

  @Test
  public void testCreateEventsSuccessfully() {
    List<Event> events = new ArrayList<>();
    events.add(sampleEvent);
    events.add(new CalendarEvent.EventBuilder()
            .setSubject("Test Model.CalendarEvent 2")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("PHYSICAL")
            .setStatus("PRIVATE")
            .build());

    cal.createEvents(events);
    assertEquals(2, cal.getEventsOnDate(sampleStartTime.toLocalDate()).size());
  }

  @Test
  public void testCreateAllEvents() {
    cal.createAllEvents(sampleEvent, "MWF", 2);
    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(1, events.size());
    events = cal.getEventsOnDate(sampleStartTime.toLocalDate().plusDays(2));
    assertEquals(1, events.size());
    events = cal.getEventsOnDate(sampleStartTime.toLocalDate().plusDays(4));
    assertEquals(1, events.size());
    events = cal.getEventsOnDate(sampleStartTime.toLocalDate().plusWeeks(1).
            plusDays(4));
    assertEquals(1, events.size());
    events = cal.getEventsOnDate(sampleStartTime.toLocalDate().plusWeeks(2).
            plusDays(4));
    assertEquals(0, events.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventsInvalidDay() {
    cal.createAllEvents(sampleEvent, "XYZ", 2);
    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(0, events.size());
  }


  @Test
  public void testEditEvents() {
    cal.createAllEvents(sampleEvent, "MWF", 2);
    PropertyChange change = new PropertyChange(PropertyType.LOCATION, "PHYSICAL");

    cal.parseEditEvents(sampleStartTime, sampleEvent.getSubject(), change);
    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(EventLocation.PHYSICAL, events.get(0).getLocation());
    events = cal.getEventsOnDate(sampleStartTime.toLocalDate().plusDays(2));
    assertEquals(EventLocation.PHYSICAL, events.get(0).getLocation());
  }

  @Test
  public void testEditEventSeries() {
    cal.createAllEvents(sampleEvent, "MWF", 2);
    PropertyChange change = new PropertyChange(PropertyType.LOCATION, "ONLINE");
    cal.parseEditEventSeries(sampleEvent.getSubject(), sampleStartTime, change);
    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(EventLocation.ONLINE, events.get(0).getLocation());
    events = cal.getEventsOnDate(sampleStartTime.toLocalDate().plusDays(2));
    assertEquals(EventLocation.ONLINE, events.get(0).getLocation());
    events = cal.getEventsOnDate(sampleStartTime.toLocalDate().plusDays(4));
    assertEquals(EventLocation.ONLINE, events.get(0).getLocation());
  }

  @Test
  public void testCreateAllEventSeries() {
    cal.createAllEvents(sampleEvent, "M", 2);
    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(1, events.size());
    events = cal.getEventsOnDate(sampleStartTime.toLocalDate().plusWeeks(1));
    assertEquals(1, events.size());
    events = cal.getEventsOnDate(sampleStartTime.toLocalDate().plusWeeks(2));
    assertEquals(0, events.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventsNull() {
    cal.createEvents(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventsEmpty() {
    cal.createEvents(new ArrayList<>());
  }

  @Test
  public void testEditEventLocationSuccessfully() {
    cal.createEvent(sampleEvent);
    EventIdentifier id = new EventIdentifier(sampleEvent.getSubject(),
            sampleStartTime, sampleEndTime);
    PropertyChange change = new PropertyChange(PropertyType.LOCATION, "PHYSICAL");

    cal.editEvent(id, change, false);
    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(EventLocation.PHYSICAL, events.get(0).getLocation());
  }


  @Test
  public void testEditEventStatusSuccessfully() {
    cal.createEvent(sampleEvent);
    EventIdentifier id = new EventIdentifier(sampleEvent.getSubject(),
            sampleStartTime, sampleEndTime);
    PropertyChange change = new PropertyChange(PropertyType.STATUS, "PRIVATE");

    cal.editEvent(id, change, false);
    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(EventStatus.PRIVATE, events.get(0).getStatus());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testEditEventInvalidLocation() {
    cal.createEvent(sampleEvent);
    EventIdentifier id = new EventIdentifier(sampleEvent.getSubject(),
            sampleStartTime, sampleEndTime);
    PropertyChange change = new PropertyChange(PropertyType.LOCATION, "REMOTE");

    cal.editEvent(id, change, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventInvalidStatus() {
    cal.createEvent(sampleEvent);
    EventIdentifier id = new EventIdentifier(sampleEvent.getSubject(), sampleStartTime,
            sampleEndTime);
    PropertyChange change = new PropertyChange(PropertyType.STATUS, "PENDING");

    cal.editEvent(id, change, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditNonexistentEvent() {
    EventIdentifier id = new EventIdentifier("Nonexistent", sampleStartTime, sampleEndTime);
    PropertyChange change = new PropertyChange(PropertyType.SUBJECT, "Updated Subject");
    cal.editEvent(id, change, false);
  }

  @Test
  public void testGetEventsOnDate() {
    cal.createEvent(sampleEvent);
    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(1, events.size());
  }

  @Test
  public void testMultipleEventsOnDate() {
    CalendarEvent secondEvent = new CalendarEvent.EventBuilder()
            .setSubject("hello")
            .setStartTime(sampleEvent.getStartTime().plusHours(3))
            .setEndTime(sampleEvent.getEndTime().plusHours(4))
            .setLocation("ONLINE")
            .setStatus("PRIVATE")
            .build();

    CalendarEvent thirdEvent = new CalendarEvent.EventBuilder()
            .setSubject("home")
            .setStartTime(sampleEvent.getStartTime().plusDays(2))
            .setEndTime(sampleEvent.getEndTime().plusDays(2))
            .build();
    cal.createEvent(sampleEvent);
    cal.createEvent(secondEvent);
    cal.createEvent(thirdEvent);
    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(2, events.size());

    CalendarEvent fourthEvent = new CalendarEvent.EventBuilder()
            .setSubject("working")
            .setStartTime(sampleEvent.getEndTime().plusHours(2))
            .setEndTime(sampleEvent.getEndTime().plusHours(3))
            .build();
    cal.createEvent(fourthEvent);
    events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(3, events.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetEventsOnNullDate() {
    cal.getEventsOnDate(null);
  }

  @Test
  public void testGetEventsBetweenDates() {
    CalendarEvent secondEvent = new CalendarEvent.EventBuilder()
            .setSubject("hello")
            .setStartTime(sampleEvent.getStartTime().plusDays(1))
            .setEndTime(sampleEvent.getEndTime().plusDays(1))
            .setLocation("PHYSICAL")
            .setStatus("PRIVATE")
            .build();
    CalendarEvent thirdEvent = new CalendarEvent.EventBuilder()
            .setSubject("home")
            .setStartTime(sampleEvent.getStartTime().plusDays(2))
            .setEndTime(sampleEvent.getEndTime().plusDays(2))
            .build();
    cal.createEvent(sampleEvent);
    cal.createEvent(secondEvent);
    cal.createEvent(thirdEvent);
    LocalDateTime startDate = sampleStartTime.minusDays(2);
    LocalDateTime endDate = sampleStartTime.plusDays(3);

    List<Event> events = cal.getEventsBetween(startDate, endDate);
    assertEquals(3, events.size());
  }

  @Test
  public void testGetStatusMessageBusy() {
    LocalDateTime now = LocalDateTime.now();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(now)
            .setEndTime(now.plusHours(1))
            .setLocation("ONLINE")
            .setStatus("PRIVATE")
            .build();

    cal.createEvent(event);

    assertEquals("Busy.", cal.getStatusMessage(now));
  }


  @Test
  public void testGetStatusMessageAvailable() {
    assertEquals("Available.", cal.getStatusMessage(LocalDateTime.now()));
  }


  @Test
  public void testEditEventsSuccessfully() {
    CalendarEvent event1 = new CalendarEvent.EventBuilder()
            .setSubject("Meeting 1")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .setSeriesId(1L)
            .build();
    cal.createEvent(event1);
    List<Event> updatedEvents = new ArrayList<>();
    updatedEvents.add(event1);
    PropertyChange change = new PropertyChange(PropertyType.SUBJECT, "Updated Subject");

    cal.editEvents(updatedEvents, change);
    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals("Updated Subject", events.get(0).getSubject());
    assertEquals(EventLocation.ONLINE, events.get(0).getLocation());
    assertEquals(EventStatus.PUBLIC, events.get(0).getStatus());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testEditEventsNull() {
    PropertyChange change = new PropertyChange(PropertyType.SUBJECT, "lalala");
    cal.editEvents(null, change);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventsEmpty() {
    PropertyChange change = new PropertyChange(PropertyType.SUBJECT, "lalala");
    cal.editEvents(new ArrayList<>(), change);
  }

  @Test
  public void testCaseInsensitiveDuplicates() {
    cal.createEvent(sampleEvent);

    CalendarEvent duplicateEvent = new CalendarEvent.EventBuilder()
            .setSubject("test event")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();

    cal.createEvent(duplicateEvent);
    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(2, events.size());
  }

  @Test
  public void testEmptySubject() {
    try {
      CalendarEvent emptySubjectEvent = new CalendarEvent.EventBuilder()
              .setSubject("")
              .setStartTime(sampleStartTime)
              .setEndTime(sampleEndTime)
              .setLocation("ONLINE")
              .setStatus("PUBLIC")
              .build();
      cal.createEvent(emptySubjectEvent);
      List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
      assertEquals(0, events.size());
    }
    catch (IllegalArgumentException e) {
      List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
      assertEquals(0, events.size());
    }
  }

  @Test
  public void testPastEventCreation() {
    LocalDateTime pastStart = LocalDateTime.now().minusDays(1);
    LocalDateTime pastEnd = pastStart.plusHours(1);

    CalendarEvent pastEvent = new CalendarEvent.EventBuilder()
            .setSubject("Past Model.Event")
            .setStartTime(pastStart)
            .setEndTime(pastEnd)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();

    cal.createEvent(pastEvent);
    assertEquals(1, cal.getEventsOnDate(pastStart.toLocalDate()).size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRecurringEventsMismatchedTimes() {
    List<Event> events = new ArrayList<>();
    events.add(sampleEvent);

    CalendarEvent differentTimeEvent = new CalendarEvent.EventBuilder()
            .setSubject("Test Model.Event 2")
            .setStartTime(sampleStartTime.plusHours(2))
            .setEndTime(sampleEndTime.plusHours(2))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();
    events.add(differentTimeEvent);

    cal.createEvents(events);
  }

  @Test
  public void testRecurringEventsNullFields() {
    List<Event> events = new ArrayList<>();
    events.add(sampleEvent);

    CalendarEvent nullLocationEvent = new CalendarEvent.EventBuilder()
            .setSubject("Test Model.Event 2")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setStatus("PUBLIC")
            .build();
    events.add(nullLocationEvent);

    cal.createEvents(events);
    assertEquals(2, cal.getEventsOnDate(sampleStartTime.toLocalDate()).size());
  }

  @Test
  public void testRecurringEventOverlap() {
    List<Event> series1 = new ArrayList<>();
    series1.add(sampleEvent);
    cal.createEvents(series1);

    List<Event> series2 = new ArrayList<>();
    CalendarEvent overlappingEvent = new CalendarEvent.EventBuilder()
            .setSubject("Overlapping Model.Event")
            .setStartTime(sampleStartTime.minusMinutes(30))
            .setEndTime(sampleEndTime.plusMinutes(30))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();
    series2.add(overlappingEvent);

    cal.createEvents(series2);
    assertEquals(1, series1.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventStartTimeConflict() {
    cal.createEvent(sampleEvent);

    CalendarEvent event2 = new CalendarEvent.EventBuilder()
            .setSubject("Test Model.Event 2")
            .setStartTime(sampleStartTime.plusHours(3))
            .setEndTime(sampleEndTime.plusHours(3))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();
    cal.createEvent(event2);

    EventIdentifier id = new EventIdentifier(event2.getSubject(),
            event2.getStartTime(), event2.getEndTime());
    PropertyChange change = new PropertyChange(PropertyType.START, sampleStartTime);

    cal.editEvent(id, change, false);
  }


  @Test
  public void testCrossDayEventQuery() {
    LocalDateTime startTime = LocalDateTime.of(2025, 6, 2, 23, 0);
    LocalDateTime endTime = LocalDateTime.of(2025, 6, 3, 1, 0);

    CalendarEvent crossDayEvent = new CalendarEvent.EventBuilder()
            .setSubject("Late Night Model.Event")
            .setStartTime(startTime)
            .setEndTime(endTime)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();

    cal.createEvent(crossDayEvent);

    assertEquals(1, cal.getEventsOnDate(startTime.toLocalDate()).size());
    assertEquals(1, cal.getEventsOnDate(endTime.toLocalDate()).size());
  }

  @Test
  public void testEditEventSubjectChange() {
    cal.createEvent(sampleEvent);
    EventIdentifier id = new EventIdentifier(sampleEvent.getSubject(), sampleStartTime,
            sampleEndTime);
    PropertyChange change = new PropertyChange(PropertyType.SUBJECT, "New Subject");
    cal.editEvent(id, change, false);
    assertEquals(0, cal.getEventsOnDate(LocalDate.now()).size());
  }


  @Test
  public void testCreateEventSpanningMultipleDays() {
    CalendarEvent multiDayEvent = new CalendarEvent.EventBuilder()
            .setSubject("Multi-day")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleStartTime.plusDays(2))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();
    cal.createEvent(multiDayEvent);
    assertEquals(1, cal.getEventsOnDate(sampleStartTime.toLocalDate()).size());
  }


  @Test
  public void testEditEventStartTimeOnly() {
    cal.createEvent(sampleEvent);
    LocalDateTime newStartTime = sampleStartTime.plusHours(1);
    String formattedTime = newStartTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
    PropertyChange change = new PropertyChange(PropertyType.START, formattedTime);

    cal.editEvent(new EventIdentifier(sampleEvent.getSubject(),
            sampleEvent.getStartTime(), sampleEvent.getEndTime()), change, false);

    List<Event> events = cal.getEventsOnDate(newStartTime.toLocalDate());
    assertEquals(1, events.size());
    assertEquals(newStartTime, events.get(0).getStartTime());
    assertEquals(sampleEndTime, events.get(0).getEndTime());
    assertEquals(sampleEvent.getSubject(), events.get(0).getSubject());
    assertEquals(sampleEvent.getLocation(), events.get(0).getLocation());
    assertEquals(sampleEvent.getStatus(), events.get(0).getStatus());
  }


  @Test
  public void testEditEventLocation() {
    cal.createEvent(sampleEvent);
    PropertyChange change = new PropertyChange(PropertyType.LOCATION, "PHYSICAL");

    cal.editEvent(new EventIdentifier(sampleEvent.getSubject(),
            sampleEvent.getStartTime(), sampleEvent.getEndTime()), change, false);

    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(1, events.size());
    assertEquals(EventLocation.PHYSICAL, events.get(0).getLocation());
    assertEquals(sampleEvent.getStartTime(), events.get(0).getStartTime());
    assertEquals(sampleEvent.getEndTime(), events.get(0).getEndTime());
    assertEquals(sampleEvent.getSubject(), events.get(0).getSubject());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testEditEventWithInvalidLocation() {
    cal.createEvent(sampleEvent);
    PropertyChange change = new PropertyChange(PropertyType.LOCATION, "INVALID");
    cal.editEvent(new EventIdentifier(sampleEvent.getSubject(),
            sampleEvent.getStartTime(), sampleEvent.getEndTime()), change, false);
  }

  @Test
  public void testEditEventDescription() {
    cal.createEvent(sampleEvent);
    String newDesc = "Updated description";
    PropertyChange change = new PropertyChange(PropertyType.DESCRIPTION, newDesc);

    cal.editEvent(new EventIdentifier(sampleEvent.getSubject(),
            sampleEvent.getStartTime(), sampleEvent.getEndTime()), change, false);

    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(1, events.size());
    assertEquals(newDesc, events.get(0).getDesc());
    assertEquals(sampleEvent.getStartTime(), events.get(0).getStartTime());
    assertEquals(sampleEvent.getEndTime(), events.get(0).getEndTime());
    assertEquals(sampleEvent.getSubject(), events.get(0).getSubject());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testEditEventToCreateConflict() {
    cal.createEvent(sampleEvent);

    CalendarEvent secondEvent = new CalendarEvent.EventBuilder()
            .setSubject("Second Event")
            .setStartTime(sampleStartTime.plusHours(2))
            .setEndTime(sampleEndTime.plusHours(2))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();
    cal.createEvent(secondEvent);

    PropertyChange change = new PropertyChange(PropertyType.START, sampleStartTime);
    cal.editEvent(new EventIdentifier(secondEvent.getSubject(),
            secondEvent.getStartTime(), secondEvent.getEndTime()), change, false);
  }


  @Test
  public void testEditEventsMultiple() {
    CalendarEvent event1 = new CalendarEvent.EventBuilder()
            .setSubject("Meeting 1")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .setSeriesId(1L)
            .build();

    CalendarEvent event2 = new CalendarEvent.EventBuilder()
            .setSubject("Meeting 1")
            .setStartTime(sampleStartTime.plusHours(2))
            .setEndTime(sampleEndTime.plusHours(2))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .setSeriesId(1L)
            .build();

    cal.createEvent(event1);
    cal.createEvent(event2);

    List<Event> eventsToEdit = new ArrayList<>();
    eventsToEdit.add(event1);

    TypingChange change = new PropertyChange(PropertyType.LOCATION, "PHYSICAL");
    cal.editEvents(eventsToEdit, change);

    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(2, events.size());
    assertEquals(EventLocation.PHYSICAL, events.get(0).getLocation());
  }

  @Test
  public void testEditEventsSame() {
    CalendarEvent event1 = new CalendarEvent.EventBuilder()
            .setSubject("Meeting 1")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .setSeriesId(1L)
            .build();

    cal.createEvent(event1);

    List<Event> eventsToEdit = new ArrayList<>();
    eventsToEdit.add(event1);

    TypingChange change = new PropertyChange(PropertyType.LOCATION, "PHYSICAL");
    cal.editEvents(eventsToEdit, change);

    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(1, events.size());
    assertEquals(EventLocation.PHYSICAL, events.get(0).getLocation());
  }


  @Test
  public void testEditEventsSeriesDescription() {
    List<Event> seriesEvents = new ArrayList<>();

    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Series Event")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .setSeriesId(1L)
            .build();

    cal.createEvent(event);

    seriesEvents.add(event);
    TypingChange change = new PropertyChange(PropertyType.DESCRIPTION,
            "Updated series description");
    cal.editEvents(seriesEvents, change);

    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(1, events.size());
    assertEquals("Updated series description", events.get(0).getDesc());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventsWithEmptyList() {
    cal.editEvents(new ArrayList<>(), new PropertyChange(PropertyType.SUBJECT,
            "New Subject"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventsWithNullList() {
    cal.editEvents(null, new PropertyChange(PropertyType.SUBJECT, "New Subject"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventsWithNullChange() {
    List<Event> events = new ArrayList<>();
    CalendarEvent event1 = new CalendarEvent.EventBuilder()
            .setSubject("Meeting 1")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .setSeriesId(1L)
            .build();
    events.add(event1);
    cal.editEvents(events, null);
  }

  @Test
  public void testEditEventsPreservesOtherProperties() {
    CalendarEvent event1 = new CalendarEvent.EventBuilder()
            .setSubject("Meeting 1")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .setSeriesId(1L)
            .build();
    cal.createEvent(event1);
    List<Event> eventsToEdit = new ArrayList<>();
    eventsToEdit.add(event1);

    PropertyChange change = new PropertyChange(PropertyType.SUBJECT, "Updated Subject");
    cal.editEvents(eventsToEdit, change);

    List<Event> events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    assertEquals(1, events.size());
    assertEquals("Updated Subject", events.get(0).getSubject());
    assertEquals(sampleEvent.getStartTime(), events.get(0).getStartTime());
    assertEquals(sampleEvent.getEndTime(), events.get(0).getEndTime());
    assertEquals(sampleEvent.getLocation(), events.get(0).getLocation());
    assertEquals(sampleEvent.getStatus(), events.get(0).getStatus());
  }

  @Test
  public void testEditEventsAcrossMultipleDays() {
    Long seriesId = 1L;
    List<Event> seriesEvents = new ArrayList<>();

    CalendarEvent event1 = new CalendarEvent.EventBuilder()
            .setSubject("Day 1 Event")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setSeriesId(seriesId)
            .build();

    CalendarEvent event2 = new CalendarEvent.EventBuilder()
            .setSubject("Day 1 Event")
            .setStartTime(sampleStartTime.plusDays(1))
            .setEndTime(sampleEndTime.plusDays(1))
            .setLocation("ONLINE")
            .setSeriesId(seriesId)
            .build();

    seriesEvents.add(event1);
    seriesEvents.add(event2);

    cal.createEvents(seriesEvents);

    TypingChange change = new PropertyChange(PropertyType.STATUS, "PRIVATE");
    cal.editEvents(seriesEvents, change);

    List<Event> day1Events = cal.getEventsOnDate(sampleStartTime.toLocalDate());
    List<Event> day2Events = cal.getEventsOnDate(sampleStartTime.plusDays(1).toLocalDate());

    assertEquals(1, day1Events.size());
    assertEquals(1, day2Events.size());
    assertEquals(EventStatus.PRIVATE, day1Events.get(0).getStatus());
    assertEquals(EventStatus.PRIVATE, day2Events.get(0).getStatus());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditEventsWithNonexistentEvent() {
    CalendarEvent nonexistentEvent = new CalendarEvent.EventBuilder()
            .setSubject("Nonexistent")
            .setStartTime(sampleStartTime.plusDays(10))
            .setEndTime(sampleEndTime.plusDays(10))
            .setSeriesId(1L)
            .build();

    List<Event> eventsToEdit = new ArrayList<>();
    eventsToEdit.add(nonexistentEvent);

    PropertyChange change = new PropertyChange(PropertyType.SUBJECT, "New Subject");
    cal.editEvents(eventsToEdit, change);
  }

  @Test
  public void testCreateEventAtDaylightSavingsBoundary() {
    LocalDateTime dstStart = LocalDateTime.of(2025, 3,
            10, 2, 0);
    LocalDateTime dstEnd = LocalDateTime.of(2025, 11, 3, 2, 0);

    CalendarEvent springEvent = new CalendarEvent.EventBuilder()
            .setSubject("DST Spring Event")
            .setStartTime(dstStart)
            .setEndTime(dstStart.plusHours(1))
            .build();

    CalendarEvent fallEvent = new CalendarEvent.EventBuilder()
            .setSubject("DST Fall Event")
            .setStartTime(dstEnd)
            .setEndTime(dstEnd.plusHours(1))
            .build();

    cal.createEvent(springEvent);
    cal.createEvent(fallEvent);

    List<Event> springEvents = cal.getEventsOnDate(dstStart.toLocalDate());
    List<Event> fallEvents = cal.getEventsOnDate(dstEnd.toLocalDate());

    assertEquals(1, springEvents.size());
    assertEquals(1, fallEvents.size());
  }

  @Test
  public void testEventSpanningMultipleYears() {
    LocalDateTime startOfYear = LocalDateTime.of(2025,
            12, 31, 23, 0);
    LocalDateTime endOfNextYear = LocalDateTime.of(2026,
            1, 1, 1, 0);

    CalendarEvent yearSpanningEvent = new CalendarEvent.EventBuilder()
            .setSubject("New Year Event")
            .setStartTime(startOfYear)
            .setEndTime(endOfNextYear)
            .build();

    cal.createEvent(yearSpanningEvent);

    List<Event> events2025 = cal.getEventsOnDate(startOfYear.toLocalDate());
    List<Event> events2026 = cal.getEventsOnDate(endOfNextYear.toLocalDate());

    assertEquals(1, events2025.size());
    assertEquals(1, events2026.size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithEndTimeBeforeStartTime() {
    CalendarEvent invalidEvent = new CalendarEvent.EventBuilder()
            .setSubject("Invalid Time Event")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleStartTime.minusMinutes(1))
            .build();

    cal.createEvent(invalidEvent);
  }

  @Test
  public void testEventExactlyAt24Hours() {
    LocalDateTime midnight = LocalDateTime.of(2025,
            6, 3, 0, 0);
    CalendarEvent midnightEvent = new CalendarEvent.EventBuilder()
            .setSubject("Midnight Event")
            .setStartTime(midnight)
            .setEndTime(midnight.plusDays(1))
            .build();

    cal.createEvent(midnightEvent);

    List<Event> events = cal.getEventsOnDate(midnight.toLocalDate());
    assertEquals(1, events.size());
  }

  @Test
  public void testCreateEventWithStartFrom() {
    CalendarEvent eventTimes = new CalendarEvent.EventBuilder()
            .setSubject("Test Model.CalendarEvent")
            .setStartTime(LocalDateTime.of(2025, 6, 2, 10, 0))
            .setEndTime(LocalDateTime.of(2025, 6, 2, 11, 0))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();
    cal.createEvent(eventTimes);
    List<Event> events = cal.getEventsOnDate(LocalDate.of(2025, 6, 2));
    assertEquals(1, events.size());
    assertEquals(10, events.get(0).getStartTime().toLocalTime().getHour());
    assertEquals(11, events.get(0).getEndTime().toLocalTime().getHour());
  }

  @Test
  public void testGetEventsBetween() {
    CalendarEvent event1 = new CalendarEvent.EventBuilder()
            .setSubject("Day 1 Event")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .build();

    CalendarEvent event2 = new CalendarEvent.EventBuilder()
            .setSubject("Day 2 Event")
            .setStartTime(sampleStartTime.plusDays(1))
            .setEndTime(sampleEndTime.plusDays(1))
            .setLocation("ONLINE")
            .build();

    CalendarEvent event3 = new CalendarEvent.EventBuilder()
            .setSubject("Day 2 Event")
            .setStartTime(sampleStartTime.plusDays(3))
            .setEndTime(sampleEndTime.plusDays(3))
            .setLocation("ONLINE")
            .build();


    cal.createEvent(event1);
    cal.createEvent(event2);
    cal.createEvent(event3);

    assertEquals(1,
            cal.getEventsBetween(sampleStartTime.plusHours(1), sampleEndTime.plusDays(2)).size());

  }


  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithNullStartTime() {
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(null)
            .setEndTime(LocalDateTime.of(2025, 6, 2, 17, 0))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();

    cal.createEvent(event);
    List<Event> events = cal.getEventsOnDate(LocalDate.of(2025, 6, 2));
    assertEquals(1, events.size());
    assertEquals(LocalDateTime.of(2025, 6, 2, 8, 0),
            events.get(0).getStartTime());
    assertEquals(LocalDateTime.of(2025, 6, 2, 17, 0),
            events.get(0).getEndTime());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateEventWithBothNullTimes() {
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(null)
            .setEndTime(null)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();

    cal.createEvent(event);
  }


  @Test
  public void testTimezoneConversion() {
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(LocalDateTime.of(2025, 6, 2, 10, 0))
            .setEndTime(LocalDateTime.of(2025, 6, 2, 11, 0))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .setTimezone(ZoneId.of("America/New_York"))
            .build();

    cal.setTimezone("America/Los_Angeles");
    cal.createEvent(event);

    List<Event> events = cal.getEventsOnDate(LocalDate.of(2025, 6, 2));
    assertEquals(1, events.size());
    assertEquals(LocalDateTime.of(2025, 6, 2, 7, 0),
            events.get(0).getStartTime());
    assertEquals(LocalDateTime.of(2025, 6, 2, 8, 0),
            events.get(0).getEndTime());
  }


  @Test
  public void testCreateEventSpanningDates() {
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(LocalDateTime.of(2025, 6, 2, 23, 0))
            .setEndTime(LocalDateTime.of(2025, 6, 3, 1, 0))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();

    cal.createEvent(event);

    List<Event> eventsDay1 = cal.getEventsOnDate(LocalDate.of(2025, 6, 2));
    List<Event> eventsDay2 = cal.getEventsOnDate(LocalDate.of(2025, 6, 3));

    assertEquals(1, eventsDay1.size());
    assertEquals(1, eventsDay2.size());
    assertEquals(event.getSubject(), eventsDay1.get(0).getSubject());
    assertEquals(event.getSubject(), eventsDay2.get(0).getSubject());
  }

  @Test
  public void testGetEventsBetweenWithTimezones() {
    cal.setTimezone("America/New_York");

    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(LocalDateTime.of(2025, 6, 2, 22, 0))
            .setEndTime(LocalDateTime.of(2025, 6, 2, 23, 0))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();

    cal.createEvent(event);

    cal.setTimezone("America/Los_Angeles");
    List<Event> events = cal.getEventsBetween(
            LocalDateTime.of(2025, 6, 2, 19, 0),
            LocalDateTime.of(2025, 6, 2, 20, 0));

    assertEquals(1, events.size());
    assertEquals(event.getSubject(), events.get(0).getSubject());
  }

  @Test
  public void testBasicTimezoneConversion() {
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Meeting")
            .setStartTime(LocalDateTime.of(2025, 6, 2, 14, 0))
            .setEndTime(LocalDateTime.of(2025, 6, 2, 15, 0))
            .setTimezone(ZoneId.of("America/New_York"))
            .build();

    CalendarEvent convertedEvent = (CalendarEvent) event.newTimezone(ZoneId.of(
            "America/Los_Angeles"));

    assertEquals(LocalDateTime.of(2025, 6, 2, 11, 0),
            convertedEvent.getStartTime());
    assertEquals(LocalDateTime.of(2025, 6, 2, 12, 0),
            convertedEvent.getEndTime());
  }

  @Test
  public void testTimezoneConversionAcrossDateline() {
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("International Call")
            .setStartTime(LocalDateTime.of(2025, 6, 2, 10, 0))
            .setEndTime(LocalDateTime.of(2025, 6, 2, 11, 0))
            .setTimezone(ZoneId.of("Asia/Tokyo"))
            .build();

    CalendarEvent convertedEvent = (CalendarEvent) event.newTimezone(ZoneId.of(
            "America/Los_Angeles"));

    assertEquals(LocalDateTime.of(2025, 6, 1, 18, 0),
            convertedEvent.getStartTime());
    assertEquals(LocalDateTime.of(2025, 6, 1, 19, 0),
            convertedEvent.getEndTime());
  }

  @Test
  public void testTimezoneConversionDuringDST() {
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Summer Meeting")
            .setStartTime(LocalDateTime.of(2025, 7, 1, 14, 0))
            .setEndTime(LocalDateTime.of(2025, 7, 1, 15, 0))
            .setTimezone(ZoneId.of("Europe/London"))
            .build();

    CalendarEvent convertedEvent = (CalendarEvent) event.newTimezone(ZoneId.of(
            "America/New_York"));

    assertEquals(LocalDateTime.of(2025, 7, 1, 9, 0),
            convertedEvent.getStartTime());
    assertEquals(LocalDateTime.of(2025, 7, 1, 10, 0),
            convertedEvent.getEndTime());
  }

  @Test
  public void testTimezoneConversionPreservesEventProperties() {
    CalendarEvent original = new CalendarEvent.EventBuilder()
            .setSubject("Full Event")
            .setStartTime(LocalDateTime.of(2025, 6, 2, 14, 0))
            .setEndTime(LocalDateTime.of(2025, 6, 2, 15, 0))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .setDesc("Test Description")
            .setSeriesId(123L)
            .setTimezone(ZoneId.of("Europe/London"))
            .build();

    CalendarEvent converted = (CalendarEvent) original.newTimezone(ZoneId.of("Asia/Tokyo"));

    assertEquals(original.getSubject(), converted.getSubject());
    assertEquals(original.getLocation(), converted.getLocation());
    assertEquals(original.getStatus(), converted.getStatus());
    assertEquals(original.getDesc(), converted.getDesc());
    assertEquals(original.getSeriesId(), converted.getSeriesId());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTimezoneConversionWithNullNewTimezone() {
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test")
            .setStartTime(LocalDateTime.now())
            .setEndTime(LocalDateTime.now().plusHours(1))
            .setTimezone(ZoneId.of("UTC"))
            .build();

    event.newTimezone(null);
  }

  @Test
  public void testTimezoneConversionWithNullOriginalTimezone() {
    LocalDateTime start = LocalDateTime.of(2025, 6, 2, 14, 0);
    LocalDateTime end = LocalDateTime.of(2025, 6, 2, 15, 0);

    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test")
            .setStartTime(start)
            .setEndTime(end)
            .build();

    CalendarEvent converted = (CalendarEvent) event.newTimezone(ZoneId.of("UTC"));

    assertNotNull(converted.getStartTime());
    assertNotNull(converted.getEndTime());
  }



  @Test
  public void testEditSeriesBasicProperties() {
    List<Event> seriesEvents = new ArrayList<>();
    LocalDateTime baseTime = LocalDateTime.of(2025, 6, 1, 10, 0);
    Long seriesId = 1L;

    for (int i = 0; i < 3; i++) {
      CalendarEvent event = new CalendarEvent.EventBuilder()
              .setSubject("Weekly Meeting")
              .setStartTime(baseTime.plusDays(i * 7))
              .setEndTime(baseTime.plusDays(i * 7).plusHours(1))
              .setLocation("ONLINE")
              .setStatus("PUBLIC")
              .setSeriesId(seriesId)
              .build();
      seriesEvents.add(event);
    }

    cal.createEvents(seriesEvents);

    PropertyChange subjectChange = new PropertyChange(PropertyType.SUBJECT,
            "Updated Meeting");
    cal.editSeries(seriesId, subjectChange);

    PropertyChange descChange = new PropertyChange(PropertyType.DESCRIPTION,
            "Team sync-up");
    for (Event event : seriesEvents) {
      EventIdentifier id = new EventIdentifier("Updated Meeting",
              event.getStartTime(), event.getEndTime(), event.getSeriesId());
      cal.editEvent(id, descChange, false);
    }

    PropertyChange locationChange = new PropertyChange(PropertyType.LOCATION,
            "PHYSICAL");
    for (Event event : seriesEvents) {
      EventIdentifier id = new EventIdentifier("Updated Meeting",
              event.getStartTime(), event.getEndTime(), event.getSeriesId());
      cal.editEvent(id, locationChange, false);
    }

    PropertyChange statusChange = new PropertyChange(PropertyType.STATUS,
            "PRIVATE");
    for (Event event : seriesEvents) {
      EventIdentifier id = new EventIdentifier("Updated Meeting",
              event.getStartTime(), event.getEndTime(), event.getSeriesId());
      cal.editEvent(id, statusChange, false);
    }

    for (int i = 0; i < 3; i++) {
      LocalDate date = baseTime.plusDays(i * 7).toLocalDate();
      List<Event> events = cal.getEventsOnDate(date);
      assertEquals(1, events.size());
      Event event = events.get(0);
      assertEquals("Updated Meeting", event.getSubject());
      assertEquals("Team sync-up", event.getDesc());
      assertEquals(EventLocation.PHYSICAL, event.getLocation());
      assertEquals(EventStatus.PRIVATE, event.getStatus());
      assertEquals(seriesId, event.getSeriesId());
    }
  }


  @Test(expected = IllegalArgumentException.class)
  public void testEditSeriesNullSeriesId() {
    PropertyChange change = new PropertyChange(PropertyType.SUBJECT, "Test");
    cal.editSeries(null, change);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testEditSeriesNonexistentSeriesId() {
    PropertyChange change = new PropertyChange(PropertyType.SUBJECT, "Test");
    cal.editSeries((long) 999, change);
  }


  @Test(expected = IllegalArgumentException.class)
  public void testEditSeriesInvalidPropertyValue() {
    List<Event> seriesEvents = new ArrayList<>();
    LocalDateTime baseTime = LocalDateTime.of(2025, 6, 1,
            10, 0);


    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(baseTime)
            .setEndTime(baseTime.plusHours(1))
            .build();
    seriesEvents.add(event);


    cal.createEvents(seriesEvents);
    Long seriesId = seriesEvents.get(0).getSeriesId();


    PropertyChange change = new PropertyChange(PropertyType.STATUS, "INVALID_STATUS");
    cal.editSeries(seriesId, change);
  }


  @Test
  public void testEditSeriesPreservesOtherProperties() {
    List<Event> seriesEvents = new ArrayList<>();
    LocalDateTime baseTime = LocalDateTime.of(2025, 6, 1, 10, 0);

    Long seriesId = 1L;
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Original Meeting")
            .setStartTime(baseTime)
            .setEndTime(baseTime.plusHours(1))
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .setDesc("Original description")
            .setSeriesId(seriesId)
            .build();
    seriesEvents.add(event);

    cal.createEvents(seriesEvents);

    PropertyChange subjectChange = new PropertyChange(PropertyType.SUBJECT, "New Meeting");
    cal.editSeries(seriesId, subjectChange);

    List<Event> events = cal.getEventsOnDate(baseTime.toLocalDate());
    Event updatedEvent = events.get(0);

    assertEquals("New Meeting", updatedEvent.getSubject());
    assertEquals(baseTime, updatedEvent.getStartTime());
    assertEquals(baseTime.plusHours(1), updatedEvent.getEndTime());
    assertEquals(EventLocation.ONLINE, updatedEvent.getLocation());
    assertEquals(EventStatus.PUBLIC, updatedEvent.getStatus());
    assertEquals("Original description", updatedEvent.getDesc());
    assertEquals(seriesId, updatedEvent.getSeriesId());
  }

  @Test
  public void testCreateCalendarSuccessfully() {
    model.createCalendar(TEST_CALENDAR, TEST_TIMEZONE);
    assertEquals(TEST_CALENDAR, model.getCurrentCalendarName());
    assertEquals(ZoneId.of(TEST_TIMEZONE), model.getCurrentCalendarTimezone());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarWithNullName() {
    model.createCalendar(null, TEST_TIMEZONE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarWithEmptyName() {
    model.createCalendar("  ", TEST_TIMEZONE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCalendarWithInvalidTimezone() {
    model.createCalendar(TEST_CALENDAR, "Invalid/Timezone");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateDuplicateCalendar() {
    model.createCalendar(TEST_CALENDAR, TEST_TIMEZONE);
    model.createCalendar(TEST_CALENDAR, ALTERNATIVE_TIMEZONE);
  }

  @Test
  public void testUseCalendarSuccessfully() {
    model.createCalendar(TEST_CALENDAR, TEST_TIMEZONE);
    CalendarModel calendar = model.useCalendar(TEST_CALENDAR);
    assertNotNull(calendar);
    assertEquals(TEST_CALENDAR, model.getCurrentCalendarName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUseNonexistentCalendar() {
    model.useCalendar("NonexistentCalendar");
  }

  @Test
  public void testEditCalendarNameSuccessfully() {
    model.createCalendar(TEST_CALENDAR, TEST_TIMEZONE);
    String newName = "NewCalendarName";
    model.editCalendar(TEST_CALENDAR, PropertyType.CALENDARNAME, newName);
    assertEquals(newName, model.getCurrentCalendarName());
  }

  @Test
  public void testEditCalendarTimezoneSuccessfully() {
    model.createCalendar(TEST_CALENDAR, TEST_TIMEZONE);
    model.editCalendar(TEST_CALENDAR, PropertyType.TIMEZONE, ALTERNATIVE_TIMEZONE);
    assertEquals(ZoneId.of(ALTERNATIVE_TIMEZONE), model.getCurrentCalendarTimezone());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditNonexistentCalendar() {
    model.editCalendar("NonexistentCalendar", PropertyType.TIMEZONE, ALTERNATIVE_TIMEZONE);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarWithInvalidProperty() {
    model.createCalendar(TEST_CALENDAR, TEST_TIMEZONE);
    // PropertyType shouldn't be null, we should validate this first
    model.editCalendar(TEST_CALENDAR, PropertyType.TIMEZONE, null);
  }

  @Test
  public void testCopyEventSuccessfully() {
    // Setup source and target calendars
    model.createCalendar("Source", TEST_TIMEZONE);
    model.createCalendar("Target", ALTERNATIVE_TIMEZONE);

    // Create and add test event to source calendar with explicit timezone
    LocalDateTime eventTime = LocalDateTime.now();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(eventTime)
            .setEndTime(eventTime.plusHours(6))
            .setTimezone(ZoneId.of(TEST_TIMEZONE))
            .build();

    model.useCalendar("Source").createEvent(event);
    List<Event> sourceEvents = model.useCalendar("Source")
            .getEventsOnDate(eventTime.toLocalDate());
    assertEquals(1, sourceEvents.size());

    LocalDateTime targetDateTime = eventTime
            .atZone(ZoneId.of(TEST_TIMEZONE))
            .withZoneSameInstant(ZoneId.of(ALTERNATIVE_TIMEZONE))
            .toLocalDateTime();

    model.copyEvent("Test Event", eventTime, "Target", targetDateTime);

    CalendarModel targetCalendar = model.useCalendar("Target");
    List<Event> targetEvents = targetCalendar.getEventsOnDate(targetDateTime.toLocalDate());

    assertEquals(1, targetEvents.size());

    Event copiedEvent = targetEvents.get(0);
    assertEquals("Test Event", copiedEvent.getSubject());
    assertEquals(targetDateTime, copiedEvent.getStartTime());
    assertEquals(targetDateTime.plusHours(1), copiedEvent.getEndTime());
    assertEquals(ZoneId.of(ALTERNATIVE_TIMEZONE), copiedEvent.getTimezone());
  }


  @Test
  public void testCopyEventsRangeSuccessfully() {
    model.createCalendar("Source", TEST_TIMEZONE);
    model.createCalendar("Target", ALTERNATIVE_TIMEZONE);

    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate.plusDays(2);
    CalendarModel sourceCalendar = model.useCalendar("Source");

    CalendarEvent event1 = new CalendarEvent.EventBuilder()
            .setSubject("Event 1")
            .setStartTime(startDate.atTime(10, 0))
            .setEndTime(startDate.atTime(11, 0))
            .build();
    sourceCalendar.createEvent(event1);

    model.copyEvents(startDate, endDate, "Target", startDate.plusDays(1));

    CalendarModel targetCalendar = model.useCalendar("Target");
    assertFalse(targetCalendar.getEventsOnDate(startDate.plusDays(1)).isEmpty());
  }

  @Test(expected = IllegalStateException.class)
  public void testCopyEventsWithNoCurrentCalendar() {
    model.copyEvents(LocalDate.now(), LocalDate.now().plusDays(1),
            "Target", LocalDate.now());
  }

  @Test
  public void testGetCurrentCalendarNameWithNoCalendar() {
    assertNull(model.getCurrentCalendarName());
  }

  @Test(expected = IllegalStateException.class)
  public void testGetCurrentCalendarTimezoneWithNoCalendar() {
    model.getCurrentCalendarTimezone();
  }

  @Test
  public void testCreateMultipleCalendarsFirstIsDefault() {
    model.createCalendar("Cal1", TEST_TIMEZONE);
    model.createCalendar("Cal2", ALTERNATIVE_TIMEZONE);
    assertEquals("Cal1", model.getCurrentCalendarName());
  }


  @Test
  public void testEditCalendarNamePreservesData() {
    model.createCalendar("OldName", TEST_TIMEZONE);
    LocalDateTime eventTime = LocalDateTime.now();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(eventTime)
            .setEndTime(eventTime.plusHours(1))
            .build();
    model.useCalendar("OldName").createEvent(event);

    model.editCalendar("OldName", PropertyType.CALENDARNAME, "NewName");
    List<Event> events = model.useCalendar("NewName").getEventsOnDate(eventTime.toLocalDate());
    assertEquals(1, events.size());
  }

  @Test(expected = IllegalStateException.class)
  public void testGetTimezoneWithNoCurrentCalendar() {
    MultipleCalendarModel emptyModel = new MultipleCalendarModelImpl();
    emptyModel.getCurrentCalendarTimezone();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsWithStartDateAfterEndDate() {
    model.createCalendar("Source", TEST_TIMEZONE);
    model.createCalendar("Target", ALTERNATIVE_TIMEZONE);
    LocalDate startDate = LocalDate.now().plusDays(1);
    LocalDate endDate = LocalDate.now();
    model.copyEvents(startDate, endDate, "Target", LocalDate.now());
  }

  @Test
  public void testCopyEventsAcrossMultipleTimezones() {
    model.createCalendar("UTC", "UTC");
    model.createCalendar("EST", "America/New_York");
    model.createCalendar("IST", "Asia/Kolkata");

    LocalDateTime utcTime = LocalDateTime.now();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Global Meeting")
            .setStartTime(utcTime)
            .setEndTime(utcTime.plusDays(2))
            .setTimezone(ZoneId.of("UTC"))
            .build();

    model.useCalendar("UTC").createEvent(event);

    LocalDateTime estTime = utcTime
            .atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.of("America/New_York"))
            .toLocalDateTime();
    model.copyEvent("Global Meeting", utcTime, "EST", estTime);

    LocalDateTime istTime = utcTime
            .atZone(ZoneId.of("UTC"))
            .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
            .toLocalDateTime();
    model.copyEvent("Global Meeting", utcTime, "IST", istTime);

    assertEquals(1, model.useCalendar("UTC").getEventsOnDate(utcTime.toLocalDate()).size());
    assertEquals(1, model.useCalendar("EST").getEventsOnDate(estTime.toLocalDate()).size());
    assertEquals(1, model.useCalendar("IST").getEventsOnDate(istTime.toLocalDate()).size());

    Event estEvent = model.useCalendar("EST").getEventsOnDate(estTime.toLocalDate()).get(0);
    assertEquals("Global Meeting", estEvent.getSubject());
    assertEquals(estTime, estEvent.getStartTime());
    assertEquals(estTime.plusHours(4), estEvent.getEndTime());

    Event istEvent = model.useCalendar("IST").getEventsOnDate(istTime.toLocalDate()).get(0);
    assertEquals("Global Meeting", istEvent.getSubject());
    assertEquals(istTime, istEvent.getStartTime());
    assertEquals(estTime.plusDays(1).plusHours(4), istEvent.getEndTime());
  }

  @Test
  public void testEditCalendarTimezoneUpdatesEvents() {
    model.createCalendar("TestCal", TEST_TIMEZONE);
    LocalDateTime eventTime = LocalDateTime.now();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(eventTime)
            .setEndTime(eventTime.plusHours(1))
            .setTimezone(ZoneId.of(TEST_TIMEZONE))
            .build();

    model.useCalendar("TestCal").createEvent(event);
    model.editCalendar("TestCal", PropertyType.TIMEZONE, ALTERNATIVE_TIMEZONE);

    ZoneId newTimezone = model.getCurrentCalendarTimezone();
    assertEquals(ZoneId.of(ALTERNATIVE_TIMEZONE), newTimezone);
  }

  @Test
  public void testCopyEventsPreservesSeriesIdAcrossCalendars() {
    model.createCalendar("Source", TEST_TIMEZONE);
    model.createCalendar("Target", ALTERNATIVE_TIMEZONE);

    LocalDate startDate = LocalDate.now();
    LocalDateTime eventTime = startDate.atTime(10, 0);
    Long seriesId = (long) 123;

    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Series Event")
            .setStartTime(eventTime)
            .setEndTime(eventTime.plusHours(1))
            .setSeriesId(seriesId)
            .build();

    model.useCalendar("Source").createEvent(event);

    model.copyEvent("Series Event", eventTime, "Target", eventTime);

    List<Event> targetEvents = model.useCalendar("Target")
            .getEventsOnDate(eventTime.toLocalDate());
    assertEquals(seriesId, targetEvents.get(0).getSeriesId());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testCopyAllDayEventAcrossTimezones() {
    model.createCalendar("NYCalendar", "America/New_York");
    model.createCalendar("LondonCalendar", "Europe/London");

    LocalDateTime nyDate = LocalDateTime.of(2025, 6, 11, 0, 0);
    CalendarEvent allDayEvent = new CalendarEvent.EventBuilder()
            .setSubject("All Day Meeting")
            .setStartTime(nyDate)
            .setEndTime(null)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .build();

    model.useCalendar("NYCalendar").createEvent(allDayEvent);

    LocalDateTime nyStart = nyDate.withHour(8).withMinute(0);
    LocalDateTime nyEnd = nyDate.withHour(17).withMinute(0);

    LocalDateTime londonStartTime = nyStart
            .atZone(ZoneId.of("America/New_York"))
            .withZoneSameInstant(ZoneId.of("Europe/London"))
            .toLocalDateTime();

    model.copyEvent("All Day Meeting", nyStart, "LondonCalendar", londonStartTime);

    List<Event> londonEvents = model.useCalendar("LondonCalendar")
            .getEventsOnDate(londonStartTime.toLocalDate());

    assertEquals(1, londonEvents.size());
    Event copiedEvent = londonEvents.get(0);
    assertEquals("All Day Meeting", copiedEvent.getSubject());
    assertEquals(londonStartTime, copiedEvent.getStartTime());
    assertEquals(londonStartTime.plusHours(9), copiedEvent.getEndTime());

    assertEquals(13, copiedEvent.getStartTime().getHour());
    assertEquals(22, copiedEvent.getEndTime().getHour());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testCopyNonexistentEvent() {
    model.createCalendar("Source", TEST_TIMEZONE);
    model.createCalendar("Target", ALTERNATIVE_TIMEZONE);

    LocalDateTime eventTime = LocalDateTime.now();
    model.copyEvent("NonexistentEvent", eventTime, "Target", eventTime);
  }

  @Test
  public void testCopyEventsPreservesAllProperties() {
    model.createCalendar("Source", TEST_TIMEZONE);
    model.createCalendar("Target", ALTERNATIVE_TIMEZONE);

    LocalDate startDate = LocalDate.now();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Full Event")
            .setStartTime(startDate.atTime(10, 0))
            .setEndTime(startDate.atTime(11, 0))
            .setLocation("ONLINE")
            .setStatus("PRIVATE")
            .setDesc("Test Description")
            .build();

    model.useCalendar("Source").createEvent(event);

    model.copyEvents(startDate, startDate, "Target", startDate);

    Event copiedEvent = model.useCalendar("Target")
            .getEventsOnDate(startDate).get(0);

    assertEquals(event.getSubject(), copiedEvent.getSubject());
    assertEquals(event.getLocation(), copiedEvent.getLocation());
    assertEquals(event.getStatus(), copiedEvent.getStatus());
    assertEquals(event.getDesc(), copiedEvent.getDesc());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsToNonexistentCalendar() {
    model.createCalendar("Source", TEST_TIMEZONE);
    LocalDate startDate = LocalDate.now();

    model.copyEvents(startDate, startDate, "NonexistentCalendar", startDate);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventWithNoCurrentCalendar() {
    model.createCalendar("Target", TEST_TIMEZONE);
    LocalDateTime eventTime = LocalDateTime.now();

    model.copyEvent("Event", eventTime, "Target", eventTime);
  }

  @Test
  public void testCopyEventsEmptyDateRange() {
    model.createCalendar("Source", TEST_TIMEZONE);
    model.createCalendar("Target", ALTERNATIVE_TIMEZONE);

    LocalDate date = LocalDate.now();
    model.useCalendar("Source");

    model.copyEvents(date, date, "Target", date);

    assertTrue(model.useCalendar("Target").getEventsOnDate(date).isEmpty());
  }

  @Test
  public void testCopyEventsAcrossDateline() {
    model.createCalendar("Tokyo", "Asia/Tokyo");
    model.createCalendar("LA", "America/Los_Angeles");

    LocalDateTime tokyoTime = LocalDateTime.of(2025, 6,
            12, 21, 0); // 9 PM in Tokyo

    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("International Meeting")
            .setStartTime(tokyoTime)
            .setEndTime(tokyoTime.plusHours(2))
            .setTimezone(ZoneId.of("Asia/Tokyo"))
            .build();

    model.useCalendar("Tokyo").createEvent(event);

    ZonedDateTime tokyoZonedTime = tokyoTime.atZone(ZoneId.of("Asia/Tokyo"));
    ZonedDateTime laZonedTime = tokyoZonedTime.withZoneSameInstant(ZoneId.of(
            "America/Los_Angeles"));

    LocalDate tokyoDate = tokyoTime.toLocalDate();
    LocalDate laDate = laZonedTime.toLocalDate();

    model.copyEvents(tokyoDate, tokyoDate, "LA", laDate);

    CalendarModel laCal = model.useCalendar("LA");
    List<Event> laEvents = laCal.getEventsOnDate(laDate);

    assertEquals(1, laEvents.size());
    Event copiedEvent = laEvents.get(0);
    assertEquals("International Meeting", copiedEvent.getSubject());
    assertEquals(ZoneId.of("America/Los_Angeles"), copiedEvent.getTimezone());
  }


  @Test(expected = IllegalArgumentException.class)
  public void testCopyEventsWithNullDates() {
    model.createCalendar("Source", TEST_TIMEZONE);
    model.createCalendar("Target", TEST_TIMEZONE);

    model.copyEvents(null, LocalDate.now(), "Target", LocalDate.now());
  }

  @Test
  public void testEditEventAfterCalendarRenameWithoutUse() {
    model.createCalendar("OriginalCalendar", "America/New_York");
    LocalDateTime eventTime = LocalDateTime.now();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(eventTime)
            .setEndTime(eventTime.plusHours(1))
            .setLocation("ONLINE")
            .build();

    model.useCalendar("OriginalCalendar").createEvent(event);

    model.editCalendar("OriginalCalendar", PropertyType.CALENDARNAME, "NewCalendarName");

    EventIdentifier id = new EventIdentifier("Test Event", eventTime, eventTime.plusHours(1));
    TypingChange change = new PropertyChange(PropertyType.DESCRIPTION, "Updated Description");

    CalendarModel calendar = model.useCalendar("NewCalendarName");
    calendar.editEvent(id, change, false);

    List<Event> events = calendar.getEventsOnDate(eventTime.toLocalDate());
    assertEquals(1, events.size());
    assertEquals("Updated Description", events.get(0).getDesc());

    try {
      model.useCalendar("OriginalCalendar");
      fail("Should not be able to use old calendar name");
    } catch (IllegalArgumentException e) {
      // empty
    }
  }

  @Test
  public void testToString() {
    assertEquals("ONLINE", location.toString());
    assertEquals("PHYSICAL", location2.toString());
    assertEquals("PRIVATE", status1.toString());
    assertEquals("PUBLIC", status2.toString());
  }


}