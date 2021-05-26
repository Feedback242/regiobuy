package de.uni_marburg.sp21.data_structure;


import java.io.Serializable;

public class Message implements Serializable {
    private String date;
    private String content;

    //TODO: Convert String to java date

    public Message(String date, String content) {
        this.date = date;
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
