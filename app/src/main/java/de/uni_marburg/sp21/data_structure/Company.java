package de.uni_marburg.sp21.data_structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

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
//    private TimeInterval[] openingHours = new TimeInterval[7];
    private HashMap openingHours;
    private boolean deliveryService;
    private ArrayList<Organization> organizations;
    private String openingHoursComments;
    private ArrayList<Message> messages;
    private ArrayList<ProductGroup> productGroups;
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

    public ArrayList<ShopType> getTypes() {
        return shopTypes;
    }

    public ArrayList<Organization> getOrganizations() {
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

    public void setTypes(ArrayList<String> types) {
        ArrayList<ShopType> list = new ArrayList<>();
        for(int i = 0; i < types.size(); i++){
            switch (types.get(i)) {
                case "producer":
                    list.add(ShopType.PRODUCER);
                    break;
                case "shop":
                    list.add(ShopType.SHOP);
                    break;
                case "restaurant":
                    list.add(ShopType.RESTAURANT);
                    break;
                case "hotel":
                    list.add(ShopType.HOTEL);
                    break;
                case "mart":
                    list.add(ShopType.MART);
                    break;
            }
            }
        this.shopTypes = list;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setOpeningHours(HashMap openingHours) {
        this.openingHours = openingHours;
    }

    public void setDeliveryService(boolean deliveryService) {
        this.deliveryService = deliveryService;
    }

    public void setOrganizations(ArrayList<Organization> organizations) {
        this.organizations = organizations;
    }

    public void setOpeningHoursComments(String openingHoursComments) {
        this.openingHoursComments = openingHoursComments;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public void setProductGroups(ArrayList<ProductGroup> productGroups) {
        this.productGroups = productGroups;
    }

    public void setProductsDescription(String productsDescription) {
        this.productsDescription = productsDescription;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }
}
