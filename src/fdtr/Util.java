package fdtr;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class Util {

    // Stores the month and year of user's date
    private static String currentDayOfWeek;
    private static String currentMonth;
    private static String currentYear;

    // --------------- METHODS ---------------

    // GETTERS of the variables above
    public String getCurrentDayOfWeek() {
        return currentDayOfWeek;
    }
    public String getCurrentMonth() {
        return currentMonth;
    }
    public String getCurrentYear() {
        return currentYear;
    }

    // This will set the current month and year of the user.
    // We made a dedicated method because we want this only to be
    // called once.
    public void setCurrentDate() {
        String b = LocalDate.now().getDayOfWeek().name();
        currentDayOfWeek = b.charAt(0) + b.toLowerCase().substring(1);
        String a = LocalDate.now().getMonth().name();
        currentMonth = a.charAt(0) + a.toLowerCase().substring(1);
        currentYear = String.valueOf(LocalDate.now().getYear());
    }

    // Formatter for input time into a proper LocalTime data type
    public LocalTime toLocalTimeFormat(int hour, int minute, String meridiem) {
        if (hour == 12) {
            if (meridiem.equalsIgnoreCase("AM")) {
                hour = 0;
            }
        } else {
            if (meridiem.equalsIgnoreCase("PM")) {
                hour = hour + 12;
            }
        }
        return LocalTime.of(hour, minute);
    }

    // Formatter for input time into a proper readable format (12:00 am, 1:30 pm)
    public String toReadableTime(LocalTime time) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("h:mm a");
        return time.format(format);
    }

    // Checks the day of the date being input
    // Example: June 5, 2021, returns "Saturday"
    public String getDayOfTheDate(String year, String month, int day) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("d-MMMM-yyyy");
        LocalDate date = LocalDate.parse(day + "-" + month + "-" + year, format);
        String a = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return a;
    }
}
