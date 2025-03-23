package fdtr.files;

import com.aspose.cells.SaveFormat;
import com.aspose.cells.Workbook;
import fdtr.Main;
import fdtr.Util;
import fdtr.controller.AttendanceController;
import fdtr.controller.GeneralController;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class ExcelWriter implements AutoCloseable {

    private Workbook workbook;

    String year;
    String month;
    String user1;
    String user2;
    String department;
    String head;

    public void writeToExcel() throws Exception {
        workbook = new Workbook("src/resources/files/fdtr.xls");

        GeneralController general = new Main().getGeneralController();
        AttendanceController attendance = new Main().getAttendanceController();

        int rowExcel = 14;
        year = general.getYear();
        month = general.getMonth();
        user1 = "NAME: " + general.getNameOfUser();
        user2 = general.getNameOfUser();
        department = "DEPARTMENT: " + general.getDepartment();
        head = general.getNameOfHead();

        write("I7", month + " " + year);
        write("A9", user1);
        write("I9", department);
        write("F112", month + " " + year);
        write("B114", user2);
        write("J115", head);


        Map<Integer, ArrayList<String>> variationSched = attendance.getAttendanceVariationSched();
        Map<Integer, ArrayList<String>> startingTime = attendance.getAttendanceStartingTime();
        Map<Integer, ArrayList<String>> closingTime = attendance.getAttendanceClosingTime();
        Map<Integer, ArrayList<String>> attendanceStatus = attendance.getAttendanceStatus();

        int schedRow;

        for( int day = 1; day <= 31; day++ ) {
            write("A" + rowExcel, day);

            if (!variationSched.containsKey(day)) {
                String dayOfWeek = new Util().getDayOfTheDate(year, month, day);
                if (dayOfWeek.equalsIgnoreCase("SUNDAY") || dayOfWeek.equalsIgnoreCase("SATURDAY")) {
                    write("B" + rowExcel, dayOfWeek.toUpperCase());
                } else {
                    write("B" + rowExcel, "HOLIDAY");
                }
            }


            // Class
            schedRow = rowExcel;
            for(int i = 0; i < variationSched.get(day).size(); i++) {
                if (variationSched.get(day).get(i).equalsIgnoreCase("CLASS")) {

                    // Starting Time
                    if (attendanceStatus.get(day).get(i).equalsIgnoreCase("ABSENT")) {
                        write("B" + schedRow, "ABSENT");
                    } else {
                        write("B" + schedRow, startingTime.get(day).get(i));
                    }

                    // Closing Time
                    if (!attendanceStatus.get(day).get(i).equalsIgnoreCase("ABSENT")) {
                        write("C" + schedRow, closingTime.get(day).get(i));
                    }

                    // Difference
                    if (!attendanceStatus.get(day).get(i).equalsIgnoreCase("ABSENT")) {
                        LocalTime first = LocalTime.parse(startingTime.get(day).get(i), DateTimeFormatter.ofPattern("h:mm a"));
                        LocalTime second = LocalTime.parse(closingTime.get(day).get(i), DateTimeFormatter.ofPattern("h:mm a"));
                        String diff = String.valueOf(Duration.between(first, second));
                        write("D" + schedRow, diff);
                    }
                    schedRow++;
                }
            }


            rowExcel = rowExcel + 3;
        }



        // Write the Excel file

        workbook.save("src/resources/files/fdtr.pdf", SaveFormat.PDF);
        new PreviewPDF().generatePdf();
    }


    private void write( final String cellPos, final String message ) {
        workbook.getWorksheets().get(0).getCells().get(cellPos).setValue(message);
    }

    private void write( final String cellPos, final int message ) {
        workbook.getWorksheets().get(0).getCells().get(cellPos).setValue(message);
    }

    private void write( final String cellPos, final double message ) {
        workbook.getWorksheets().get(0).getCells().get(cellPos).setValue(message);
    }

    @Override
    public void close() throws Exception {

    }
}
