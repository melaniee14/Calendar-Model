package view;

import java.io.IOException;
import java.util.List;

import model.Event;

/**
 * A concrete implementation of the View.CalendarView interface that renders information
 * in a plain text format.
 */
public class CalendarViewImpl implements CalendarView {
  private final Appendable out;

  /**
   * Constructs a new View.CalendarViewImpl with the given Appendable and controller.
   *
   * @param out the Appendable to which the calendar information will be written
   */
  public CalendarViewImpl(Appendable out) {
    if (out == null) {
      throw new IllegalArgumentException("Output and controller cannot be null");
    }
    this.out = out;
  }

  @Override
  public Appendable getOut() {
    return out;
  }

  @Override
  public void renderMessage(String message) {
    try {
      out.append(message).append("\n");
    }
    catch (IOException e) {
      throw new IllegalStateException("Input stream is incorrect");
    }
  }

  @Override
  public void renderEvents(List<Event> events) {
    try {
      if (events == null || events.isEmpty()) {
        out.append("No events found.\n");
        return;
      }

      for (Event event : events) {
        out.append(event.getSubject());

        out.append(" (")
                .append(event.getStartTime().toLocalTime().toString())
                .append(" - ")
                .append(event.getEndTime().toLocalTime().toString())
                .append(")");

        if (!event.getStartTime().toLocalDate().equals(event.getEndTime().toLocalDate())) {
          out.append(" From: ")
                  .append(event.getStartTime().toLocalDate().toString())
                  .append(" To: ")
                  .append(event.getEndTime().toLocalDate().toString());
        }

        if (event.getLocation() != null) {
          out.append(" located at ").append(event.getLocation().toString());
        }

        if (event.getStatus() != null) {
          out.append(" ").append(event.getStatus().toString());
        }

        if (event.getDesc() != null && !event.getDesc().isEmpty()) {
          out.append("\n  Description: ")
                  .append(event.getDesc());
        }

        out.append("\n");
      }
    }
    catch (IOException e) {
      throw new IllegalStateException("Failed to write");
    }
  }


  @Override
  public void renderStatus(String status) {
    try {
      out.append("Status: ")
              .append(status)
              .append("\n");
    }
    catch (IOException e) {
      throw new IllegalStateException("Failed to write to output");
    }
  }
}