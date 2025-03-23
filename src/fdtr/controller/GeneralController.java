package fdtr.controller;

import fdtr.Main;
import fdtr.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class GeneralController implements Initializable {

    // All input related stuff of the GUI are declared
    @FXML
    private ChoiceBox<String> month;
    @FXML
    private TextField year;
    @FXML
    private TextField nameOfUser;
    @FXML
    private TextField nameOfHead;
    @FXML
    private TextField department;

    // --------------- METHODS ---------------

    // Grabs the month set by the user in the FDTR GUI
    public String getMonth() { return month.getValue(); }

    // Same as above but for year
    public String getYear() { return year.getText(); }

    // Gets the name of user
    public String getNameOfUser() { return nameOfUser.getText(); }

    // Gets the name of head department
    public String getNameOfHead() { return nameOfHead.getText(); }

    // Gets the department the user belong
    public String getDepartment() { return department.getText(); }

    // Used to change the window from General to Working Hours
    @FXML
    private void changeWindowWorkingHours(ActionEvent event) {
        // This updates the output of month-year placeholder of Attendance window
        AttendanceController controller = new Main().getAttendanceController();
        AttendanceEmptyController controller2 = new Main().getAttendanceEmptyController();
        controller.setPlaceholderMonthYear(month.getValue(), year.getText());
        controller2.setPlaceholderMonthYear(month.getValue(), year.getText());

        // Change window to Working Hours
        Main.changeWindow("WorkingHours");
    }

    // Used to change the window from General to Attendance
    @FXML
    private void changeWindowAttendance(ActionEvent event) {
        // Getting the controller instance of the program
        AttendanceController attendanceController = new Main().getAttendanceController();
        WorkingHoursController workingHoursController = new Main().getWorkingHoursController();
        AttendanceEmptyController attendanceEmptyController = new Main().getAttendanceEmptyController();

        // Updates the Attendance data list to be displayed
        attendanceController.updateDataList(workingHoursController.getVariationSchedData(), workingHoursController.getStartingTimeData(), workingHoursController.getClosingTimeData());

        // Checks if the working hours data is empty, then show the respective window for it.
        if (workingHoursController.getWHDataViewer().getItems().isEmpty()) {
            Main.changeWindow("AttendanceEmpty");
        } else {
            Main.changeWindow("Attendance");
        }

        // Updates all the Attendance Controller with the date set by the user
        attendanceController.setPlaceholderMonthYear(month.getValue(), year.getText());
        attendanceEmptyController.setPlaceholderMonthYear(month.getValue(), year.getText());

        // Change window to Working Hours
        Main.changeWindow("Attendance");

    }

    // Close button of the window
    @FXML
    private void programClose(MouseEvent event) {
        System.exit(0);
    }

    // This prepares the input stuff before showing the General GUI
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Grabs the month of when the user is using the program. Then
        // sets it as a default choice. Uses Util class to get data.
        month.setValue(new Util().getCurrentMonth());

        // Adds all the months of the year to the choices
        month.getItems().addAll("January", "February", "March", "April", "May",
                "June", "July", "August", "September", "October", "November", "December");

        // Same purpose above but for year
        year.setText(new Util().getCurrentYear());

        // Attaches a listener to year text box to add constraint to their input.
        // We don't want them to input year that is above the year of the user.
        // Example: Year as of now is 2021, so we will only restrict them to year 2021
        // and below. Anything beyond that are blocked.
        year.textProperty().addListener((observable, oldValue, newValue) -> {

            // Checks if user is trying to input non integer characters, if they do
            // the program will simply ignore it.
            if (!newValue.matches("\\d*")) {
                year.setText(oldValue);
            } else {

                // Ignores the checks below if the input has not been set before.
                if (newValue.length() == 1) return;

                // Checks if the input year is in the range.
                // The range will be: 0 - current year.
                // Sometimes an exception is thrown when formatting the numbers so
                // we will just catch it and ignore.
                try {
                    int nv = Integer.parseInt(newValue);
                    if (nv > LocalDate.now().getYear() || nv < 0) {
                        year.setText(new Util().getCurrentYear());
                    } else if (nv == 11 || nv == 10) {
                        year.setText(String.valueOf(nv));
                    } else if (nv < 1) {
                        year.setText(new Util().getCurrentYear());
                    }
                } catch (NumberFormatException e) {}
            }
        });

    }
}
