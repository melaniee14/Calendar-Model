import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.ArrayList;

import model.CalendarEvent;
import model.CalendarModel;
import model.CalendarModelImpl;
import model.Event;
import view.CalendarView;
import view.CalendarViewImpl;

/**
 * Handles all tests for the calendar view.
 */
public class CalendarViewImplTest {
  private StringBuilder output;
  private CalendarView view;
  private LocalDateTime sampleStartTime;
  private LocalDateTime sampleEndTime;

  @Before
  public void setUp() {
    output = new StringBuilder();
    output = new StringBuilder();
    CalendarModel model = new CalendarModelImpl("Test");
    view = new CalendarViewImpl(output);
    sampleStartTime = LocalDateTime.of(2025, 6, 3, 10, 0);
    sampleEndTime = LocalDateTime.of(2025, 6, 3, 11, 0);

  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorWithNullAppendable() {
    new CalendarViewImpl(null);
  }

  @Test
  public void testRenderMessage() {
    view.renderMessage("Test message");
    assertEquals("Test message\n", output.toString());
  }

  @Test
  public void testRenderStatus() {
    view.renderStatus("Available");
    assertEquals("Status: Available\n", output.toString());
  }

  @Test
  public void testRenderEventWithMaxTimeValues() {
    ArrayList<Event> events = new ArrayList<>();
    LocalDateTime maxDateTime = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("End of Time Event")
            .setStartTime(maxDateTime)
            .setEndTime(maxDateTime)
            .build();
    events.add(event);
    view.renderEvents(events);
    assertEquals(true, output.toString().contains("23:59"));
  }

  @Test
  public void testRenderEventAt24HourBoundary() {
    ArrayList<Event> events = new ArrayList<>();
    LocalDateTime startTime = LocalDateTime.of(2025, 6, 3, 23, 59);
    LocalDateTime endTime = LocalDateTime.of(2025, 6, 4, 0, 1);
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Midnight Event")
            .setStartTime(startTime)
            .setEndTime(endTime)
            .build();
    events.add(event);
    view.renderEvents(events);
    assertEquals(true, output.toString().contains("23:59")
            && output.toString().contains("00:01"));
  }

  @Test
  public void testRenderEventsWithIdenticalTimes() {
    ArrayList<Event> events = new ArrayList<>();
    LocalDateTime time = LocalDateTime.of(2025, 6, 3, 12, 0);
    events.add(new CalendarEvent.EventBuilder()
            .setSubject("Event 1")
            .setStartTime(time)
            .setEndTime(time)
            .build());
    events.add(new CalendarEvent.EventBuilder()
            .setSubject("Event 2")
            .setStartTime(time)
            .setEndTime(time)
            .build());
    view.renderEvents(events);
    String result = output.toString();
    assertEquals(true, result.contains("Event 1") && result.contains("Event 2"));
  }

  @Test
  public void testRenderEventsEmpty() {
    view.renderEvents(new ArrayList<>());
    assertEquals("No events found.\n", output.toString());
  }

  @Test
  public void testRenderEventsNull() {
    view.renderEvents(null);
    assertEquals("No events found.\n", output.toString());
  }

  @Test
  public void testRenderSingleBasicEvent() {
    ArrayList<Event> events = new ArrayList<>();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Basic Meeting")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .build();
    events.add(event);

    view.renderEvents(events);
    assertEquals("Basic Meeting (10:00 - 11:00)\n", output.toString());
  }

  @Test
  public void testRenderEventWithAllFields() {
    ArrayList<Event> events = new ArrayList<>();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Full Meeting")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .setDesc("Important discussion")
            .build();
    events.add(event);

    view.renderEvents(events);
    assertEquals("Full Meeting (10:00 - 11:00) located at ONLINE PUBLIC\n  " +
                    "Description: Important discussion\n",
            output.toString());
  }

  @Test
  public void testRenderMultiDayEvent() {
    ArrayList<Event> events = new ArrayList<>();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Conference")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime.plusDays(1))
            .setLocation("PHYSICAL")
            .build();
    events.add(event);

    view.renderEvents(events);
    assertEquals("Conference (10:00 - 11:00) From: 2025-06-03 To: 2025-06-04 " +
                    "located at PHYSICAL\n",
            output.toString());
  }

  @Test
  public void testIOExceptionHandling() {
    Appendable failingAppendable = new Appendable() {
      @Override
      public Appendable append(CharSequence csq) throws java.io.IOException {
        throw new java.io.IOException("Test IO Exception");
      }

      @Override
      public Appendable append(CharSequence csq, int start, int end) throws java.io.IOException {
        throw new java.io.IOException("Test IO Exception");
      }

      @Override
      public Appendable append(char c) throws java.io.IOException {
        throw new java.io.IOException("Test IO Exception");
      }
    };

    CalendarView failingView = new CalendarViewImpl(failingAppendable);

    try {
      failingView.renderMessage("Test");
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      assertEquals("Input stream is incorrect", e.getMessage());
    }

    try {
      failingView.renderStatus("Test");
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      assertEquals("Failed to write to output", e.getMessage());
    }

    ArrayList<Event> events = new ArrayList<>();
    events.add(new CalendarEvent.EventBuilder()
            .setSubject("Test")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .build());

    try {
      failingView.renderEvents(events);
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      assertEquals("Failed to write", e.getMessage());
    }
  }

  @Test
  public void testRenderMultipleEvents() {
    ArrayList<Event> events = new ArrayList<>();
    CalendarEvent event1 = new CalendarEvent.EventBuilder()
            .setSubject("Meeting 1")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .build();

    CalendarEvent event2 = new CalendarEvent.EventBuilder()
            .setSubject("Meeting 2")
            .setStartTime(sampleStartTime.plusHours(2))
            .setEndTime(sampleEndTime.plusHours(2))
            .setLocation("PHYSICAL")
            .build();

    events.add(event1);
    events.add(event2);

    view.renderEvents(events);
    assertEquals("Meeting 1 (10:00 - 11:00) located at ONLINE\n" +
                    "Meeting 2 (12:00 - 13:00) located at PHYSICAL\n",
            output.toString());
  }

  @Test
  public void testRenderEventWithVeryLongSubject() {
    ArrayList<Event> events = new ArrayList<>();
    String longSubject = "A".repeat(100); // Create a very long subject
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject(longSubject)
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .build();
    events.add(event);

    view.renderEvents(events);
    assertTrue(output.toString().contains(longSubject));
  }

  @Test
  public void testRenderEventWithSpecialCharacters() {
    ArrayList<Event> events = new ArrayList<>();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Meeting!@#$%^&*()")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .build();
    events.add(event);

    view.renderEvents(events);
    assertEquals("Meeting!@#$%^&*() (10:00 - 11:00)\n", output.toString());
  }

  @Test
  public void testRenderEventWithEmptyDescription() {
    ArrayList<Event> events = new ArrayList<>();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Meeting")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setDesc("")
            .build();
    events.add(event);

    view.renderEvents(events);
    assertEquals("Meeting (10:00 - 11:00)\n", output.toString());
  }

  @Test
  public void testRenderEventWithNullDescription() {
    ArrayList<Event> events = new ArrayList<>();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Meeting")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setDesc(null)
            .build();
    events.add(event);

    view.renderEvents(events);
    assertEquals("Meeting (10:00 - 11:00)\n", output.toString());
  }

  @Test
  public void testRenderMessageWithEmptyString() {
    view.renderMessage("");
    assertEquals("\n", output.toString());
  }

  @Test
  public void testRenderMessageWithSpecialCharacters() {
    view.renderMessage("Test!@#$%^&*()_+");
    assertEquals("Test!@#$%^&*()_+\n", output.toString());
  }

  @Test
  public void testRenderStatusWithEmptyString() {
    view.renderStatus("");
    assertEquals("Status: \n", output.toString());
  }

  @Test
  public void testRenderEventWithMultiLineDescription() {
    ArrayList<Event> events = new ArrayList<>();
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Meeting")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setDesc("Line 1\nLine 2\nLine 3")
            .build();
    events.add(event);

    view.renderEvents(events);
    assertTrue(output.toString().contains("Description: Line 1\nLine 2\nLine 3"));
  }

  @Test
  public void testRenderEventsWithSameStartTime() {
    ArrayList<Event> events = new ArrayList<>();
    CalendarEvent event1 = new CalendarEvent.EventBuilder()
            .setSubject("Meeting A")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .build();

    CalendarEvent event2 = new CalendarEvent.EventBuilder()
            .setSubject("Meeting B")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime.plusHours(1))
            .build();

    events.add(event1);
    events.add(event2);

    view.renderEvents(events);
    assertTrue(output.toString().contains("Meeting A"));
    assertTrue(output.toString().contains("Meeting B"));
  }

  @Test
  public void testRenderMessageWithNewlines() {
    view.renderMessage("Line 1\nLine 2\nLine 3");
    assertEquals("Line 1\nLine 2\nLine 3\n", output.toString());
  }

  @Test
  public void testConsecutiveRenderCalls() {
    view.renderMessage("First Message");
    view.renderMessage("Second Message");
    assertEquals("First Message\nSecond Message\n", output.toString());
  }

  @Test
  public void testPrintCommandEventDetails() {
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .setLocation("ONLINE")
            .setStatus("PUBLIC")
            .setDesc("Test Description")
            .build();
    ArrayList<Event> events = new ArrayList<>();
    events.add(event);
    view.renderEvents(events);
    String output = this.output.toString();
    assertTrue(output.contains("Test Event"));
    assertTrue(output.contains("ONLINE"));
    assertTrue(output.contains("PUBLIC"));
    assertTrue(output.contains("Test Description"));
  }

  @Test
  public void testPrintCommandFormat() {
    CalendarEvent event = new CalendarEvent.EventBuilder()
            .setSubject("Test Event")
            .setStartTime(sampleStartTime)
            .setEndTime(sampleEndTime)
            .build();
    ArrayList<Event> events = new ArrayList<>();
    events.add(event);
    view.renderEvents(events);
    String expected = "Test Event (10:00 - 11:00)\n";
    assertEquals(expected, this.output.toString());
  }

}