package de.uni_marburg.sp21.data_structure;

public class Address {
    private String city;
    private String street;
    private String zip;

    public Address(String city, String street, String zip) {
        this.city = city;
        this.street = street;
        this.zip = zip;
    }

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
