package de.uni_marburg.sp21.data_structure;

import java.util.ArrayList;

public class Company {
    private final int ID;
    private String name;
    private Address address;
    private Location location;
    private String description;
    private String mail;
    private String url;
    private ShopTypes types;
    private String owner;
    private TimeInterval[] openingHours = new TimeInterval[7];
    private boolean deliveryService;
    private ArrayList<Organization> organizations;
    private String openingHoursComments;
    private ArrayList<Message> messages;
    private ArrayList<ProductGroup> productGroups;
    private String productsDescription;
    private String geoHash;

    public Company(String name, final int ID, Address address, String geoHash){
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.geoHash = geoHash;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Address getAddress() {
        return address;
    }

    public ShopTypes getTypes() {
        return types;
    }
}
