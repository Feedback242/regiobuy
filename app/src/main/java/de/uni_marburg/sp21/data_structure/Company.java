package de.uni_marburg.sp21.data_structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Company implements Serializable {
    private final String ID;
    private String name;
    private Address address;
    private Location location;
    private String description;
    private String mail;
    private String url;
    private ArrayList<ShopType> shopTypes;
    private String owner;
//  private TimeInterval[] openingHours = new TimeInterval[7];
    private Map<String, ArrayList<String>> openingHours;
    private boolean deliveryService;
    private List<Organization> organizations;
    private String openingHoursComments;
    private List<Message> messages;
    private List<ProductGroup> productGroups;
    private String productsDescription;
    private String geoHash;

    public Company(String name, final String ID, Address address, String geoHash){
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.geoHash = geoHash;
    }



    public Company(String ID) {
        this.ID = ID;
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

    public List<ShopType> getTypes() {
        return shopTypes;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTypes(List<String> types) {
        ArrayList<ShopType> list = new ArrayList<>();
        for(String s : types){
            list.add(ShopType.fromDatabaseString(s));
        }
        this.shopTypes = list;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setOpeningHours(Map openingHours) {
        this.openingHours = openingHours;
    }

    public void setDeliveryService(boolean deliveryService) {
        this.deliveryService = deliveryService;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    public void setOpeningHoursComments(String openingHoursComments) {
        this.openingHoursComments = openingHoursComments;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setProductGroups(List<ProductGroup> productGroups) {
        this.productGroups = productGroups;
    }

    public void setProductsDescription(String productsDescription) {
        this.productsDescription = productsDescription;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }
}
