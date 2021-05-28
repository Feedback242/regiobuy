package de.uni_marburg.sp21.data_structure;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Company implements Serializable {

    private final static String COMPANY_FILENAME = "Company.ser";
    private final String ID;
    private String name;
    private Address address;
    private Location location;
    private String description;
    private String mail;
    private String url;
    private ArrayList<ShopType> shopTypes;
    private String owner;
    private Map<String,Map<String,ArrayList<Map<String, String>>>> openingHours;
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

    public static void save(Company company, Context context){
        File path = context.getExternalFilesDir(null);
        File file = new File(path, COMPANY_FILENAME);

        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(company);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Company load(Context context) {
        File path = context.getExternalFilesDir(null);
        File file = new File(path, COMPANY_FILENAME);
        Company company = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            company = (Company) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return company;
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

    public String getOwner() {
        return owner;
    }

    public String getProductsDescription() {
        return productsDescription;
    }

    public List<ProductGroup> getProductGroups() {
        return productGroups;
    }

    public String getOpeningHoursComments() {
        return openingHoursComments;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Map<String,Map<String,ArrayList<Map<String, String>>>> getOpeningHours() {
        return openingHours;
    }

    public boolean isDeliveryService() {
        return deliveryService;
    }

    public String getMail() {
        return mail;
    }

    public String getUrl() {
        return url;
    }

    public int getImageResource(){
        List<ShopType> shopTypes = getTypes();
        ShopType randomTypeFromList = shopTypes.get(new Random().nextInt(shopTypes.size()));
        return randomTypeFromList.toDrawableID();
    }

    public boolean isOpened(){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        long timeInMillis = calendar.getTimeInMillis();
        switch (day){
            case Calendar.MONDAY :
            case Calendar.TUESDAY :
            case Calendar.WEDNESDAY :
            case Calendar.THURSDAY :
            case Calendar.FRIDAY :
            case Calendar.SATURDAY :
            case Calendar.SUNDAY :
        }
        //TODO
        return false;
    }

    private boolean isOpenedAt(long timeInMillis){
        //TODO
        return false;
    }

    public boolean isOpened(Date date){
        //TODO
        return false;
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

    public void setOpeningHours(Map<String, Map<String, ArrayList<Map<String, String>>>> openingHours) {
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
