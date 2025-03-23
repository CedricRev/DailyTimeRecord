package fdtr.controller;

import fdtr.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class AttendanceEmptyController implements Initializable {
    @FXML
    private Label placeholderMonthYear;

    public void setPlaceholderMonthYear(String month, String year) {
        placeholderMonthYear.setText(month + ", " + year);
    }

    @FXML
    private void changeWindowWorkingHours(ActionEvent event) { Main.changeWindow("WorkingHours"); }

    @FXML
    private void changeWindowGeneral(ActionEvent event) { Main.changeWindow("General"); }

    @FXML
    private void programClose(MouseEvent event) { System.exit(0); }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        GeneralController controller = new Main().getGeneralController();
        setPlaceholderMonthYear(controller.getMonth(), controller.getYear());
    }
}
