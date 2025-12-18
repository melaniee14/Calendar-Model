import controller.CalendarControllerGUIImpl;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Comprehensive test class for CalendarControllerGUIImpl.
 */
public class CalendarControllerGUIImplTest {
  private MockGUIView mockView;
  private MockGUIMultipleModel mockModel;
  private CalendarControllerGUIImpl controller;
  private StringBuilder viewLog;
  private StringBuilder modelLog;

  @Before
  public void setUp() {
    viewLog = new StringBuilder();
    modelLog = new StringBuilder();
    mockView = new MockGUIView(viewLog);
    mockModel = new MockGUIMultipleModel(modelLog);
    controller = new CalendarControllerGUIImpl(mockModel, mockView);
  }

  // Constructor Tests
  @Test
  public void testConstructorSetsViewAndModel() {
    assertNotNull(controller);
    assertTrue(viewLog.toString().contains("setController called"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullModel() {
    new CalendarControllerGUIImpl(null, mockView);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullView() {
    new CalendarControllerGUIImpl(mockModel, null);
  }

  // Run Tests
  @Test
  public void testRun() {
    controller.run();
    assertTrue(viewLog.toString().contains("display called"));
    assertTrue(modelLog.toString().contains("useCalendar called with name: Default Calendar"));
  }

  // Calendar Creation Tests
  @Test
  public void testCreateNewCalendar() {
    controller.createNewCalendar("TestCal", "America/New_York");
    assertTrue(modelLog.toString().contains("createCalendar called with name: TestCal"));
    assertTrue(viewLog.toString().contains("success message: Created calendar: TestCal"));
  }

  @Test
  public void testCreateNewCalendarWithInvalidTimezone() {
    controller.createNewCalendar("TestCal", "Invalid/Timezone");
    assertTrue(viewLog.toString().contains("error message"));
    assertTrue(!modelLog.toString().contains("createCalendar called with name: TestCal"));
  }

  @Test
  public void testCreateNewCalendarWithEmptyName() {
    controller.createNewCalendar("", "America/New_York");
    assertTrue(viewLog.toString().contains("error message"));
  }

  // View Schedule Tests
  @Test
  public void testViewSchedule() {
    LocalDate testDate = LocalDate.now();
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.viewSchedule(testDate);


    assertTrue(viewLog.toString().contains("success"));
    assertTrue(viewLog.toString().contains("updateScheduleView called"));
    assertTrue(modelLog.toString().contains("useCalendar called with name: TestCal"));
  }

  @Test
  public void testViewScheduleWithNullDate() {
    controller.viewSchedule(null);
    assertTrue(viewLog.toString().contains("error message"));
  }

  // Add Event Tests
  @Test
  public void testAddEventSuccessfully() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Test Event", "2025-06-07T14:30",
            "2025-06-07T15:30", "ONLINE", "Description",
            "PUBLIC");
    assertTrue(viewLog.toString().contains("success message: Event added successfully"));
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "addEvent has been called, event added \n", modelLog.toString());
  }

  @Test
  public void testAddEventWithInvalidDates() {
    controller.addEvent("Test Event", "invalid-date",
            "2025-06-07T15:30",
            "Room 101", "Description", "CONFIRMED");
    assertTrue(viewLog.toString().contains("error message"));
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n", modelLog.toString());
  }

  @Test
  public void testAddEventWithEmptySubject() {
    controller.addEvent("", "2025-06-07T14:30",
            "2025-06-07T15:30",
            "online", "Description", "private");
    assertTrue(viewLog.toString().contains("error message"));
  }

  @Test
  public void testAddEventWithEndTimeBeforeStartTime() {
    controller.addEvent("Test Event", "2025-06-07T15:30",
            "2025-06-07T14:30",
            "online", "Description", "private");
    assertTrue(viewLog.toString().contains("error message"));
  }

  // Edit Event Tests
  @Test
  public void testEditEventSuccessfully() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Test Event", "2025-06-07T14:30",
            "2025-06-07T15:30",
            "online", "Description", "private");
    controller.editEvent("Test Event", "SUBJECT", "Updated Event");

    assertTrue(viewLog.toString().contains("Event updated"));
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "addEvent has been called, event added \n" +
            "editEvent has been called, event edited \n", modelLog.toString());
  }


  @Test
  public void testEditEventWithInvalidChangeType() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Test Event", "2025-06-07T14:30",
            "2025-06-07T15:30",
            "online", "Description", "private");
    controller.editEvent("Test Event", "INVALID_TYPE", "New Value");
    assertTrue(viewLog.toString().contains("error message"));
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "addEvent has been called, event added \n", modelLog.toString());
  }

  // Switch Calendar Tests
  @Test
  public void testSwitchCalendarSuccessfully() {
    controller.switchCalendar("TestCalendar");
    assertTrue(modelLog.toString().contains("useCalendar called with name: TestCalendar"));
    assertTrue(viewLog.toString().contains("success message"));
  }

  @Test
  public void testSwitchToNonexistentCalendar() {
    controller.switchCalendar("NonexistentCalendar");
    assertTrue(viewLog.toString().contains("error message"));
  }

  // Calendar Names Tests
  @Test
  public void testGetAvailableCalendarNames() {
    List<String> names = controller.getAvailableCalendarNames();
    assertTrue(modelLog.toString().contains("getAllNames called"));
    assertNotNull(names);
    assertTrue(names.contains("TestCalendar"));
  }

  // Event Names Tests
  @Test
  public void testGetAllEventNames() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Test Event", "2025-06-07T14:30",
            "2025-06-07T15:30",
            "online", "Description", "private");
    List<String> eventNames = controller.getAllEventNames();
    assertNotNull(eventNames);
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "addEvent has been called, event added \n", modelLog.toString());
  }

  // Time Change Tests
  @Test
  public void testEditEventStartTime() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Test Event", "2025-06-07T14:30",
            "2025-06-07T15:30",
            "online", "Description", "private");
    controller.editEvent("Test Event", "START TIME", "14:30");
    assertTrue(viewLog.toString().contains("Event updated"));

    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "addEvent has been called, event added \n" +
            "editEvent has been called, event edited \n", modelLog.toString());
  }

  @Test
  public void testEditEventEndTime() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Test Event", "2025-06-07T14:30",
            "2025-06-07T15:30",
            "online", "Description", "private");
    controller.editEvent("Test Event", "START TIME", "16:30");
    assertTrue(viewLog.toString().contains("Event updated"));
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "addEvent has been called, event added \n" +
            "editEvent has been called, event edited \n", modelLog.toString());
  }

  @Test
  public void testEditEventStartDate() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Test Event", "2025-06-07T14:30",
            "2025-06-07T15:30",
            "online", "Description", "private");
    controller.editEvent("Test Event", "START DATE", "2025-06-07");

    assertTrue(viewLog.toString().contains("Event added"));
    assertTrue(viewLog.toString().contains("Event updated"));
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "addEvent has been called, event added \n" +
            "editEvent has been called, event edited \n", modelLog.toString());
  }

  @Test
  public void testEditEventEndDate() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Test Event", "2025-06-07T14:30",
            "2025-06-07T15:30",
            "online", "Description", "private");
    controller.editEvent("Test Event", "END DATE", "2025-06-07");
    assertTrue(viewLog.toString().contains("Event added"));
    assertTrue(viewLog.toString().contains("Event updated"));
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "addEvent has been called, event added \n" +
            "editEvent has been called, event edited \n", modelLog.toString());
  }

  // Edge Cases
  @Test
  public void testAddEventSpanningMultipleDays() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Multi-day Event", "2025-06-07T14:30",
            "2025-06-08T15:30",
            "online", "Description", "private");
    assertTrue(viewLog.toString().contains("Event added"));
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "addEvent has been called, event added \n", modelLog.toString());
  }

  @Test
  public void testAddEventWithMinimalData() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Test Event", "2025-06-07T14:30",
            "2025-06-07T15:30",
            "N/A", "", "N/A");

    assertTrue(viewLog.toString().contains("Created calendar"));
    assertTrue(viewLog.toString().contains("Event added"));
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "addEvent has been called, event added \n", modelLog.toString());
  }

  @Test
  public void testViewScheduleOnEmptyDate() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Test Event", "2025-06-07T14:30",
            "2025-06-07T15:30",
            "N/A", "", "N/A");
    controller.viewSchedule(LocalDate.of(2000, 1, 1));

    assertTrue(viewLog.toString().contains("updateScheduleView called"));
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "addEvent has been called, event added \n", modelLog.toString());
  }

  @Test
  public void testEditEventWithNoChanges() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Test Event", "2025-06-07T14:30",
            "2025-06-07T15:30",
            "N/A", "", "N/A");
    controller.editEvent("Test Event", "SUBJECT", "Test Event");
    assertTrue(viewLog.toString().contains("Event updated"));
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "addEvent has been called, event added \n" +
            "editEvent has been called, event edited \n", modelLog.toString());
  }

  @Test
  public void testSwitchToSameCalendar() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.switchCalendar("TestCal");

    assertTrue(viewLog.toString().contains("Created calendar"));
    assertTrue(viewLog.toString().contains("Switched to calendar:"));
    assertEquals("getCurrentCalendarName called\n" +
            "useCalendar called with name: Default Calendar\n" +
            "createCalendar called with name: TestCal timezone: America/New_York\n" +
            "useCalendar called with name: TestCal\n" +
            "useCalendar called with name: TestCal\n", modelLog.toString());
  }

  @Test
  public void testEditEventNotValid() {
    controller.createNewCalendar("TestCal", "America/New_York");
    controller.addEvent("Test Event", "2025-06-07T14:30",
            "2025-06-07T15:30", "N/A", "", "N/A");
    controller.editEvent("Test Event", "Incorrect", "Test Event");
    controller.viewSchedule(LocalDate.of(2000, 1, 1));
    assertTrue(viewLog.toString().contains("error message"));

  }
}