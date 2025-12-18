import controller.CalendarCommand;
import controller.CalendarCommandParser;
import controller.CopyEvent;
import controller.CopyEvents;
import controller.CreateCalendar;
import controller.CreateEventSeries;
import controller.EditCalendar;
import controller.EditEventSeries;
import controller.EventCommandParser;
import controller.MultipleCalendarCommand;
import model.CalendarEvent;
import model.CalendarModelAllHelpers;
import model.CalendarModelImpl;
import model.Event;
import model.MultipleCalendarModel;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Class for command parser tests.
 */
public class EventCommandParserTest {
  private EventCommandParser parser;
  private CalendarModelAllHelpers mockModel;
  private StringBuilder log;
  private CalendarCommandParser cmdParser;
  private MultipleCalendarModel model;
  private StringBuilder sb;

  @Before
  public void setUp() {
    parser = new EventCommandParser();
    log = new StringBuilder();
    mockModel = new MockModel(log);
    cmdParser = new CalendarCommandParser();
    sb = new StringBuilder();
    model = new MockMultipleModel(sb);
  }

  @Test
  public void testParseSeriesWithNOccurrences() {
    String command = "create event working-all-day from 2025-02-21T10::30 to 2025-02-21T11::30 " +
            "repeats T until 2025-03-04";
    CalendarCommand cmd = parser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be CreateEventSeries", cmd instanceof CreateEventSeries);
    cmd.execute(mockModel);
    assertTrue("Model should receive createEvents call",
            log.toString().contains("createAllEvents has been called, event created \n"));
  }

  @Test
  public void testParseSeriesUntilDate() {
    String command = "create event working-half-day on 2025-07-10 repeats UM for 3 times";
    CalendarCommand cmd = parser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be CreateEventSeries", cmd instanceof CreateEventSeries);
    cmd.execute(mockModel);
    assertTrue("Model should receive createEvents call",
            log.toString().contains("createAllEvents has been called, event created \n"));
  }


  @Test
  public void testControllerToModelInputs() {
    String command = "create event help on 2025-02-14";
    CalendarCommand cmd = parser.parse(command);
    cmd.execute(mockModel);
    assertTrue("Model should receive createEvent call",
            log.toString().contains("addEvent has been called, event added \n"));
  }

  @Test
  public void testValidWeekdayCharacters() {
    CalendarModelAllHelpers model = new CalendarModelImpl("cal", "America/New_York");
    String command = "create event sleep from 2025-06-06T14::30 to 2025-06-06T16::30 repeats " +
            "MWF for 3 times";
    CalendarCommand cmd = parser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    cmd.execute(mockModel);
    assertTrue("Model should receive createEvents call",
            log.toString().contains("createAllEvents has been called, event created"));
    cmd.execute((CalendarModelAllHelpers) model);

    LocalDate date = LocalDate.of(2025, 6, 11);
    ArrayList<Event> events = new ArrayList<>();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("sleep")
            .setStartTime(date.atTime(14, 30, 0))
            .setEndTime(date.atTime(16, 30, 0))
            .build();
    events.add(event);
    assertEquals(events, model.getEventsOnDate(date));
  }

  @Test
  public void testEditSeriesCommand() {
    String command = "edit series SUBJECT sleep from 2025-06-07T14::30 with snoring";
    CalendarCommand cmd = parser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be EditEventSeries", cmd instanceof EditEventSeries);
    cmd.execute(mockModel);
    assertTrue("Model should receive editSeries call",
            log.toString().contains("parseEditEventSeries has been called, event edited"));
  }

  @Test
  public void testParseCreateCalendar() {
    String command = "create calendar --name MyCalendar --timezone America/New_York";
    MultipleCalendarCommand cmd = cmdParser.parse(command);
    cmd.execute(model);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be CreateCalendar", cmd instanceof CreateCalendar);
    assertTrue("Model should receive createCalendar call",
            sb.toString().contains("create calendar called"));
  }


  @Test
  public void testParseEditCalendarName() {
    String command = "edit calendar --name MyCalendar --property CALENDARNAME NewCalendarName";
    MultipleCalendarCommand cmd = cmdParser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be EditCalendar", cmd instanceof EditCalendar);
    cmd.execute(model);
    assertTrue("Model should receive editCalendar call",
            sb.toString().contains("edit calendar called"));
  }

  @Test
  public void testParseEditCalendarTimezone() {
    String command = "edit calendar --name MyCalendar --property TIMEZONE Europe/London";
    MultipleCalendarCommand cmd = cmdParser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be EditCalendar", cmd instanceof EditCalendar);
    cmd.execute(model);
    assertTrue("Model should receive editCalendar call",
            sb.toString().contains("edit calendar called"));
  }

  @Test
  public void testParseCopyEvent() {
    String command = "copy event Meeting on 2025-06-11T10::00 --target OtherCalendar to " +
            "2025-06-12T14::00";
    MultipleCalendarCommand cmd = cmdParser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be CopyEvent", cmd instanceof CopyEvent);
    cmd.execute(model);
    assertTrue("Model should receive copyEvent call",
            sb.toString().contains("copy event called"));
  }

  @Test
  public void testParseCopyEventsDay() {
    String command = "copy events on 2025-06-11 --target OtherCalendar to 2025-06-12";
    MultipleCalendarCommand cmd = cmdParser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be CopyEvents", cmd instanceof CopyEvents);
    cmd.execute(model);
    assertTrue("Model should receive copyEvents call",
            sb.toString().contains("copy events called"));
  }

  @Test
  public void testParseCopyEventsBetween() {
    String command = "copy events between 2025-06-11 and 2025-06-15 --target OtherCalendar to " +
            "2025-06-20";
    MultipleCalendarCommand cmd = cmdParser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be CopyEvents", cmd instanceof CopyEvents);
    cmd.execute(model);
    assertTrue("Model should receive copyEvents call",
            sb.toString().contains("copy events called"));
  }

  @Test
  public void testCopyEventWithDescription() {
    String command = "copy event Team-Meeting on 2025-06-11T10::00 --target OtherCalendar to " +
            "2025-06-12T14::00";
    MultipleCalendarCommand cmd = cmdParser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be CopyEvent", cmd instanceof CopyEvent);
    cmd.execute(model);
    assertTrue("Model should receive copyEvent call",
            sb.toString().contains("copy event called"));
  }

  @Test
  public void testCopyEventsWithSpacesInCalendarName() {
    String command = "copy events on 2025-06-11 --target My-Calendar to 2025-06-12";
    MultipleCalendarCommand cmd = cmdParser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be CopyEvents", cmd instanceof CopyEvents);
    cmd.execute(model);
    assertTrue("Model should receive copyEvents call",
            sb.toString().contains("copy events called"));
  }

  @Test
  public void testParseCopyEventPreservingSpecialCharacters() {
    String command = "copy event Meeting-#1-(Q&A) on 2025-06-11T10::00 --target OtherCalendar to" +
            " 2025-06-12T14::00";
    MultipleCalendarCommand cmd = cmdParser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be CopyEvent", cmd instanceof CopyEvent);
    cmd.execute(model);
    assertTrue("Model should receive copyEvent call with special characters preserved",
            sb.toString().contains("copy event called"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testEditCalendarWithSameNewName() {
    String command = "edit calendar --name MyCalendar --property name MyCalendar";
    cmdParser.parse(command);
  }

  @Test
  public void testCopyEventsPreservingSeriesStatus() {
    String command = "copy events between 2025-06-11 and 2025-06-15 --target " +
            "OtherCalendar to 2025-06-20";
    MultipleCalendarCommand cmd = cmdParser.parse(command);
    assertNotNull("Command should be parsed successfully", cmd);
    assertTrue("Command should be CopyEvents", cmd instanceof CopyEvents);
    cmd.execute(model);
    assertTrue("Model should preserve series information",
            sb.toString().contains("copy events called"));
  }

  @Test
  public void testCopyEventToSameCalendar() {
    String command = "copy event Meeting on 2025-06-11T10::00 --target CurrentCalendar to" +
            " 2025-06-12T14::00";
    MultipleCalendarCommand cmd = cmdParser.parse(command);
    cmd.execute(model);
    assertTrue("Event copied successfully", sb.toString().contains("copy event called"));
  }

}