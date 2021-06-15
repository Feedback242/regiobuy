package de.uni_marburg.sp21;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import de.uni_marburg.sp21.company_data_structure.Address;
import de.uni_marburg.sp21.company_data_structure.Category;
import de.uni_marburg.sp21.company_data_structure.Company;
import de.uni_marburg.sp21.company_data_structure.Location;
import de.uni_marburg.sp21.company_data_structure.Message;
import de.uni_marburg.sp21.company_data_structure.Organization;
import de.uni_marburg.sp21.company_data_structure.ProductGroup;
import de.uni_marburg.sp21.company_data_structure.Season;

public class DataBaseManager {

    private static final String COMPANIES_FILENAME = "RegioBuy.ser";

    /**
     * @param db the Firebase Database
     * @return returns a List of Companies parsed from the Firebase Database
     */
    public static List<Company> getCompanyList(FirebaseFirestore db) {
        long start = System.currentTimeMillis();
        Log.d(MyApplication.APP_TAG, "Started downloading and assigning companies from database.");
        List<Company> companies;

        db.collection("companies")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        List<Company> temp = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Map<String, Object> documentMap = document.getData();
                                Company company = new Company(documentMap.get("id").toString());

                                //name
                                if(documentMap.get("name") != null){
                                    company.setName(documentMap.get("name").toString());
                                } else {
                                    company.setName("");
                                }
                                //description
                                if(documentMap.get("description") != null){
                                    company.setDescription(documentMap.get("description").toString());
                                } else {
                                    company.setDescription("");
                                }
                                //mail
                                if(documentMap.get("mail") != null){
                                    company.setMail(documentMap.get("mail").toString());
                                } else {
                                    company.setMail("");
                                }
                                //url
                                if(documentMap.get("url") != null){
                                    company.setUrl(documentMap.get("url").toString());
                                } else {
                                    company.setUrl("");
                                }
                                //owner
                                if(documentMap.get("owner") != null){
                                    company.setOwner(documentMap.get("owner").toString());
                                } else {
                                    company.setOwner("");
                                }
                                //deliveryService
                                if(documentMap.get("deliveryService") != null){
                                    company.setDeliveryService(Boolean.parseBoolean(documentMap.get("deliveryService").toString()));
                                } else {
                                    company.setDeliveryService(false);
                                }
                                //openingHoursComment
                                if(documentMap.get("openingHoursComments") != null){
                                    company.setOpeningHoursComments(documentMap.get("openingHoursComments").toString());
                                } else {
                                    company.setOpeningHoursComments("");
                                }
                                //productsDescription
                                if(documentMap.get("productsDescription") != null){
                                    company.setProductsDescription(documentMap.get("productsDescription").toString());
                                } else {
                                    company.setProductsDescription("");
                                }
                                //geoHash
                                if(documentMap.get("geoHash") != null){
                                    company.setGeoHash(documentMap.get("geoHash").toString());
                                } else {
                                    company.setGeoHash("");
                                }
                                //productGroups
                                if(documentMap.get("productGroups") != null) {
                                    List<Map> productGroupList = (ArrayList<Map>) document.get("productGroups");
                                    List<ProductGroup> productGroups = new ArrayList<>();
                                    for (Map m : productGroupList) {
                                        ProductGroup productGroup = new ProductGroup(Category.fromDatabaseString(m.get("category").toString()), (Double) m.get("producer"));
                                        productGroup.setRawProd((Boolean) m.get("rawProduct"));
                                        List<String> seasonsStrings = (ArrayList<String>) m.get("seasons");
                                        List<Season> seasons = new ArrayList<>();
                                        for (String s : seasonsStrings) {
                                            seasons.add(Season.fromDatabaseString(s));
                                        }
                                        productGroup.setSeasons(seasons);
                                        productGroup.setProductTags((ArrayList<String>) m.get("productTags"));
                                        productGroups.add(productGroup);
                                    }
                                    company.setProductGroups(productGroups);
                                }
                                //messages
                                if(documentMap.get("messages") != null) {
                                    List<Map> messagesList = (ArrayList<Map>) document.get("messages");
                                    List<Message> messages = new ArrayList<>();
                                    for (Map m : messagesList) {
                                        messages.add(new Message(m.get("date").toString(), m.get("content").toString()));
                                    }
                                    company.setMessages(messages);
                                }
                                //organisations
                                if(documentMap.get("organizations") != null) {
                                    List<Map> organisationsList = (ArrayList<Map>) documentMap.get("organizations");
                                    List<Organization> organizations = new ArrayList<>();
                                    for (Map m : organisationsList) {
                                        organizations.add(new Organization((Double) m.get("id"), m.get("name").toString(), m.get("url").toString()));
                                    }
                                    company.setOrganizations(organizations);
                                }
                                //openingHours
                                if(documentMap.get("openingHours") != null) {
                                    Map<String,ArrayList<Map<String, String>>> openingHoursMap = (Map<String,ArrayList<Map<String, String>>>) documentMap.get("openingHours");
                                    company.setOpeningHours(openingHoursMap);
                                }
                                //types
                                if(documentMap.get("types") != null) {
                                    List<String> types = (ArrayList<String>) documentMap.get("types");
                                    company.setTypes(types);
                                }
                                //location
                                if(documentMap.get("location") != null) {
                                    Map<String, Object> locationMap = (Map<String, Object>) documentMap.get("location");
                                    Location location = new Location((Double) locationMap.get("lat"), (Double) locationMap.get("lon"));
                                    company.setLocation(location);
                                }
                                //address
                                if(documentMap.get("address") != null) {
                                    Map<String, Object> addressMap = (Map<String, Object>) documentMap.get("address");
                                    Address address = new Address(addressMap.get("city").toString(), addressMap.get("street").toString(), addressMap.get("zip").toString());
                                    company.setAddress(address);
                                }
                                //imagePaths
                                if(documentMap.get("imagePaths") != null){
                                    company.setImagePaths((List<String>) documentMap.get("imagePaths"));
                                }
                                temp.add(company);
                            }
                        }
                        save(temp);
                    }
                });
        companies = load();
        long end = System.currentTimeMillis();
        Log.d(MyApplication.APP_TAG, "Finished downloading and assigning companies from database. It took " + (float)(end-start)/1000f + " seconds");
        return companies;
    }

    public static void save(List<Company> companies){
        File path = MyApplication.getAppContext().getExternalFilesDir(null);
        File file = new File(path, COMPANIES_FILENAME);

        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(companies);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static List<Company> load() {
        File path = MyApplication.getAppContext().getExternalFilesDir(null);
        File file = new File(path, COMPANIES_FILENAME);
        List<Company> companies = new ArrayList<>();
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            companies = (List<Company>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return companies;
    }
}
