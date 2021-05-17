package de.uni_marburg.sp21.data_structure;

public class Organization {
    private final int ID;
    private String name;
    private String url;

    public Organization(final int ID, String name) {
        this.ID = ID;
        this.name = name;
        this.url = "";
    }

    public String getName() {
        return name;
    }

    public int getID() {
        return ID;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
