import model.EventIdentifier;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Class for event identifier tests.
 */
public class EventIdentifierTest {
  private EventIdentifier eventId1;
  private EventIdentifier eventId2;
  private LocalDateTime startTime;
  private LocalDateTime endTime;

  @Before
  public void setUp() {
    startTime = LocalDateTime.of(2025, 6, 4, 10, 0);
    endTime = LocalDateTime.of(2025, 6, 4, 11, 0);
    eventId1 = new EventIdentifier("Test Event", startTime, endTime);
    eventId2 = new EventIdentifier("Test Event", startTime, endTime);
  }

  @Test
  public void testEqualsWithSameObject() {
    assertTrue(eventId1.equals(eventId1));
  }

  @Test
  public void testEqualsWithEqualObject() {
    assertEquals(eventId1, eventId2);
    assertEquals(eventId2, eventId1);
  }

  @Test
  public void testEqualsWithDifferentSubject() {
    EventIdentifier different = new EventIdentifier("Different Event", startTime, endTime);
    assertFalse(eventId1.equals(different));
  }

  @Test
  public void testEqualsWithDifferentStartTime() {
    LocalDateTime differentStart = startTime.plusHours(1);
    EventIdentifier different = new EventIdentifier("Test Event", differentStart, endTime);
    assertFalse(eventId1.equals(different));
  }

  @Test
  public void testEqualsWithDifferentEndTime() {
    LocalDateTime differentEnd = endTime.plusHours(1);
    EventIdentifier different = new EventIdentifier("Test Event", startTime, differentEnd);
    assertNotEquals(eventId1, different);
  }

  @Test
  public void testEqualsWithNull() {
    assertNotNull(eventId1);
  }

  @Test
  public void testEqualsWithDifferentClass() {
    assertNotEquals("Not an EventIdentifier", eventId1);
  }

  @Test
  public void testHashCodeConsistency() {
    assertEquals(eventId1.hashCode(), eventId1.hashCode());

    assertEquals(eventId1.hashCode(), eventId2.hashCode());
  }

  @Test
  public void testHashCodeDifference() {
    EventIdentifier different1 = new EventIdentifier("Different Event", startTime, endTime);
    EventIdentifier different2 = new EventIdentifier("Test Event",
            startTime.plusHours(1), endTime);
    EventIdentifier different3 = new EventIdentifier("Test Event",
            startTime, endTime.plusHours(1));

    assertNotEquals(eventId1.hashCode(), different1.hashCode());
    assertNotEquals(eventId1.hashCode(), different2.hashCode());
    assertNotEquals(eventId1.hashCode(), different3.hashCode());
  }

}