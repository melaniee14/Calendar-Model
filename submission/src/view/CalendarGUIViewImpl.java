package view;

import controller.CalendarControllerGUI;
import model.Event;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JFrame;
import javax.swing.SpinnerDateModel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * A concrete, Swing-based implementation of the CalendarGUIView interface.
 * Handles user input and renders the graphical user interface for the calendar application.
 */
public class CalendarGUIViewImpl extends JFrame implements CalendarGUIView {
  private final DefaultTableModel tableModel;
  private final DatePickerPanel datePicker;
  private final JLabel statusLabel;
  private CalendarControllerGUI controller;

  /**
   * Constructs the CalendarGUIViewImpl and sets up the GUI components.
   */
  public CalendarGUIViewImpl() {
    super("Calendar Application");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1200, 600);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    this.tableModel = new DefaultTableModel(new String[]{"Subject", "Start Time", "End Time"},
            0);

    JTable scheduleTable = new JTable(tableModel) {
      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };

    JScrollPane scrollPane = new JScrollPane(scheduleTable);

    this.datePicker = new DatePickerPanel();

    this.statusLabel = new JLabel("Status: Ready");

    JButton viewButton = new JButton("View Schedule");
    JButton addButton = new JButton("Add Event");
    JButton editButton = new JButton("Edit Event");
    JButton switchCalendarButton = new JButton("Switch Calendar");
    JButton createCalendarButton = new JButton("Create Calendar");

    viewButton.addActionListener(e -> {
      LocalDate selectedDate = datePicker.getDate();
      controller.viewSchedule(selectedDate);
    });

    addButton.addActionListener(e -> showAddEventDialog());
    editButton.addActionListener(e -> showEditEventDialog());

    switchCalendarButton.addActionListener(e -> {
      String selectedCalendar = showCalendarDropdown();
      if (selectedCalendar != null && !selectedCalendar.isEmpty()) {
        controller.switchCalendar(selectedCalendar);
      }
    });

    createCalendarButton.addActionListener(e -> {
      String calendarName = JOptionPane.showInputDialog(this,
              "Enter the name of the new calendar:",
              "Create New Calendar",
              JOptionPane.QUESTION_MESSAGE);
      String timezone = JOptionPane.showInputDialog(this,
              "Enter the timezone (e.g., 'UTC', 'America/New_York'):",
              "Create New Calendar",
              JOptionPane.QUESTION_MESSAGE);

      if (calendarName != null && timezone != null && !calendarName.isEmpty() &&
              !timezone.isEmpty()) {
        controller.createNewCalendar(calendarName, timezone);
      }
    });

    JPanel controlPanel = new JPanel(new FlowLayout());
    controlPanel.add(new JLabel("Select Date:"));
    controlPanel.add(datePicker);
    controlPanel.add(viewButton);
    controlPanel.add(addButton);
    controlPanel.add(editButton);
    controlPanel.add(switchCalendarButton);
    controlPanel.add(createCalendarButton);

    mainPanel.add(controlPanel, BorderLayout.NORTH);
    mainPanel.add(scrollPane, BorderLayout.CENTER);
    mainPanel.add(statusLabel, BorderLayout.SOUTH);

    setContentPane(mainPanel);
    setLocationRelativeTo(null);
  }

  @Override
  public void display() {
    setVisible(true);
  }

  @Override
  public void updateScheduleView(List<Event> events) {
    tableModel.setRowCount(0);
    for (Event event : events) {
      tableModel.addRow(new Object[]{
              event.getSubject(),
              event.getStartTime().toString(),
              event.getEndTime().toString(),
              event.getLocation()
      });
    }
    statusLabel.setText("Status: Schedule updated successfully.");
  }

  @Override
  public void showErrorMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Error",
            JOptionPane.ERROR_MESSAGE);
    statusLabel.setText("Error: " + message);
  }

  @Override
  public void showSuccessMessage(String message) {
    JOptionPane.showMessageDialog(this, message, "Success",
            JOptionPane.INFORMATION_MESSAGE);
    statusLabel.setText(message);
  }

  @Override
  public void setController(CalendarControllerGUI controller) {
    this.controller = controller;
  }

  /**
   * Displays a dialog for adding a new event to the calendar.
   */
  private void showAddEventDialog() {
    JTextField subjectField = new JTextField(15);
    JTextField descField = getJTextField();
    JSpinner startTimeSpinner = createTimeSpinner();
    JSpinner endTimeSpinner = createTimeSpinner();

    String[] locationTypes = new String[]{"N/A", "Online", "Physical"};
    String[] statusTypes = new String[]{"N/A", "Public", "Private"};
    JComboBox<String> locationComboBox = new JComboBox<>(locationTypes);
    JComboBox<String> statusComboBox = new JComboBox<>(statusTypes);
    JPanel formPanel = new JPanel(new GridLayout(6, 2));
    formPanel.add(new JLabel("Subject:"));
    formPanel.add(subjectField);
    formPanel.add(new JLabel("Start Time:"));
    formPanel.add(startTimeSpinner);
    formPanel.add(new JLabel("End Time:"));
    formPanel.add(endTimeSpinner);
    formPanel.add(new JLabel("Description: "));
    formPanel.add(descField);
    formPanel.add(new JLabel("Location:"));
    formPanel.add(locationComboBox);
    formPanel.add(new JLabel("Status:"));
    formPanel.add(statusComboBox);


    int result = JOptionPane.showConfirmDialog(this, formPanel,
            "Add New Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

    if (result == JOptionPane.OK_OPTION) {
      try {
        String subject = subjectField.getText();
        String startDate = datePicker.getDate().toString();
        String endDate = datePicker.getDate().toString();
        String start = startTimeSpinner.getValue().toString().substring(11, 16);
        String end = endTimeSpinner.getValue().toString().substring(11, 16);
        String startWithDate = startDate + "T" + start;
        String endWithDate = endDate + "T" + end;
        String description = descField.getText();
        String location = Objects.requireNonNull(locationComboBox.getSelectedItem()).toString();
        String status = Objects.requireNonNull(statusComboBox.getSelectedItem()).toString();


        controller.addEvent(subject, startWithDate, endWithDate, location, description, status);
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
                "Error creating event: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  private static JTextField getJTextField() {
    JTextField descField = new JTextField("A description is optional.") {
    };
    descField.addFocusListener(new FocusListener() {

      /**
       * Invoked when a component gains the keyboard focus.
       *
       * @param e the event to be processed
       */
      @Override
      public void focusGained(FocusEvent e) {
        if (descField.getText().equals("A description is optional.")) {
          descField.setText("");

        }
      }

      /**
       * Invoked when a component loses the keyboard focus.
       *
       * @param e the event to be processed
       */
      @Override
      public void focusLost(FocusEvent e) {
        // must include method for focus listeners
      }


    });
    return descField;
  }

  private void showEditEventDialog() {
    List<String> allEvents = controller.getAllEventNames();
    if (allEvents.isEmpty()) {
      JOptionPane.showMessageDialog(this, "No events to edit.",
              "Error", JOptionPane.ERROR_MESSAGE);
    } else {
      JComboBox<String> allEventComboBox = new JComboBox<String>(allEvents.toArray(new String[0]));

      String[] changeTypes = new String[]{"Subject",
          "Start date","End date", "Start time", "End time",
          "Location",
          "Description",
          "Status"};

      JComboBox<String> possibleChanges = new JComboBox<String>(changeTypes);
      JPanel formPanel = new JPanel(new GridLayout(4, 2));

      JTextField changeField = new JTextField(15);
      formPanel.add(new JLabel("Select event:"));
      formPanel.add(allEventComboBox);
      formPanel.add(new JLabel("Change Type:"));
      formPanel.add(possibleChanges);
      formPanel.add(new JLabel("Change:"));
      formPanel.add(changeField);


      int result = JOptionPane.showConfirmDialog(this, formPanel,
              "Edit an Event", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

      if (result == JOptionPane.OK_OPTION) {
        try {
          String change = changeField.getText();

          controller.editEvent(Objects.requireNonNull(allEventComboBox
                          .getSelectedItem()).toString().substring(0,
                          allEventComboBox.getSelectedItem().toString().length() - 17),
                  Objects.requireNonNull(possibleChanges.getSelectedItem()).toString()
                          .toUpperCase(), change);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog(this, ex.getMessage(),
                  "Error", JOptionPane.ERROR_MESSAGE);
        }
      }


    }

  }


  /**
   * Displays a dropdown for selecting a calendar.
   *
   * @return The selected calendar name or null if no selection was made.
   */
  private String showCalendarDropdown() {
    List<String> calendars = controller.getAvailableCalendarNames();
    if (calendars.isEmpty()) {
      JOptionPane.showMessageDialog(this, "No calendars available to switch to.",
              "Error", JOptionPane.ERROR_MESSAGE);
      return null;
    }

    String selectedCalendar = (String) JOptionPane.showInputDialog(this,
            "Select a calendar:",
            "Switch Calendar",
            JOptionPane.QUESTION_MESSAGE,
            null,
            calendars.toArray(),
            calendars.get(0));

    return selectedCalendar;
  }

  /**
   * Creates a spinner for time input, set to display hours and minutes.
   *
   * @return A JSpinner configured for time input.
   */
  private JSpinner createTimeSpinner() {
    SpinnerDateModel model = new SpinnerDateModel();
    JSpinner spinner = new JSpinner(model);
    spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
    return spinner;
  }
}