package de.uni_marburg.sp21.company_data_structure;


import java.io.Serializable;

public class Message implements Serializable {
    private String date;
    private String content;

    /**
     * Constructor
     * @param date The Date when the Message has been written
     * @param content The Content (String) of the Message
     */
    public Message(String date, String content) {
        this.date = date;
        this.content = content;
    }

    //------------------ GET / SET -------------------


    public String getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}
