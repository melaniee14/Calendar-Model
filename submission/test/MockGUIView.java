
import controller.CalendarControllerGUI;
import model.Event;
import view.CalendarGUIView;
import java.util.List;

/**
 * A mock implementation of CalendarGUIView for testing purposes.
 */
public class MockGUIView implements CalendarGUIView {
  private final StringBuilder log;

  public MockGUIView(StringBuilder log) {
    this.log = log;
  }

  @Override
  public void display() {
    log.append("display called\n");
  }

  @Override
  public void updateScheduleView(List<Event> events) {
    log.append("updateScheduleView called with ").append(events.size()).append(" events\n");
  }

  @Override
  public void showErrorMessage(String message) {
    log.append("error message: ").append(message).append("\n");
  }

  @Override
  public void showSuccessMessage(String message) {
    log.append("success message: ").append(message).append("\n");
  }

  @Override
  public void setController(CalendarControllerGUI controller) {
    log.append("setController called\n");
  }

  public String getLog() {
    return log.toString();
  }
}