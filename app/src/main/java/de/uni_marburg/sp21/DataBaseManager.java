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

import de.uni_marburg.sp21.data_structure.Address;
import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.Location;
import de.uni_marburg.sp21.data_structure.Message;
import de.uni_marburg.sp21.data_structure.Organization;
import de.uni_marburg.sp21.data_structure.ProductGroup;

public class DataBaseManager {

    private static final String COMPANIES_FILENAME = "RegioBuy.ser";

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
                                Log.d(MainActivity.TAG, document.getData().toString());

                                Company company = new Company((String) document.getId());

                                if(document.get("name") != null){
                                    company.setName((String)document.get("name"));
                                }

                                if(document.get("address") != null){
                                    HashMap map = (HashMap<String, String>)document.get("address");
                                    Address address = new Address((String)map.get("city"), (String)map.get("street"), (String)map.get("zip"));
                                    company.setAddress(address);
                                }

                                if(document.get("location") != null){
                                    HashMap map = (HashMap<String, Double>)document.get("location");
                                    Location location = new Location((Double)map.get("lon"), (Double)map.get("lat"));
                                    company.setLocation(location);
                                }

                                if(document.get("description") != null){
                                    company.setDescription((String)document.get("description"));
                                }

                                if(document.get("mail") != null){
                                    company.setMail((String)document.get("mail"));
                                }

                                if(document.get("url") != null){
                                    company.setUrl((String)document.get("url"));
                                }



                                if(document.get("types") != null){
                                    company.setTypes((ArrayList<String>) document.get("types"));
                                }

                                if(document.get("owner") != null){
                                    company.setOwner((String)document.get("owner"));
                                }

                                if(document.get("openingHours") != null){
                                    company.setOpeningHours((HashMap<String, ArrayList<String>>)document.get("openingHours"));
                                }

                                if(document.get("deliveryService") != null){
                                    company.setDeliveryService((Boolean)document.get("deliveryService"));
                                }

                                if(document.get("organizations") != null){
                                    ArrayList arrayList = (ArrayList<HashMap>)document.get("organizations");
                                    ArrayList orgs = new ArrayList<Organization>();
                                    if(!arrayList.isEmpty()){
                                        for(int i = 0; i < arrayList.size(); i++){
                                            HashMap hashMap = (HashMap) arrayList.get(i);
                                            orgs.add(new Organization((Double)hashMap.get("id"), (String)hashMap.get("name"), (String)hashMap.get("url")));
                                        }
                                    }
                                    company.setOrganizations(orgs);
                                }

                                if(document.get("openingHoursComments") != null){
                                    company.setOpeningHoursComments((String)document.get("openingHoursComments"));
                                }

                                if(document.get("messages") != null){
                                    ArrayList arrayList = (ArrayList)document.get("messages");
                                    ArrayList messages = new ArrayList<Message>();
                                    if (!arrayList.isEmpty()){
                                        for(int i = 0; i < arrayList.size(); i++){
                                            HashMap hashMap = (HashMap)arrayList.get(i);
                                            messages.add(new Message((String)hashMap.get("date"), (String)hashMap.get("content")));
                                        }
                                    }
                                }

                                if(document.get("productGroups") != null){
                                    ArrayList arrayList = (ArrayList)document.get("productGroups");
                                    if(!arrayList.isEmpty()){
                                        for(int i = 0; i < arrayList.size(); i++){
                                            HashMap hashMap = (HashMap)arrayList.get(i);
                                            ProductGroup productGroup = new ProductGroup((String)hashMap.get("category"), (Double)hashMap.get("producer"));
                                            if(hashMap.containsKey("rawProduct")){
                                                productGroup.setRawProd((Boolean)hashMap.get("rawProduct"));
                                            }
                                            if(hashMap.containsKey("seasons")){
                                                productGroup.setSeasons((ArrayList<String>)hashMap.get("seasons"));
                                            }
                                            if(hashMap.containsKey("productTags")){
                                                productGroup.setSeasons((ArrayList<String>)hashMap.get("productTags"));
                                            }
                                        }
                                    }
                                }

                                if(document.get("productsDescription") != null){
                                    company.setProductsDescription((String)document.get("productsDescription"));
                                }

                                if(document.get("geoHash") != null){
                                    company.setGeoHash((String)document.get("geoHash"));
                                    System.out.println("test");
                                }

                                temp.add(company);
                                System.out.println("test");

                            }
                            System.out.println("test");
                            save(temp, context);
                        } else {
                            Log.w(MainActivity.TAG, "Error getting documents.", task.getException());
                        }
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
