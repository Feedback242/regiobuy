package de.uni_marburg.sp21.filter;

import java.util.Date;

public class PickedTime {
    private String weekday;
    private Date startTime;
    private Date endTime;

    /**
     * Constructor
     */
    public PickedTime(){
        this.endTime = null;
        this.startTime = null;
        this.weekday = "";
    }

    /**
     * Resets the PickedTime
     */
    public void reset(){
        this.endTime = null;
        this.startTime = null;
        this.weekday = "";
    }
    //------------------ GET / SET -------------------


    /**
    * @param endTime The end time that has been chosen with the TimePicker (The Date is not relevant)
    */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @param startTime The start time that has been chosen with the TimePicker (The Date is not relevant)
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @param weekday The String Representation of a Weekday that has been Generated by the TimePicker
     */
    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getWeekday() {
        return weekday;
    }
}
