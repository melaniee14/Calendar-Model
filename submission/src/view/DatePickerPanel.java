package view;


import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.SpinnerNumberModel;

import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;


/**
 * Class for the panel used to select a date when adding an event or viewing the schedule.
 */
public class DatePickerPanel extends JPanel {
  private final JSpinner yearSpinner;
  private final JComboBox<String> monthComboBox;
  private final JSpinner daySpinner;

  /**
   * Creates a @DatePickerPanel object.
   */
  public DatePickerPanel() {
    setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

    String[] months = {"January", "February", "March", "April", "May", "June",
      "July", "August", "September", "October", "November", "December"};
    monthComboBox = new JComboBox<>(months);

    SpinnerNumberModel yearModel = new SpinnerNumberModel(
            LocalDate.now().getYear(),
            LocalDate.now().getYear() - 100,
            LocalDate.now().getYear() + 100,
            1);
    yearSpinner = new JSpinner(yearModel);

    SpinnerNumberModel dayModel = new SpinnerNumberModel(1, 1, 31, 1);
    daySpinner = new JSpinner(dayModel);

    setDate(LocalDate.now());

    monthComboBox.addActionListener(e -> updateDaySpinner());
    yearSpinner.addChangeListener(e -> updateDaySpinner());

    add(monthComboBox);
    add(daySpinner);
    add(yearSpinner);
  }

  private void updateDaySpinner() {
    int year = (int) yearSpinner.getValue();
    int month = monthComboBox.getSelectedIndex() + 1;
    int day = (int) daySpinner.getValue();

    int maxDay = YearMonth.of(year, month).lengthOfMonth();

    SpinnerNumberModel model = (SpinnerNumberModel) daySpinner.getModel();
    model.setMaximum(maxDay);

    if (day > maxDay) {
      daySpinner.setValue(maxDay);
    }
  }

  /**
   * Shows what the date looks like.
   *
   * @return the current date being selected by the user
   */
  public LocalDate getDate() {
    int year = (int) yearSpinner.getValue();
    int month = monthComboBox.getSelectedIndex() + 1;
    int day = (int) daySpinner.getValue();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    if (month < 10) {
      return LocalDate.parse(year + "-0" + month + "-" + day, formatter);
    } else {
      return LocalDate.parse(year + "-" + month + "-" + day, formatter);
    }
  }

  /**
   * Sets the date to the inputted value.
   *
   * @param date the date the picker should now be set to
   */
  public void setDate(LocalDate date) {
    yearSpinner.setValue(date.getYear());
    monthComboBox.setSelectedIndex(date.getMonthValue() - 1);
    daySpinner.setValue(date.getDayOfMonth());
  }
}
