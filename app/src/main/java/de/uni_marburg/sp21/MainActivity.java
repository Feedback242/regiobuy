package de.uni_marburg.sp21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;

import de.uni_marburg.sp21.data_structure.Category;
import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.Message;
import de.uni_marburg.sp21.data_structure.Organization;
import de.uni_marburg.sp21.data_structure.ProductGroup;
import de.uni_marburg.sp21.data_structure.Organization;
import de.uni_marburg.sp21.data_structure.ShopType;
import de.uni_marburg.sp21.filter.BottomSheetFilter;
import de.uni_marburg.sp21.filter.CheckItem;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "REGIO";
    private FirebaseFirestore database;

    public List<Company> companies;
    public List<Company> filteredCompanies;
    public HashSet<Company> filterCompaniesSet;

    private CompanyAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ImageView filterButton;
    private SearchView searchView;

    private CheckItem[] categories;
    private CheckItem[] organisations;
    private CheckItem[] restrictions;
    private CheckItem[] types;
    private boolean isOpen;
    private boolean isDelivery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);

        database = FirebaseFirestore.getInstance();
        companies = DataBaseManager.getCompanyList(database, MainActivity.this);
        filteredCompanies = new ArrayList<>(companies);
        filterCompaniesSet = new HashSet<>(companies);

        filterButton = findViewById(R.id.filterButton);
        buildRecyclerView();
        buildFilter();

        categories = Category.createCheckItemArray();
        types = ShopType.createCheckItemArray();
        organisations = getOrganisations();
        restrictions = new CheckItem[]{new CheckItem("Name Betrieb"), new CheckItem("Name Besitzer"), new CheckItem("Arten Betrieb"),
                new CheckItem("Adresse"), new CheckItem("Beschreibung Betrieb"), new CheckItem("Beschreibung Produkte"),
                new CheckItem("Schlagworte Produktgruppen"), new CheckItem("Öffnungseit Anmerkung"), new CheckItem("Name Organisation"),
                new CheckItem("Nachrichten des Betriebes")};

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!(newText == null || newText.length()== 0 )){
                    Log.d(TAG, "-");
                    filterCompaniesSet.clear();
                    filter(newText.toLowerCase());
                    adapter.notifyDataSetChanged();
                }
                return false;
            }

        });

    }

    private CheckItem[] getOrganisations(){
        HashSet<String> set = new HashSet<>();
        for(Company c : companies){
            List<Organization> orgs = c.getOrganizations();
            for(Organization o : orgs){
                set.add(o.getName());
            }
        }
        List<CheckItem> temp = new ArrayList<>();
        for (String s : set){
            temp.add(new CheckItem(s));
        }
        return temp.toArray(new CheckItem[temp.size()]);
    }

    private void filter(String s) {
        for (Company c : companies) {
            boolean isDefault = true;
            for (CheckItem r : restrictions) {
                if(r.isChecked()){
                    isDefault = false;
                }
            }
            //default search (only company name and city)
            if(isDefault){
                if (c.getName().toLowerCase().contains(s)) {
                    filterCompaniesSet.add(c);
                    Log.d(TAG, "company name: " + c.getName());
                }
                if (c.getAddress().getCity().toLowerCase().contains(s)) {
                    filterCompaniesSet.add(c);
                }
            } else {
                for (CheckItem r : restrictions) {
                    if (r.isChecked()) {
                        //company name
                        if (restrictions[0].getText().equals(r.getText())) {
                            if (c.getName().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                                Log.d(TAG, "company name: " + c.getName());
                            }
                        }
                        //name owner
                        else if (restrictions[1].getText().equals(r.getText())) {
                            if (c.getOwner().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                        }
                        //type
                        else if (restrictions[2].getText().equals(r.getText())) {
                            for (ShopType shopType : ShopType.values()) {
                                if (shopType.toString().toLowerCase().contains(s)) {
                                    filterCompaniesSet.add(c);
                                    Log.d(TAG, "type: " + c.getName());
                                    break;
                                }
                            }
                        }
                        //adress
                        else if (restrictions[3].getText().equals(r.getText())) {
                            if (c.getAddress().getCity().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                            if (c.getAddress().getStreet().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                            if (c.getAddress().getZip().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                        }
                        //description companie
                        else if (restrictions[4].getText().equals(r.getText())) {
                            if (c.getDescription().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                        }
                        //description products
                        else if (restrictions[5].getText().equals(r.getText())) {
                            if (c.getProductsDescription().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                        }
                        //product tags
                        else if (restrictions[6].getText().equals(r.getText())) {
                            List<ProductGroup> productGroups = c.getProductGroups();
                            boolean isAdded = false;
                            for (ProductGroup p : productGroups) {
                                for (String tag : p.getProductTags()) {
                                    if (tag.toLowerCase().contains(s)) {
                                        filterCompaniesSet.add(c);
                                        isAdded = true;
                                        break;
                                    }
                                }
                                if (isAdded) break;
                            }
                        }
                        //opening hours comments
                        else if (restrictions[7].getText().equals(r.getText())) {
                            if (c.getOpeningHoursComments().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                        }
                        //organisation names
                        else if (restrictions[8].getText().equals(r.getText())) {
                            List<Organization> orgs = c.getOrganizations();
                            for (Organization o : orgs) {
                                if (o.getName().toLowerCase().contains(s)) {
                                    filterCompaniesSet.add(c);
                                    break;
                                }
                            }
                        }
                        //messages
                        else if (restrictions[9].getText().equals(r.getText())) {
                            List<Message> messages = c.getMessages();
                            for (Message m : messages) {
                                if (m.getContent().toLowerCase().contains(s)) {
                                    filterCompaniesSet.add(c);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        filteredCompanies.clear();
        filteredCompanies.addAll(filterCompaniesSet);
    }

    /**
     * builds the Filter BottomSheetFragment, when clicked on the filterButton
     */
    private void buildFilter(){
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Array aller organizations übergeben
                BottomSheetFilter settingsDialog = new BottomSheetFilter(MainActivity.this, organisations, categories, types, restrictions);
                settingsDialog.show(getSupportFragmentManager(), "SETTINGS_SHEET");
                settingsDialog.setOnItemClickListener(new BottomSheetFilter.OnItemClickListener() {
                    @Override
                    public void onOrganisationClick(int position, boolean isChecked) {
                        organisations[position].check(isChecked);
                    }

                    @Override
                    public void onTypeClick(int position, boolean isChecked) {
                        types[position].check(isChecked);
                        if (isChecked) {
                            companies.iterator().forEachRemaining(x -> {
                                if (x.getTypes().contains(types[position]))
                                    filteredCompanies.add(x);
                                else filteredCompanies.remove(x);
                            });
                        } else {
                            companies.iterator().forEachRemaining(x -> {
                                if (!(x.getTypes().contains(types[position])))
                                    filteredCompanies.add(x);
                            });
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCategoryClick(int position, boolean isChecked) {
                        categories[position].check(isChecked);
                        if (isChecked) {
                            companies.iterator().forEachRemaining(x -> {
                                x.getProductGroups().iterator().forEachRemaining(a -> {
                                    if(a.getCategory().toString().equals(categories[position].getText()))
                                    filteredCompanies.add(x);
                                else filteredCompanies.remove(x);
                            }); });
                        } else {
                            companies.iterator().forEachRemaining(x -> {
                                x.getProductGroups().iterator().forEachRemaining(a -> {
                                    if(!(a.getCategory().toString().equals(categories[position].getText())))
                                        filteredCompanies.remove(x);
                        });
                    });
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onRestrictionClick(int position, boolean isChecked) {
                        restrictions[position].check(isChecked);
                    }

                    @Override
                    public void onTimeStartChanged(String time) {

                    }

                    @Override
                    public void onTimeEndChanged(String time) {

                    }

                    @Override
                    public void onTimeDateChanged(String time) {

                    }

                    @Override
                    public void onDeliveryClick(boolean isD) {
                        isDelivery = isD;
                    }

                    @Override
                    public void onOpenedClick(boolean isO) {
                        isOpen = isO;
                    }
                });
            }
        });
    }

    /**
     * builds the main RecyclerView
     */
    private void buildRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new CompanyAdapter(filteredCompanies);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}