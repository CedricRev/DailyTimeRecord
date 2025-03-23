package fdtr.controller;

import fdtr.Main;
import fdtr.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class WorkingHoursController implements Initializable {

    // key: Day
    // values: Schedule Type
    private Map<String, ArrayList<String>> variationSchedData = new HashMap<>();

    // key: Day
    // values: Starting Time
    private Map<String, ArrayList<LocalTime>> startingTimeData = new HashMap<>();

    // key: Day
    // values: Closing Time
    private Map<String, ArrayList<LocalTime>> closingTimeData = new HashMap<>();


    // All GUI stuff for Working Hours window
    @FXML
    private ChoiceBox<String> daysOfTheWeek;
    @FXML
    private ChoiceBox<String> variationSched;
    @FXML
    private ChoiceBox<String> startingTimeMeridiem;
    @FXML
    private ChoiceBox<String> closingTimeMeridiem;
    @FXML
    private ListView<String> WHDataViewer;
    @FXML
    private TextField startingTimeHours;
    @FXML
    private TextField startingTimeMinutes;
    @FXML
    private TextField closingTimeHours;
    @FXML
    private TextField closingTimeMinutes;

    public ListView<String> getWHDataViewer() {
        return WHDataViewer;
    }

    public Map<String, ArrayList<String>> getVariationSchedData() { return variationSchedData; }

    public Map<String, ArrayList<LocalTime>> getStartingTimeData() { return startingTimeData; }

    public Map<String, ArrayList<LocalTime>> getClosingTimeData() { return closingTimeData; }

    // Method of Click to add button in GUI
    @FXML
    private void storeData(ActionEvent event) {

        // Grabs all the input
        String day = daysOfTheWeek.getValue();
        String variation = variationSched.getValue();

        int sHour = Integer.parseInt(startingTimeHours.getText());
        int sMinute = Integer.parseInt(startingTimeMinutes.getText());
        String sMeridiem = startingTimeMeridiem.getValue();

        int cHour = Integer.parseInt(closingTimeHours.getText());
        int cMinute = Integer.parseInt(closingTimeMinutes.getText());
        String cMeridiem = closingTimeMeridiem.getValue();

        // Formats the raw data to LocalTime and store in sValue
        LocalTime sValue = new Util().toLocalTimeFormat(sHour, sMinute, sMeridiem);
        LocalTime cValue = new Util().toLocalTimeFormat(cHour, cMinute, cMeridiem);

        // Pass the new formatted data to addToDataViewList method
        addToWHDataViewList(day, variation, sValue, cValue);

    }

    // Internal process of data that are added to viewList output in GUI
    private void addToWHDataViewList(String day, String variation, LocalTime sValue, LocalTime cValue) {

        // Will not add the data if the starting time is equal to closing time
        if (sValue.equals(cValue)) { return; }

        // Will not add the data if the closing time is before starting time
        if (sValue.isAfter(cValue)) { return; }

        // Simple calculation if the starting time or the closing time is out
        // of range of the day.
        // Example: If the day is SATURDAY and the closing time is 12:00 AM, the
        //          program will not add the data since 12:00 AM is part of SUNDAY
        Duration d = Duration.between(sValue, cValue);
        if (d.isNegative()) { return; }

        // Checks if day from the dataArray is already set, check else block for more information.
        if (startingTimeData.containsKey(day)) {

            // Checks for starting time and closing time that are colliding
            // Example: A 3:00 PM - 4:00 PM schedule is conflicting with 3:00 PM - 3:30 PM
            if (startingTimeData.get(day).contains(sValue) && closingTimeData.get(day).contains(cValue)) { return; }

            // Loops all the data to apply more checks
            for (int i = 0; i < startingTimeData.get(day).size(); i++) {
                LocalTime a = startingTimeData.get(day).get(i);
                LocalTime b = closingTimeData.get(day).get(i);

                // Will not add data if the there's an existing starting value.
                if (sValue.equals(a)) { return; }

                // Will not add data if the there's an existing closing value.
                if (cValue.equals(b)) { return; }

                // Will not add data if a time range is conflicting to existing data.
                if (sValue.isAfter(a) && sValue.isBefore(b)) { return; }
                if (cValue.isAfter(a) && cValue.isBefore(b)) { return; }
            }

            // The data will be added here if the data passed all the checks above.
            startingTimeData.get(day).add(sValue);
            closingTimeData.get(day).add(cValue);
            variationSchedData.get(day).add(variation);

        }
        // The program will create container if the day specified has not been set yet.
        else {
            ArrayList<LocalTime> a = new ArrayList<>();
            a.add(sValue);
            ArrayList<LocalTime> b = new ArrayList<>();
            b.add(cValue);
            ArrayList<String> c = new ArrayList<>();
            c.add(variation);
            startingTimeData.put(day, a);
            closingTimeData.put(day, b);
            variationSchedData.put(day, c);
        }

        // The items will now then be added to the listView in the GUI
        // Format: [day] variation: startingTime - closingTime
        WHDataViewer.getItems().add("[" + day + "] " + variation + ": " + new Util().toReadableTime(sValue) + " - " + new Util().toReadableTime(cValue));
    }

    // Remove button in the Working Hours GUI
    @FXML
    private void removeData(ActionEvent event) {
        fixDataForRemoval();
    }

    // Changes data to its proper data type before passing to removeDataFromList method.
    private void fixDataForRemoval() {

        // If the listView is empty and the remove data button was clicked, it will just ignore the action.
        if (WHDataViewer.getItems().isEmpty()) return;

        // Stores the selected data in selectedItems variable.
        List<String> selectedItems = new ArrayList<>(WHDataViewer.getSelectionModel().getSelectedItems());

        // Variable a is one of the value that is selected to be removed.
        // Format: [day] variationOfSched: startingTime - closingTime
        // Example: [Thursday] Class: 2:00 PM - 3:00 PM
        for (String a: selectedItems) {

            // We are using the splitter to get each required data for later. This is the only
            // viable option that I could think of when gathering data from the listView since there is no
            // direct method to grab data. We will be doing this technique for most of the loop.
            // NOTE TO SELF: Recode this to regex or substrings later.
            String[] splitter = a.split("] ");
            String d = splitter[0].replace("[", "");

            String[] splitter2 = splitter[1].split(": ");
            String vSched = splitter2[0];

            String[] splitter3 = splitter2[1].split(" - ");
            String[] splitter3a = splitter3[0].split(" ");
            String[] splitter3b = splitter3[1].split(" ");

            String sTMeridiem = splitter3a[1];
            String cTMeridiem = splitter3b[1];

            String[] splitter4a = splitter3a[0].split(":");
            String[] splitter4b = splitter3b[0].split(":");

            int sTHour = Integer.parseInt(splitter4a[0]);
            int sTMinute = Integer.parseInt(splitter4a[1]);
            int cTHour = Integer.parseInt(splitter4b[0]);
            int cTMinute = Integer.parseInt(splitter4b[1]);

            // At this point, our data are almost set. The code below will just translate the time-related data
            // type into LocalTime data type with the help of toLocalTimeFormat method from Util class.
            LocalTime sTime = new Util().toLocalTimeFormat(sTHour, sTMinute, sTMeridiem);
            LocalTime cTime = new Util().toLocalTimeFormat(cTHour, cTMinute, cTMeridiem);

            // Data are now polished. We will pass these to removeFromDataViewList method of the class to
            // get rid of the selected data in the listView.
            removeFromDataViewList(d, vSched, sTime, cTime);


        }
        // Finally, remove all the selected data from the listView since they are no longer
        // in our data variables
        WHDataViewer.getItems().removeAll(selectedItems);
    }

    // Internal process when removing data from list
    private void removeFromDataViewList(String day, String vSched, LocalTime sValue, LocalTime cValue) {
        for (int i = 0; i < startingTimeData.get(day).size(); i++) {
            if (variationSchedData.get(day).get(i).equalsIgnoreCase(vSched)) {
                if (startingTimeData.get(day).get(i).equals(sValue)) {
                    if (closingTimeData.get(day).get(i).equals(cValue)) {
                        startingTimeData.get(day).remove(i);
                        closingTimeData.get(day).remove(i);
                        variationSchedData.get(day).remove(i);
                    }
                }
            }
        }
    }

    // Used to change the window from Working Hours to General
    @FXML
    private void changeWindowGeneral(ActionEvent event) {
        AttendanceController controller = new Main().getAttendanceController();
        controller.updateDataList(variationSchedData, startingTimeData, closingTimeData);
        Main.changeWindow("General");
    }

    // Used to change the window from Working Hours to Attendance
    @FXML
    private void changeWindowAttendance(ActionEvent event) {
        AttendanceController controller = new Main().getAttendanceController();
        controller.updateDataList(variationSchedData, startingTimeData, closingTimeData);
        if (WHDataViewer.getItems().isEmpty()) {
            Main.changeWindow("AttendanceEmpty");
        } else {
            Main.changeWindow("Attendance");
        }
    }

    // Close button of the window
    @FXML
    private void programClose(MouseEvent event) { System.exit(0); }

    // This prepares the input stuff before showing the Working Hours GUI
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Sets the default day and the days of week list
        String day = new Util().getCurrentDayOfWeek();
        daysOfTheWeek.setValue(day);
        daysOfTheWeek.getItems().addAll("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

        // Sets the default schedule type and its list
        variationSched.setValue("Class");
        variationSched.getItems().addAll("Class", "Consultation", "Related Activities", "Others");

        // Sets the default time and its list
        startingTimeMeridiem.setValue("AM");
        startingTimeMeridiem.getItems().addAll("AM", "PM");
        closingTimeMeridiem.setValue("AM");
        closingTimeMeridiem.getItems().addAll("AM", "PM");
        startingTimeHours.setText("1");
        startingTimeMinutes.setText("00");
        closingTimeHours.setText("1");
        closingTimeMinutes.setText("00");

        // Allows the user to select multiple data from the data table.
        WHDataViewer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Allows the listView to be removed with double click
        WHDataViewer.setOnMouseClicked(click -> {
            if (!WHDataViewer.getItems().isEmpty()) {
                if (click.getClickCount() == 2) {
                    fixDataForRemoval();
                }
            }

        });

        // Adds Listener to the input textbox, so they can only input numbers with restricted range
        startingTimeHours.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                startingTimeHours.setText(oldValue);
            } else {
                if (newValue.length() == 1) return;
                try {
                    int nv = Integer.parseInt(newValue);
                    if (nv >= 12) {
                        startingTimeHours.setText("12");
                    } else if (nv == 11 || nv == 10) {
                        startingTimeHours.setText(String.valueOf(nv));
                    } else if (nv < 1) {
                        startingTimeHours.setText("1");
                    }
                } catch (NumberFormatException e) {}
            }
        });

        startingTimeMinutes.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                startingTimeMinutes.setText(oldValue);
            } else {
                if (newValue.length() == 1) return;
                try {
                    int nv = Integer.parseInt(newValue);
                    if (nv > 59) {
                        startingTimeMinutes.setText("00");
                    } else if (nv >= 10) {
                        startingTimeMinutes.setText(String.valueOf(nv));
                    } else if (nv < 1) {
                        startingTimeMinutes.setText("00");
                    }
                } catch (NumberFormatException e) {}
            }
        });

        closingTimeHours.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                closingTimeHours.setText(oldValue);
            } else {
                if (newValue.length() == 1) return;
                try {
                    int nv = Integer.parseInt(newValue);
                    if (nv >= 12) {
                        closingTimeHours.setText("12");
                    } else if (nv == 11 || nv == 10) {
                        closingTimeHours.setText(String.valueOf(nv));
                    } else if (nv < 1) {
                        closingTimeHours.setText("1");
                    }
                } catch (NumberFormatException e) {}
            }
        });

        closingTimeMinutes.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                closingTimeMinutes.setText(oldValue);
            } else {
                if (newValue.length() == 1) return;
                try {
                    int nv = Integer.parseInt(newValue);
                    if (nv > 59) {
                        closingTimeMinutes.setText("00");
                    } else if (nv >= 10) {
                        closingTimeMinutes.setText(String.valueOf(nv));
                    } else if (nv < 1) {
                        closingTimeMinutes.setText("00");
                    }
                } catch (NumberFormatException e) {}
            }
        });
    }
}
