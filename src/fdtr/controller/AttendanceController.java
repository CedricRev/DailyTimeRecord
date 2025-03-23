package fdtr.controller;

import fdtr.Main;
import fdtr.Util;
import fdtr.files.ExcelWriter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AttendanceController implements Initializable {

    // New data container for excel file
    // 1st Map: "year", "month"
    // 2nd Map: day, "data"
    private Map<Integer, ArrayList<String>> attendanceVariationSched = new HashMap<>();
    private Map<Integer, ArrayList<String>> attendanceStartingTime = new HashMap<>();
    private Map<Integer, ArrayList<String>> attendanceClosingTime = new HashMap<>();
    private Map<Integer, ArrayList<String>> attendanceStatus = new HashMap<>();

    @FXML
    private ChoiceBox<String> editBox;

    @FXML
    private Label editLabel;

    @FXML
    private Label placeholderMonthYear;

    @FXML
    private ListView<String> listViewData;


    public Map<Integer, ArrayList<String>> getAttendanceVariationSched() {
        return attendanceVariationSched;
    }

    public Map<Integer, ArrayList<String>> getAttendanceStartingTime() {
        return attendanceStartingTime;
    }

    public Map<Integer, ArrayList<String>> getAttendanceClosingTime() {
        return attendanceClosingTime;
    }

    public Map<Integer, ArrayList<String>> getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setPlaceholderMonthYear(String month, String year) {
        if (placeholderMonthYear.getText().contains(month)) {
            if (placeholderMonthYear.getText().contains(year)) {
                return;
            }
        }
        clearDataList();
        placeholderMonthYear.setText(month + " " + year);
    }

    public void clearDataList() {
        attendanceVariationSched.clear();
        attendanceStartingTime.clear();
        attendanceClosingTime.clear();
        attendanceStatus.clear();
        for (int i = 1; i <= 31; i++) {
            attendanceVariationSched.put(i, new ArrayList<>());
            attendanceStartingTime.put(i, new ArrayList<>());
            attendanceClosingTime.put(i, new ArrayList<>());
            attendanceStatus.put(i, new ArrayList<>());
        }
    }

    // This will update the attendance data, data used to fillup excel form
    public void updateDataList(Map<String, ArrayList<String>> variationSchedData, Map<String, ArrayList<LocalTime>> startingTimeData, Map<String, ArrayList<LocalTime>> closingTimeData) {

        // Check if the listView from Working Hours has data, else no update
        if (!variationSchedData.isEmpty()) {

            int selectedDataBefore = listViewData.getSelectionModel().getSelectedIndex();

            // Clears the table in the attendance listView, to update new values
            listViewData.getItems().clear();

            GeneralController controller = new Main().getGeneralController();

            // Prepares the util class, since we have a method here that returns the
            // day (Sunday, Monday, etc.) from a set date
            Util util = new Util();

            // Loops all the days in a month
            for (int i = 1; i <= 31; i++) {

                // Using the util#getDayOfTheDate method, it will help us to distinguish the day
                // from a given month and year
                // EXAMPLE: JUNE 6, 2021 -> SUNDAY
                String day = util.getDayOfTheDate(controller.getYear(), controller.getMonth(), i);

                // The weird thing about LocalDate when it comes to date that doesn't exist
                // (eg. June 31, 2021) returns the previous day of that date. (June 30, 2021 =
                // Wednesday and June 31, 2021 = Wednesday) So we used to this to check if
                // the date does exist, if not then break the loop.
                if (i == 31 || i == 29) {
                    if (day.equalsIgnoreCase(util.getDayOfTheDate(controller.getYear(), controller.getMonth(), i - 1))) {
                        break;
                    }
                }

                // Checks if the day is in Working Hours
                if (startingTimeData.containsKey(day)) {
                    ArrayList<String> copyStartingTimeData = new ArrayList<>();
                    for (int j = 0; j < startingTimeData.get(day).size(); j++) {
                        copyStartingTimeData.add(startingTimeData.get(day).get(j).format(DateTimeFormatter.ofPattern("h:mm a")));
                    }

                    if (!copyStartingTimeData.equals(attendanceStartingTime.get(i))) {
                        if (copyStartingTimeData.isEmpty()) {
                            clearDataList();
                        } else {
                            for (int j = 0; j < copyStartingTimeData.size(); j++) {
                                if (j > attendanceStartingTime.get(i).size() - 1) {
                                    attendanceVariationSched.get(i).add(variationSchedData.get(day).get(j));
                                    attendanceStartingTime.get(i).add(startingTimeData.get(day).get(j).format(DateTimeFormatter.ofPattern("h:mm a")));
                                    attendanceClosingTime.get(i).add(closingTimeData.get(day).get(j).format(DateTimeFormatter.ofPattern("h:mm a")));
                                    attendanceStatus.get(i).add("Present");
                                    continue;
                                }
                                if (!copyStartingTimeData.get(j).equalsIgnoreCase(attendanceStartingTime.get(i).get(j))) {
                                    attendanceVariationSched.get(i).set(j, variationSchedData.get(day).get(j));
                                    attendanceStartingTime.get(i).set(j, startingTimeData.get(day).get(j).format(DateTimeFormatter.ofPattern("h:mm a")));
                                    attendanceClosingTime.get(i).set(j, closingTimeData.get(day).get(j).format(DateTimeFormatter.ofPattern("h:mm a")));
                                    attendanceStatus.get(i).set(j, "Present");
                                }
                            }
                            if (attendanceStartingTime.get(i).size() > copyStartingTimeData.size()) {
                                int tempIndex = attendanceStartingTime.get(i).size() - copyStartingTimeData.size();

                                for (int j = 0; j < tempIndex; j++) {

                                    attendanceVariationSched.get(i).remove(attendanceVariationSched.get(i).size() - 1 - j);
                                    attendanceStartingTime.get(i).remove(attendanceStartingTime.get(i).size() - 1 - j);
                                    attendanceClosingTime.get(i).remove(attendanceClosingTime.get(i).size() - 1 - j);
                                    attendanceStatus.get(i).remove(attendanceStatus.get(i).size() - 1 - j);

                                }
                            }
                        }
                    }
                }
                for (int j = 0; j < attendanceStatus.get(i).size(); j++) {
                    listViewData.getItems().add("Day " + i + " (" + day + "): " + attendanceVariationSched.get(i).get(j) + " [" + attendanceStartingTime.get(i).get(j) + " - " + attendanceClosingTime.get(i).get(j) + "]: " + attendanceStatus.get(i).get(j));
                }
            }
            if (!listViewData.getItems().isEmpty()) {
                if (selectedDataBefore > listViewData.getItems().size()) {
                    listViewData.getSelectionModel().select(0);
                    selectItemForEdit();
                } else {
                    listViewData.getSelectionModel().select(selectedDataBefore);
                }

            }

        }
    }

    @FXML
    private void changeWindowWorkingHours(ActionEvent event) { Main.changeWindow("WorkingHours"); }

    @FXML
    private void changeWindowGeneral(ActionEvent event) { Main.changeWindow("General"); }

    @FXML
    private void programClose(MouseEvent event) { System.exit(0); }

    private void selectItemForEdit() {
        String item = listViewData.getSelectionModel().getSelectedItems().get(0);
        if (item.contains("Present")) {
            editBox.setValue("Present");
        } else if (item.contains("Absent")) {
            editBox.setValue("Absent");
        } else if (item.contains("Holiday")) {
            editBox.setValue("Holiday");
        } else if (item.contains("Academic Break")) {
            editBox.setValue("Academic Break");
        } else if (item.contains("Others")) {
            editBox.setValue("Others");
        }
        editLabel.setText(item.replaceAll(": " + editBox.getValue(), ""));
    }

    private void updateAttendanceStatus(String a) {
        String item = listViewData.getSelectionModel().getSelectedItems().get(0);
        for (int i = 1; i <= 31; i++) {
            if (item.contains("Day " + i + " ")) {
                for (int j = 0; j < attendanceVariationSched.get(i).size(); j++) {
                    if (item.contains("Day " + i + " ") && item.contains(attendanceVariationSched.get(i).get(j) + " [" + attendanceStartingTime.get(i).get(j) + " - " + attendanceClosingTime.get(i).get(j) + "]")) {
                        attendanceStatus.get(i).set(j, a);
                        WorkingHoursController controller = new Main().getWorkingHoursController();
                        updateDataList(controller.getVariationSchedData(), controller.getStartingTimeData(), controller.getClosingTimeData());
                    }
                }
            }
        }
    }

    @FXML
    private void generatePDF() throws Exception {
        new ExcelWriter().writeToExcel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GeneralController controller = new Main().getGeneralController();
        setPlaceholderMonthYear(controller.getMonth(), controller.getYear());
        editBox.setValue("Present");
        editBox.getItems().addAll("Present", "Absent", "Holiday", "Academic Break", "Others");
        editBox.setOnAction(click -> {
            if (!listViewData.getItems().isEmpty()) {
                updateAttendanceStatus(editBox.getValue());
            }
        });
        listViewData.setOnMouseClicked(click -> {
            if (!listViewData.getItems().isEmpty()) {
                if (click.getClickCount() == 1) {
                    selectItemForEdit();
                }
            }

        });
    }
}
