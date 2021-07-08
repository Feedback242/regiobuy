package de.uni_marburg.sp21.company_data_structure;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.uni_marburg.sp21.DataBaseManager;
import de.uni_marburg.sp21.MyApplication;
import de.uni_marburg.sp21.R;
import de.uni_marburg.sp21.TimeConverter;
import de.uni_marburg.sp21.glide.GlideApp;

public class Company implements Serializable {

    private final static String COMPANY_FILENAME = "Company.ser";

    private final String ID;
    private String name;
    private Address address;
    private Location location;
    private String description;
    private String mail;
    private String url;
    private List<ShopType> shopTypes;
    private String owner;
    private Map<String,ArrayList<Map<String, String>>> openingHours;
    private boolean deliveryService;
    private List<Organization> organizations;
    private String openingHoursComments;
    private List<Message> messages;
    private List<ProductGroup> productGroups;
    private String productsDescription;
    private String geoHash;
    private List<String> imagePaths;
    private boolean isFavorite;
    private boolean newMessage;

    /**
     * Constructor
     * @param name the Name of the Company
     * @param ID the ID of the Company
     * @param address the Address of the Company
     * @param geoHash the geoHash String of the Company
     */
    public Company(String name, final String ID, Address address, String geoHash){
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.geoHash = geoHash;

    }

    /**
     * Saves a Company Object to pass it to another Activity with load()
     * @param company the Company Object that has to be saved
     */
    public static void save(Company company){
        File path = MyApplication.getAppContext().getExternalFilesDir(null);
        File file = new File(path, COMPANY_FILENAME);

        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(company);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Loads a Company Object, that has been saved with save() before to pass it between Activities.
     * @return the Company Object that has been saved
     */
    public static Company load() {
        File path = MyApplication.getAppContext().getExternalFilesDir(null);
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

    /**
     * @return true if company is open
     */
    public boolean isOpen(){
        Calendar calendar = Calendar.getInstance();
        Date currentTime = calendar.getTime();
        String dayName = TimeConverter.currentWeekdayString();
        ArrayList<Map<String, String>> openingList = getOpeningHours().get(dayName);
        boolean isCurrentlyOpen = false;
        if(openingList != null){
            for(Map<String, String> m : openingList){
                Date start = TimeConverter.convertToDate(m.get("start"));
                Date end = TimeConverter.convertToDate(m.get("end"));

                isCurrentlyOpen = isCurrentlyOpen || (currentTime.after(start) && currentTime.before(end));
            }
        }
        return isCurrentlyOpen;
    }

    /**
     * Sets one of images of the Company to the passed imageView
     * @param imageView the imageView that should hold one image from the Company
     */
    public void setImageToImageView(ImageView imageView){
        if(!imagePaths.isEmpty()) {

            Random random = new Random();
            int size = imagePaths.size();
            int r = random.nextInt(size);

            DataBaseManager.setImageFromPath(imagePaths.get(r), imageView);

        }else {
            imageView.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
        }
    }

    //------------------ GET / SET -------------------


    public String getID() {
        return ID;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public List<String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(List<String> imagePaths) {
        this.imagePaths = imagePaths;
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

    public Map<String,ArrayList<Map<String, String>>> getOpeningHours() {
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
        List<ShopType> list = new ArrayList<>();
        for(String s : types){
            list.add(ShopType.fromDatabaseString(s));
        }
        this.shopTypes = list;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setOpeningHours(Map<String,ArrayList<Map<String, String>>> openingHours) {
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

    public Location getLocation() {
        return location;
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}
