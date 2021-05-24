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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_marburg.sp21.data_structure.Address;
import de.uni_marburg.sp21.data_structure.Category;
import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.Location;
import de.uni_marburg.sp21.data_structure.Message;
import de.uni_marburg.sp21.data_structure.Organization;
import de.uni_marburg.sp21.data_structure.ProductGroup;
import de.uni_marburg.sp21.data_structure.Season;

public class DataBaseManager {

    private static final String COMPANIES_FILENAME = "RegioBuy.ser";

    /**
     * @param db the Firebase Database
     * @param context the Context of the Activity
     * @return returns a List of Companies parsed from the Firebase Database
     */
    public static List<Company> getCompanyList(FirebaseFirestore db, Context context) {
        List<Company> companies = new ArrayList<>();

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
                                }
                                //description
                                if(documentMap.get("description") != null){
                                    company.setDescription(documentMap.get("description").toString());
                                }
                                //mail
                                if(documentMap.get("mail") != null){
                                    company.setMail(documentMap.get("mail").toString());
                                }
                                //url
                                if(documentMap.get("url") != null){
                                    company.setUrl(documentMap.get("url").toString());
                                }
                                //owner
                                if(documentMap.get("owner") != null){
                                    company.setOwner(documentMap.get("owner").toString());
                                }
                                //deliveryService
                                if(documentMap.get("deliveryService") != null){
                                    company.setDeliveryService(Boolean.parseBoolean(documentMap.get("deliveryService").toString()));
                                }
                                //openingHoursComment
                                if(documentMap.get("openingHoursComments") != null){
                                    company.setOpeningHoursComments(documentMap.get("openingHoursComments").toString());
                                }
                                //productsDescription
                                if(documentMap.get("productsDescription") != null){
                                    company.setProductsDescription(documentMap.get("productsDescription").toString());
                                }
                                //geoHash
                                if(documentMap.get("geoHash") != null){
                                    company.setGeoHash(documentMap.get("geoHash").toString());
                                }
                                //productGroups
                                if(document.get("productGroups") != null) {
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
                                if(document.get("messages") != null) {
                                    List<Map> messagesList = (ArrayList<Map>) document.get("messages");
                                    List<Message> messages = new ArrayList<>();
                                    for (Map m : messagesList) {
                                        messages.add(new Message(m.get("date").toString(), m.get("content").toString()));
                                    }
                                    company.setMessages(messages);
                                }
                                //organisations
                                if(document.get("organizations") != null) {
                                    List<Map> organisationsList = (ArrayList<Map>) documentMap.get("organizations");
                                    List<Organization> organizations = new ArrayList<>();
                                    for (Map m : organisationsList) {
                                        organizations.add(new Organization((Double) m.get("id"), m.get("name").toString(), m.get("url").toString()));
                                    }
                                    company.setOrganizations(organizations);
                                }
                                //openingHours
                                if(document.get("openingHours") != null) {
                                    Map<String, ArrayList<String>> openingHoursMap = (Map<String, ArrayList<String>>) documentMap.get("openingHours");
                                    company.setOpeningHours(openingHoursMap);
                                }
                                //types
                                if(document.get("types") != null) {
                                    List<String> types = (ArrayList<String>) documentMap.get("types");
                                    company.setTypes(types);
                                }
                                //location
                                if(document.get("location") != null) {
                                    Map<String, Object> locationMap = (Map<String, Object>) documentMap.get("location");
                                    Location location = new Location((Double) locationMap.get("lat"), (Double) locationMap.get("lon"));
                                    company.setLocation(location);
                                }
                                //address
                                if(document.get("address") != null) {
                                    Map<String, Object> addressMap = (Map<String, Object>) documentMap.get("address");
                                    Address address = new Address(addressMap.get("city").toString(), addressMap.get("street").toString(), addressMap.get("zip").toString());
                                    company.setAddress(address);
                                }
                                temp.add(company);
                            }
                        }
                        save(temp, context);
                    }
                });
        companies = load(context);
        return companies;
    }

    private static void save(List<Company> companies, Context context){
        File path = context.getExternalFilesDir(null);
        File file = new File(path, COMPANIES_FILENAME);

        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(companies);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static List<Company> load(Context context) {
        File path = context.getExternalFilesDir(null);
        File file = new File(path, COMPANIES_FILENAME);
        List<Company> groceryLists = new ArrayList<>();
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            groceryLists = (List<Company>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return groceryLists;
    }
}
