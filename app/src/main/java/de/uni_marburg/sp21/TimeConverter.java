package de.uni_marburg.sp21;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeConverter {
    public static final String[] WEEKDAYS = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

    /**
     * This Method returns the Database representation of the Weekday-Names, not the Translation.
     * @return the current Weekday-Name that matches the Weekday-Names in the Database
     */
    public static String currentWeekdayString(){
        Calendar calendar = Calendar.getInstance();
        String weekday = "";
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day){
            case Calendar.MONDAY: weekday = WEEKDAYS[0];
                break;
            case Calendar.TUESDAY: weekday = WEEKDAYS[1];
                break;
            case Calendar.WEDNESDAY: weekday = WEEKDAYS[2];
                break;
            case Calendar.THURSDAY: weekday = WEEKDAYS[3];
                break;
            case Calendar.FRIDAY: weekday = WEEKDAYS[4];
                break;
            case Calendar.SATURDAY: weekday = WEEKDAYS[5];
                break;
            case Calendar.SUNDAY: weekday = WEEKDAYS[6];
                break;
        }
        return weekday;
    }

    /**
     * The Database is holding other Weekday-Names than the Calendar Class, they are dependent from the language too.
     * The Method converts this Calendar Weekday-Name to the Weekday-Name, that matches the Database
     * @param calendarWeekday the String you got from the Calendar when you call get(Calendar.DAY_OF_WEEK) on your calendar Object
     * @return The String that matches the Weekday-Name in the Database
     * @throws ParseException
     */
    public static String convertToDatabaseWeekday(String calendarWeekday) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEE");
        Date date = format.parse(calendarWeekday);
        calendar.setTime(date);
        String weekday = "";
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day){
            case Calendar.MONDAY: weekday = WEEKDAYS[0];
                break;
            case Calendar.TUESDAY: weekday = WEEKDAYS[1];
                break;
            case Calendar.WEDNESDAY: weekday = WEEKDAYS[2];
                break;
            case Calendar.THURSDAY: weekday = WEEKDAYS[3];
                break;
            case Calendar.FRIDAY: weekday = WEEKDAYS[4];
                break;
            case Calendar.SATURDAY: weekday = WEEKDAYS[5];
                break;
            case Calendar.SUNDAY: weekday = WEEKDAYS[6];
                break;
        }
        return weekday;
    }

    /**
     * Converts a String with the Pattern "HH:mm" to the current Date but with that time
     * @param time The Time String, that has to match the Regex "HH:mm"
     * @return The current Date with the time from the string or null if the string doesn't matches the pattern
     */
    public static Date convertToDate(String time){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        try {
            Date setTime = format.parse(time);
            calendar.setTime(setTime);
            calendar.set(date.getYear() + 1900, date.getMonth(), date.getDate());
            Date result = calendar.getTime();
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
