package de.uni_marburg.sp21;

import java.util.Date;

public class Message {
    private Date date;
    private String content;

    public Message(Date date, String content) {
        this.date = date;
        this.content = content;
    }
}