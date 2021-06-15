package de.uni_marburg.sp21.company_data_structure;

import java.io.Serializable;

public class Organization implements Serializable {
    private final Double ID;
    private String name;
    private String url;

    /**
     * Constructor
     * @param ID The ID of the Organisation
     * @param name The Name of the Organisation
     * @param url The URL (Website) of the Organisation
     */
    public Organization(final Double ID, String name, String url) {
        this.ID = ID;
        this.name = name;
        this.url = url;
    }

    //------------------ GET / SET -------------------

    public String getName() {
        return name;
    }

    public double getID() {
        return ID;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
