package de.uni_marburg.sp21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import de.uni_marburg.sp21.data_structure.Category;
import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.Organization;
import de.uni_marburg.sp21.data_structure.ShopType;
import de.uni_marburg.sp21.data_structure.TimeInterval;
import de.uni_marburg.sp21.filter.BottomSheetFilter;
import de.uni_marburg.sp21.filter.CheckItem;
import de.uni_marburg.sp21.filter.Filter;

public class MainActivity extends AppCompatActivity {

    //Database stuff
    private FirebaseFirestore database;
    public List<Company> companies;
    public List<Company> filteredCompanies;

    //RecyclerView
    private CompanyAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    //Views
    private ImageView filterButton;
    private SearchView searchView;

    //Filter stuff
    private CheckItem[] categories;
    private CheckItem[] organisations;
    private CheckItem[] types;
    private CheckItem[] restrictions;
    private boolean isOpen;
    private boolean isDelivery;

    private Context context;

    public static final String INTENT_TAG = "clicked_company";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;

        initializeViews();
        getData();

        buildRecyclerView();
        buildFilterView();
        buildSearchView();
    }


    private void sortFilteredCompanies(){
        Collections.sort(filteredCompanies, (o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    private void filterAndUpdateRecyclerview(){
        filteredCompanies.clear();
        filteredCompanies.addAll(Filter.filter(searchView.getQuery().toString(), companies, types, organisations, categories, restrictions, isDelivery, isOpen));
        sortFilteredCompanies();
        adapter.notifyDataSetChanged();
    }

    private void buildFilterView(){
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFilter settingsDialog = new BottomSheetFilter(MainActivity.this, organisations, categories, types, restrictions);
                settingsDialog.show(getSupportFragmentManager(), "SETTINGS_SHEET");
                settingsDialog.setOnItemClickListener(new BottomSheetFilter.OnItemClickListener() {
                    @Override
                    public void onOrganisationClick(int position, boolean isChecked) {
                        organisations[position].check(isChecked);
                        filterAndUpdateRecyclerview();
                    }

                    @Override
                    public void onTypeClick(int position, boolean isChecked) {
                        types[position].check(isChecked);
                        filterAndUpdateRecyclerview();
                    }

                    @Override
                    public void onCategoryClick(int position, boolean isChecked) {
                        categories[position].check(isChecked);
                        filterAndUpdateRecyclerview();
                    }

                    @Override
                    public void onRestrictionClick(int position, boolean isChecked) {
                        restrictions[position].check(isChecked);
                        filterAndUpdateRecyclerview();
                    }

                    @Override
                    public void onTimeStartChanged(String time) {

                        System.out.println(time);

                        String[] hourAndMinutes = time.split(":");
                        TimeInterval.setStart(new Time(Integer.parseInt(hourAndMinutes[0]), Integer.parseInt(hourAndMinutes[1]), 0));
                    //    filterAndUpdateRecyclerview();
                    }

                    @Override
                    public void onTimeEndChanged(String time) {
                        System.out.println(time);
                        String[] hourAndMinutes = time.split(":");
                        TimeInterval.setEnd(new Time(Integer.parseInt(hourAndMinutes[0]), Integer.parseInt(hourAndMinutes[1]), 0));
                       // filterAndUpdateRecyclerview();
                    }

                    @Override
                    public void onTimeDateChanged(String time) {
                        System.out.println(time);
                        TimeInterval.setDate(time);
                        filterAndUpdateRecyclerview();
                    }

                    @Override
                    public void onDeliveryClick(boolean isD) {
                        isDelivery = isD;
                        filterAndUpdateRecyclerview();
                    }

                    @Override
                    public void onOpenedClick(boolean isO) {
                        isOpen = isO;
                        filterAndUpdateRecyclerview();
                    }
                });
            }
        });
    }

    private void getData(){
        database = FirebaseFirestore.getInstance();
        companies = DataBaseManager.getCompanyList(database, MainActivity.this);
        filteredCompanies = new ArrayList<>();
        filteredCompanies.addAll(companies);

        categories = Category.createCheckItemArray();
        types = ShopType.createCheckItemArray();
        organisations = getOrganisations();
        restrictions = new CheckItem[]{new CheckItem("Name Betrieb"), new CheckItem("Name Besitzer"), new CheckItem("Arten Betrieb"),
                new CheckItem("Adresse"), new CheckItem("Beschreibung Betrieb"), new CheckItem("Beschreibung Produkte"),
                new CheckItem("Schlagworte Produktgruppen"), new CheckItem("Öffnungseit Anmerkung"), new CheckItem("Name Organisation"),
                new CheckItem("Nachrichten des Betriebes")};
    }

    private void initializeViews(){
        searchView = findViewById(R.id.searchView);
        filterButton = findViewById(R.id.filterButton);
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

    private void buildSearchView(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAndUpdateRecyclerview();
                return false;
            }
        });
    }

    private void buildRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        sortFilteredCompanies();
        adapter = new CompanyAdapter(filteredCompanies);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setListener(new CompanyAdapter.OnItemClickListener() {
            @Override
            public void onCompanyClick(int pos) {
                Intent intent = new Intent(context, CompanyActivity.class);
                Company.save(filteredCompanies.get(pos), context);
                startActivity(intent);
            }
        });
    }
}