
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;

import controller.CalendarController;
import controller.CalendarControllerGUIImpl;
import controller.CalendarControllerImpl;
import model.MultipleCalendarModelAllNames;
import model.MultipleCalendarModelImpl;
import view.CalendarGUIView;
import view.CalendarGUIViewImpl;
import view.CalendarView;
import view.CalendarViewImpl;

/**
 * This class serves as the entry point for the Calendar Program. It initializes the model,
 * view, and controller components of the application.
 * The program can be run in two modes: interactive mode, where user input is read from the
 * console, and headless mode, where events are processed from a file.
 */
public class CalendarProgram {

  /**
   * The main method serves as the entry point for the Calendar Program. It initializes
   * the model, view, and controller components for the application and manages the mode of
   * operation.
   * The program can run in two modes:
   * - Interactive mode: Reads input from the console.
   * - Headless mode: Processes events from a specified input file.
   *
   * @param args Command-line arguments
   */

  public static void main(String[] args) {
    MultipleCalendarModelAllNames model = new MultipleCalendarModelImpl();
    CalendarController controller;

    if (args.length != 0) {
      if (args.length > 1) {
        if (args[1].equalsIgnoreCase("headless")) {
          try {
            FileReader file = new FileReader(args[2]);
            CalendarView view = new CalendarViewImpl(System.out);
            controller = new CalendarControllerImpl(file, view, model);
            controller.run();
          } catch (FileNotFoundException e) {
            throw new RuntimeException("Error opening file: " + args[2]);
          }
        } else if (args[1].equalsIgnoreCase("interactive")) {
          Readable in = new InputStreamReader(System.in);
          CalendarView view = new CalendarViewImpl(System.out);
          controller = new CalendarControllerImpl(in, view, model);
          controller.run();
        } else {
          System.out.println("Invalid mode. Please specify, 'interactive', or 'headless'.");
          System.exit(0);
        }
      } else {
        System.out.println("Invalid mode. Please specify, 'interactive', or 'headless'.");
        System.exit(0);
      }

    } else {
      CalendarGUIView guiView = new CalendarGUIViewImpl();
      controller = new CalendarControllerGUIImpl(model, guiView);
      controller.run();
    }
  }
}
