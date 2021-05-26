package de.uni_marburg.sp21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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
    public HashSet<Company> filteredCompanies;

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
        filteredCompanies = new HashSet<>(companies);

        filterButton = findViewById(R.id.filterButton);
        buildRecyclerView();
        buildFilter();
        Button button = findViewById(R.id.testButton);

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
                    filter(newText);
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
        filteredCompanies.clear();
        for (Company c : companies) {
            for (CheckItem r : restrictions) {
                if (r.isChecked()) {
                    //company name
                    if (restrictions[0].equals(r.getText())) {
                        if (c.getName().contains(s)) {
                            filteredCompanies.add(c);
                        }
                    }
                    //name owner
                    else if (restrictions[1].equals(r.getText())) {
                        if (c.getOwner().contains(s)) {
                            filteredCompanies.add(c);
                        }
                    }
                    //type
                    else if (restrictions[2].equals(r.getText())) {
                        for (ShopType shopType : ShopType.values()){
                            if (shopType.toString().contains(s)) {
                                filteredCompanies.add(c);
                                break;
                            }
                        }
                    }
                    //adress
                    else if (restrictions[3].equals(r.getText())) {
                        if (c.getAddress().getCity().contains(s)) {
                            filteredCompanies.add(c);
                        }
                        if (c.getAddress().getStreet().contains(s)) {
                            filteredCompanies.add(c);
                        }
                        if (c.getAddress().getZip().contains(s)) {
                            filteredCompanies.add(c);
                        }
                    }
                    //description companie
                    else if (restrictions[4].equals(r.getText())) {
                        if (c.getDescription().contains(s)) {
                            filteredCompanies.add(c);
                        }
                    }
                    //description products
                    else if (restrictions[5].equals(r.getText())) {
                        if (c.getProductsDescription().contains(s)) {
                            filteredCompanies.add(c);
                        }
                    }
                    //product tags
                    else if (restrictions[6].equals(r.getText())) {
                        List<ProductGroup> productGroups = c.getProductGroups();
                        boolean isAdded = false;
                        for (ProductGroup p : productGroups){
                            for(String tag : p.getProductTags()){
                                if (tag.contains(s)) {
                                    filteredCompanies.add(c);
                                    isAdded = true;
                                    break;
                                }
                            }
                            if (isAdded) break;
                        }
                    }
                    //opening hours comments
                    else if (restrictions[7].equals(r.getText())) {
                        if (c.getOpeningHoursComments().contains(s)) {
                            filteredCompanies.add(c);
                        }
                    }
                    //organisation names
                    else if (restrictions[8].equals(r.getText())) {
                        List<Organization> orgs = c.getOrganizations();
                        for (Organization o : orgs){
                            if (o.getName().contains(s)) {
                                filteredCompanies.add(c);
                                break;
                            }
                        }
                    }
                    //messages
                    else if (restrictions[9].equals(r.getText())) {
                        List<Message> messages = c.getMessages();
                        for (Message m : messages){
                            if (m.getContent().contains(s)) {
                                filteredCompanies.add(c);
                                break;
                            }
                        }
                    }
                }
            }
            adapter.notifyDataSetChanged();
        }
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
                        if (isChecked) {
                            types[position].check();
                            companies.iterator().forEachRemaining(x -> {
                                if (x.getTypes().contains(types[position]))
                                    filteredCompanies.add(x);
                                else filteredCompanies.remove(x);
                            });
                        } else {
                            types[position].unCheck();
                            companies.iterator().forEachRemaining(x -> {
                                if (!(x.getTypes().contains(types[position])))
                                    filteredCompanies.add(x);
                            });
                        }
                    }

                    @Override
                    public void onCategoryClick(int position, boolean isChecked) {
                        if (isChecked) {
                            categories[position].check();
                            companies.iterator().forEachRemaining(x -> {
                                x.getProductGroups().iterator().forEachRemaining(a -> {
                                    if(a.getCategory().toString().equals(categories[position].getText()))
                                    filteredCompanies.add(x);
                                else filteredCompanies.remove(x);
                            }); });
                        } else {
                            categories[position].unCheck();
                            companies.iterator().forEachRemaining(x -> {
                                x.getProductGroups().iterator().forEachRemaining(a -> {
                                    if(!(a.getCategory().toString().equals(categories[position].getText())))
                                        filteredCompanies.remove(x);
                        });
                    });
                        }
                        categories[position].check(isChecked);
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