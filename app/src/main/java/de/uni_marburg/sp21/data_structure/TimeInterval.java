package de.uni_marburg.sp21.data_structure;

import java.io.Serializable;
import java.sql.Time;

public class TimeInterval implements Serializable {
    static Time start;
    static Time end;
    static String Date;

    TimeInterval(Time start, Time end){
        TimeInterval.start = start;
        TimeInterval.end = end;
    }

    public static String getDate() {
        return Date;
    }

    public static void setDate(String date) {
        switch (date) {
            case "Sun":
                Date = "Sunday";
                break;
            case "Sat":
                Date = "Saturday";
            case "Mon":
                Date = "Monday";
            case "Tue":
                Date = "Tuesday";
            case "Wed":
                Date = "Wednesday";
            case "Thu":
                Date = "Thursday";
            case "Fri":
                Date = "Friday";

        }
    }

    public static Time getEnd() {
        return end;
    }

    public static Time getStart() {
        return start;
    }

    public static void setEnd(Time end) {
        TimeInterval.end = end;
    }

    public static void setStart(Time start) {
        TimeInterval.start = start;
    }
}
