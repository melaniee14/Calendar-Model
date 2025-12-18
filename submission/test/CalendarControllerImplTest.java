import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.StringReader;

import controller.CalendarCommand;
import controller.CalendarController;
import controller.CalendarControllerImpl;
import controller.CommandParser;
import controller.CreateEvent;
import controller.EventCommandParser;
import model.CalendarModel;
import model.MultipleCalendarModel;
import model.MultipleCalendarModelImpl;
import view.CalendarView;
import view.CalendarViewImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Handles all tests for the calendar controller.
 */
public class CalendarControllerImplTest {
  Readable input;
  Appendable output;
  StringBuilder string;
  StringBuilder stringForCal;
  CalendarModel model;
  MultipleCalendarModel calendarModel;
  CalendarView view;
  CommandParser parser;


  @Before
  public void setUp() {
    output = new StringBuilder();
    string = new StringBuilder();
    stringForCal = new StringBuilder();
    calendarModel = new MockMultipleModel(stringForCal);
    model = new MockModel(string);
    view = new CalendarViewImpl(output);
    parser = new EventCommandParser();
  }


  @Test
  public void testInteractiveCommandWithNoCalendar() {
    String sb = "create calendar --name lalala --timezone America/Los_Angeles\n" +
            "create event sleep from 2025-06-07T14::30 to 2025-06-07T16::30 repeats S for" +
            " 3 times\n" +
            "use calendar --name lalala \n" +
            "exit";

    input = new StringReader(sb);
    CalendarView view = new CalendarViewImpl(output);
    CalendarController controller = new CalendarControllerImpl(input, view, calendarModel);


    controller.run();

    assertEquals("Command executed successfully\n" +
            "Invalid command: Calendar not in use.\n", output.toString());

    assertEquals("create calendar called \n" +
            "use calendar called \n", stringForCal.toString());
  }

  @Test
  public void testInteractiveInValidTimezone() {
    String sb = "create calendar --name lalaa --timezone NYC/USA \n" +
            "create calendar --name lala --timezone America/New_York \n" +
            "exit";
    input = new StringReader(sb);
    CalendarView view = new CalendarViewImpl(output);
    MultipleCalendarModel genuineModel = new MultipleCalendarModelImpl();
    CalendarController controller = new CalendarControllerImpl(input, view, genuineModel);
    controller.run();

    assertEquals("Error: Invalid timezone format\n" +
            "Command executed successfully\n", output.toString());
  }

  @Test
  public void testInteractiveInvalidDateAndTime() {
    String sb = "create calendar --name lala --timezone America/New_York \n" +
            "use calendar --name lala\n" +
            "create event sleep from 2025-06 to 2025-06-07T16::30 repeats S for 3 times\n" +
            "create event sleep from 2025-06-07T15:30 to 2025-06-07T16::30 repeats S for 3 times\n"
            +
            "create event sleep from 2025-06-07T15::30 to 2025-06-07T16::30 repeats S for " +
            "3 times\n"
            +
            "exit";
    input = new StringReader(sb);
    CalendarView view = new CalendarViewImpl(output);
    MultipleCalendarModel genuineModel = new MultipleCalendarModelImpl();
    CalendarController controller = new CalendarControllerImpl(input, view, genuineModel);
    controller.run();

    assertEquals("Command executed successfully\n" +
            "Error: Text '2025-06' could not be parsed at index 7\n" +
            "Error: Text '2025-06-07T15:30' could not be parsed at index 14\n" +
            "Command executed successfully\n", output.toString());
  }

  @Test
  public void testInteractiveInvalidEditCalendar() {
    String sb = "create calendar --name lala --timezone America/New_York \n" +
            "edit calendar --name lala --property NAME sleeping \n" +
            "edit calendar --name lala --property CALENDARNAME sleeping \n" +
            "exit";

    input = new StringReader(sb);
    CalendarView view = new CalendarViewImpl(output);
    MultipleCalendarModel genuineModel = new MultipleCalendarModelImpl();
    CalendarController controller = new CalendarControllerImpl(input, view, genuineModel);
    controller.run();

    assertEquals("Command executed successfully\n" +
            "Error: No enum constant model.PropertyType.NAME\n" +
            "Command executed successfully\n", output.toString());
  }

  @Test
  public void testInteractiveMissingCalendarName() {
    String sb = "create calendar --name --timezone America/New_York \n" +
            "exit";
    input = new StringReader(sb);
    CalendarView view = new CalendarViewImpl(output);
    MultipleCalendarModel genuineModel = new MultipleCalendarModelImpl();
    CalendarController controller = new CalendarControllerImpl(input, view, genuineModel);
    controller.run();

    assertEquals("Error: Invalid, please input name or timezone of calendar\n"
            , output.toString());
  }

  @Test
  public void testInteractiveCorrectlyPassed() {
    String sb = "create calendar --name working --timezone America/New_York\n" +
            "create calendar --name lalala --timezone America/Los_Angeles\n" +
            "copy event thinking on 2025-02-14T09::50 --target things to 2025-02-15T11::30\n" +
            "copy events on 2025-02-14 --target things to 2025-02-15\n" +
            "copy events between 2025-02-14 and 2025-02-17 --target things 2025-02-18\n" +
            "exit";
    input = new StringReader(sb);
    CalendarView view = new CalendarViewImpl(output);
    CalendarController controller = new CalendarControllerImpl(input, view, calendarModel);
    controller.run();

    assertEquals("create calendar called \n" +
            "create calendar called \n" +
            "copy event called \n" +
            "copy events called \n" +
            "copy events called \n", stringForCal.toString());
  }

  @Test
  public void testHeadlessCommandWithNoCalendar() throws FileNotFoundException {
    input = new BufferedReader(new FileReader(
            "/Users/melan/Desktop/cs3500/Calendar/test/multipleInvalidCommands.txt"));
    CalendarController controller = new CalendarControllerImpl(input, view, calendarModel);


    controller.run();

    assertEquals("Command executed successfully\n" +
            "Invalid command: Calendar not in use.\n", output.toString());

    assertEquals("create calendar called \n" +
            "use calendar called \n", stringForCal.toString());
  }

  @Test
  public void testHeadlessValidCommandSequence() throws FileNotFoundException {
    input = new FileReader("/Users/melan/Desktop/cs3500/Calendar/test/test.txt");
    MultipleCalendarModel genuineModel = new MultipleCalendarModelImpl();
    CalendarController controller = new CalendarControllerImpl(input, view, genuineModel);
    controller.run();

    assertEquals("Command executed successfully\n" +
            "Command executed successfully\n" +
            "Invalid command: Calendar not in use.\n" +
            "Command executed successfully\n" +
            "Command executed successfully\n" +
            "Command executed successfully\n" +
            "sleep (14:30 - 16:30)\n" +
            "awake (14:30 - 16:30)\n" +
            "awake (14:30 - 16:30)\n" +
            "Command executed successfully\n" +
            "Command executed successfully\n" +
            "sleep (15:30 - 16:30)\n" +
            "awake (15:30 - 16:30)\n" +
            "awake (15:30 - 16:30)\n" +
            "Command executed successfully\n" +
            "Command executed successfully\n" +
            "sleep (09:30 - 10:30)\n" +
            "awake (09:30 - 10:30)\n" +
            "awake (09:30 - 10:30)\n", output.toString());
  }

  @Test
  public void testHeadlessInvalidDateAndTime() throws FileNotFoundException {
    input = new FileReader("/Users/melan/Desktop/cs3500/Calendar/res/txtwithinvalid.txt");
    MultipleCalendarModel genuineModel = new MultipleCalendarModelImpl();
    CalendarView view = new CalendarViewImpl(output);
    CalendarController controller = new CalendarControllerImpl(input, view, genuineModel);
    controller.run();
    assertEquals("Command executed successfully\n" +
            "Command executed successfully\n" +
            "Command executed successfully\n" +
            "sleep (14:30 - 16:30)\n" +
            "Command executed successfully\n" +
            "Invalid command: get events\n" +
            "Command executed successfully\n" +
            "Error: Model.CalendarEvent duplicate exists\n" +
            "Error: No enum constant model.PropertyType.NAME\n" +
            "Command executed successfully\n" +
            "Command executed successfully\n" +
            "Command executed successfully\n" +
            "Command executed successfully\n" +
            "help (08:00 - 17:00)\n" +
            "lalala (09:50 - 10:30)\n" +
            "sleep (14:30 - 18:30)\n" +
            "awake (14:30 - 18:30)\n" +
            "awake (14:30 - 18:30)\n" +
            "Command executed successfully\n" +
            "Available.\n", output.toString());
  }


  @Test
  public void testInteractiveInvalidCommands() {
    String sb = "invalid \n" +
            "invalid \n" +
            "exit";
    input = new StringReader(sb);
    CalendarView view = new CalendarViewImpl(output);
    CalendarController controller = new CalendarControllerImpl(input, view, calendarModel);

    controller.run();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(out);
    System.setOut(printStream);
    assertEquals("", string.toString());
  }


  @Test
  public void testScriptParsing() {
    String script = "create event help from 2020-02-14";
    CalendarCommand command = parser.parse(script);
    assertNotNull(command);
    assertTrue(command instanceof CreateEvent);
  }


  @Test
  public void testEdit() {
    String sb = "create calendar --name lala --timezone America/New_York\n" +
            "use calendar --name lala\n" +
            "create event help from 2020-02-14 \n" +
            "create event lalala from 2020-02-14T09:50 to 2020-02-14T10:30 \n" +
            "create event lalalalla from 2025-06-06T14:30 to 2025-06-06T16:30 repeats S for " +
            "3 times \n" +
            "exit";
    input = new StringReader(sb);
    CalendarView view = new CalendarViewImpl(output);
    CalendarController controller = new CalendarControllerImpl(input, view, calendarModel);
    controller.run();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream printStream = new PrintStream(out);
    System.setOut(printStream);

    assertEquals("create calendar called \n" +
            "use calendar called \n" +
            "get calendar name called \n" +
            "use calendar called \n", stringForCal.toString());
  }


}


