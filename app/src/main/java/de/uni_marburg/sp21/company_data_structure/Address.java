package de.uni_marburg.sp21.company_data_structure;

import java.io.Serializable;

public class Address implements Serializable {
    private String city;
    private String street;
    private String zip;

    /**
     * Constructor
     * @param city The City of the Address
     * @param street The Street of the Address
     * @param zip The zip-Code of the Address
     */
    public Address(String city, String street, String zip) {
        this.city = city;
        this.street = street;
        this.zip = zip;
    }

    //------------------ GET / SET -------------------
    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getZip() {
        return zip;
    }
}
