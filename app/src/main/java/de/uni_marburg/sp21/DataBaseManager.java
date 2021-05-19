package de.uni_marburg.sp21;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.uni_marburg.sp21.data_structure.Address;
import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.Location;
import de.uni_marburg.sp21.data_structure.Message;
import de.uni_marburg.sp21.data_structure.Organization;
import de.uni_marburg.sp21.data_structure.ProductGroup;
import de.uni_marburg.sp21.data_structure.ShopTypes;

public class DataBaseManager {

    public static List<Company> getCompanyList(FirebaseFirestore db) {
        List<Company> companies = new ArrayList<>();

        db.collection("companies")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(MainActivity.TAG, document.getData().toString());

                                Company company = new Company((String) document.getId());

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


                                //TODO implement types
                                if(document.get("types") != null){
                                    ArrayList<String> list = (ArrayList<String>) document.get("types");
                                    company.setTypes(new ShopTypes(list));
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

                                companies.add(company);
                                System.out.println("test");

                            }
                            System.out.println("test");
                        } else {
                            Log.w(MainActivity.TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        System.out.println("test");
        return companies;

    }
}
