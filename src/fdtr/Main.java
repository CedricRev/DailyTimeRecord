package fdtr;

import fdtr.controller.AttendanceController;
import fdtr.controller.AttendanceEmptyController;
import fdtr.controller.GeneralController;
import fdtr.controller.WorkingHoursController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.Scanner;

public class Main extends Application {


    // Window instance and its dimensions
    private static Stage window;

    private double xOffsetWindow;
    private double yOffsetWindow;

    // Scenes, useful for switching windows
    private static Scene sceneGeneral;
    private static Scene sceneWorkingHours;
    private static Scene sceneAttendance;
    private static Scene sceneAttendanceEmpty;

    // Stores the controller instance in the variable, so we can call it
    // whenever we need its methods.
    private static GeneralController generalController;
    private static WorkingHoursController workingHoursController;
    private static AttendanceController attendanceController;
    private static AttendanceEmptyController attendanceEmptyController;

    // --------------- METHODS ---------------

    // GETTERS of the controllers
    public GeneralController getGeneralController() { return generalController; }
    public WorkingHoursController getWorkingHoursController() { return workingHoursController; }
    public AttendanceController getAttendanceController() { return attendanceController; }
    public AttendanceEmptyController getAttendanceEmptyController() { return attendanceEmptyController; }

    @Override
    public void start(Stage primaryStage) throws Exception {

        // We store this on a variable to keep track of the instance of the program.
        // We can use this for switching pages.
        window = primaryStage;

        // Removes the default window border of the OS (Windows, Linux, etc.) that the user
        // is using. We are using this because our program is not responsive in terms of
        // scaling of font. If you know how to make the fonts scale to the size of the window,
        // please get rid of this and change everything in this program to make it responsive.
        window.initStyle(StageStyle.UNDECORATED);

        // Serves as the icon of the program, we see this logo usually in our taskbar. The logo
        // is in the same directory/package of the Main Class.
        window.getIcons().add(new Image(getClass().getResourceAsStream("/resources/images/msuiit-logo.jpg")));

        // Sets the programs name, we see this when hovering the logo in the taskbar.
        window.setTitle("MSU-IIT FDTR Generator");

        // Sets the date of the instance loaded
        new Util().setCurrentDate();

        // Loads all the FXML files needed for the program.
        for (int i = 1; i <= 4; i++) {
            FXMLLoader loader;
            Parent parent = null;
            switch (i) {
                case 1 -> {
                    loader = new FXMLLoader(getClass().getResource("/fdtr/fxml/General.fxml"));
                    parent = loader.load();
                    generalController = loader.getController();
                }
                case 2 -> {
                    loader = new FXMLLoader(getClass().getResource("/fdtr/fxml/WorkingHours.fxml"));
                    parent = loader.load();
                    workingHoursController = loader.getController();
                }
                case 3 -> {
                    loader = new FXMLLoader(getClass().getResource("/fdtr/fxml/Attendance.fxml"));
                    parent = loader.load();
                    attendanceController = loader.getController();
                }
                case 4 -> {
                    loader = new FXMLLoader(getClass().getResource("/fdtr/fxml/AttendanceEmpty.fxml"));
                    parent = loader.load();
                    attendanceEmptyController = loader.getController();
                }
                default -> System.out.println("[Log] An FXML file is not loaded");
            }

            // This makes the program window movable, for some reason the window is not
            // movable by default when using UNDECORATED StageStyle.
            parent.setOnMousePressed(event -> {
                xOffsetWindow = event.getSceneX();
                yOffsetWindow = event.getSceneY();
            });
            parent.setOnMouseDragged(event -> {
                window.setX(event.getScreenX() - xOffsetWindow);
                window.setY(event.getScreenY() - yOffsetWindow);
            });

            // Stores all scenes/pages to their respective variables for later.
            switch (i) {
                case 1 -> sceneGeneral = new Scene(parent, 600, 600);
                case 2 -> sceneWorkingHours = new Scene(parent, 600, 600);
                case 3 -> sceneAttendance = new Scene(parent, 600, 600);
                case 4 -> sceneAttendanceEmpty = new Scene(parent, 600, 600);
                default -> System.out.println("[Log] Scene specified is missing");
            }

        }

        // After everything is loaded, it will now boot the program showing the General page.
        window.setScene(sceneGeneral);
        window.show();

    }

    // Used to change pages of the window
    public static void changeWindow(String a) {
        if (a.equalsIgnoreCase("General")) {
            window.setScene(sceneGeneral);
            return;
        }
        if (a.equalsIgnoreCase("WorkingHours")) {
            window.setScene(sceneWorkingHours);
            return;
        }
        if (a.equalsIgnoreCase("Attendance")) {
            window.setScene(sceneAttendance);
            return;
        }
        if (a.equalsIgnoreCase("AttendanceEmpty")) {
            window.setScene(sceneAttendanceEmpty);
            return;
        }
        System.out.println("Window not found: " + window);
    }

    public static void main(String[] args) { launch(args);}
}
