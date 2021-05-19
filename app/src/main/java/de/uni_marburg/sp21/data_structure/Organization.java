package de.uni_marburg.sp21.data_structure;

public class Organization {
    private final Double ID;
    private String name;
    private String url;

    public Organization(final Double ID, String name, String url) {
        this.ID = ID;
        this.name = name;
        this.url = url;
    }


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
