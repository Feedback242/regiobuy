package de.uni_marburg.sp21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import de.uni_marburg.sp21.company_data_structure.Category;
import de.uni_marburg.sp21.company_data_structure.Company;
import de.uni_marburg.sp21.company_data_structure.Organization;
import de.uni_marburg.sp21.company_data_structure.ShopType;
import de.uni_marburg.sp21.filter.BottomSheetFilter;
import de.uni_marburg.sp21.filter.CheckItem;
import de.uni_marburg.sp21.filter.Filter;
import de.uni_marburg.sp21.filter.LocationBottomSheet;
import de.uni_marburg.sp21.filter.PickedTime;

public class MainActivity extends AppCompatActivity {

    //Database stuff
    private FirebaseFirestore database;
    public List<Company> companies;
    public List<Company> filteredCompanies;
    private List<Company> defaultCompany;

    //RecyclerView
    private CompanyAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    //Views
    private ImageView favoriteButton;
    private ImageView filterButton;
    private SearchView searchView;
    private ImageView locationButton;

    //Filter stuff
    private CheckItem[] categories;
    private CheckItem[] organisations;
    private CheckItem[] types;
    private CheckItem[] restrictions;
    private boolean isOpen;
    private boolean isDelivery;
    private PickedTime pickedTime;
    private boolean favoriteIsClicked;
    private int radius;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        radius = 0;
        setSystemLanguage();

        pickedTime = new PickedTime();
        initializeViews();
        getData();

        buildRecyclerView();
        buildFilterView();
        buildSearchView();
        buildLocationView();
    }


    private void sortFilteredCompanies(){
        Collections.sort(filteredCompanies, (o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    private void filterAndUpdateRecyclerview(){
        filteredCompanies.clear();
        filteredCompanies.addAll(Filter.filter(searchView.getQuery().toString(), companies, types, organisations, categories, restrictions, isDelivery, isOpen, pickedTime));
        sortFilteredCompanies();
        adapter.notifyDataSetChanged();
    }

    private void buildFilterView(){
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFilter settingsDialog = new BottomSheetFilter(MainActivity.this, organisations, categories, types, restrictions, isDelivery, isOpen, pickedTime);
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
                        pickedTime.setStartTime(TimeConverter.convertToDate(time));
                        filterAndUpdateRecyclerview();
                    }

                    @Override
                    public void onTimeEndChanged(String time) {
                        pickedTime.setEndTime(TimeConverter.convertToDate(time));
                        filterAndUpdateRecyclerview();
                    }

                    @Override
                    public void onTimeDateChanged(String weekday) {
                        pickedTime.setWeekday(weekday);
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

                    @Override
                    public void onResetTimePickerClick() {
                        resetTimePicker();
                        filterAndUpdateRecyclerview();
                    }
                });
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoriteIsClicked = !favoriteIsClicked;
                if (favoriteIsClicked){
                    favoriteButton.setImageResource(R.drawable.ic_baseline_star_24);
                    companies.clear();
                    for (Company c : defaultCompany){
                        if(c.isFavorite()){
                            companies.add(c);
                            filterAndUpdateRecyclerview();
                        }
                    }
                }else {
                    favoriteButton.setImageResource(R.drawable.ic_baseline_star_border_24);
                    companies.addAll(defaultCompany);
                    filterAndUpdateRecyclerview();
                }
            }
        });

    }

    private void buildLocationView(){
        locationButton = findViewById(R.id.locationSearch);
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationBottomSheet locationBottomSheet = new LocationBottomSheet(radius);
                locationBottomSheet.show(getSupportFragmentManager(), "SETTINGS_SHEET");
                locationBottomSheet.setLocationSettingsListener(new LocationBottomSheet.LocationSettingsListener() {
                    @Override
                    public void onLocationChange(int rad) {
                        radius = rad;
                    }
                });
            }
        });
    }

    private void getData(){
        database = FirebaseFirestore.getInstance();
        companies = DataBaseManager.getCompanyList(database);
        filteredCompanies = new ArrayList<>();
        filteredCompanies.addAll(companies);
        defaultCompany = new ArrayList<>(companies);

        categories = Category.createCheckItemArray();
        types = ShopType.createCheckItemArray();
        organisations = getOrganisations();
        restrictions = new CheckItem[]{new CheckItem(getResources().getString(R.string.name_company)), new CheckItem(getResources().getString(R.string.name_owner)), new CheckItem(getResources().getString(R.string.shop_types_without_colon)),
                new CheckItem(getResources().getString(R.string.address)), new CheckItem(getResources().getString(R.string.description_company)), new CheckItem(getResources().getString(R.string.description_products)),
                new CheckItem(getResources().getString(R.string.product_tags)), new CheckItem(getResources().getString(R.string.opening_hours_comment)), new CheckItem(getResources().getString(R.string.name_organisation)),
                new CheckItem(getResources().getString(R.string.messages_company))};
        resetTimePicker();
    }

    private void resetTimePicker(){
        pickedTime.reset();
    }

    private void initializeViews(){
        searchView = findViewById(R.id.searchView);
        filterButton = findViewById(R.id.filterButton);
        favoriteButton = findViewById(R.id.favorite_button);
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
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setListener(new CompanyAdapter.OnItemClickListener() {
            @Override
            public void onCompanyClick(int pos) {
                Intent intent = new Intent(context, CompanyActivity.class);
                Company.save(filteredCompanies.get(pos));
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(int pos) {
                if(filteredCompanies.get(pos).isFavorite()){
                    filteredCompanies.get(pos).setFavorite(false);
                }else {
                    filteredCompanies.get(pos).setFavorite(true);
                }
                Log.d(MyApplication.APP_TAG , "" + filteredCompanies.get(pos).isFavorite());
            }
        });
    }

    private void setSystemLanguage(){
        String languageCode = Locale.getDefault().getDisplayLanguage();
        Resources res = getResources();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(languageCode.toLowerCase()));
    }
}