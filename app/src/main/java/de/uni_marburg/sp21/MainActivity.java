package de.uni_marburg.sp21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import de.uni_marburg.sp21.company_data_structure.Category;
import de.uni_marburg.sp21.company_data_structure.Company;
import de.uni_marburg.sp21.filter.FavoritesManager;
import de.uni_marburg.sp21.company_data_structure.Message;
import de.uni_marburg.sp21.company_data_structure.Organization;
import de.uni_marburg.sp21.company_data_structure.ShopType;
import de.uni_marburg.sp21.filter.BottomSheetFilter;
import de.uni_marburg.sp21.filter.CheckItem;
import de.uni_marburg.sp21.filter.Filter;
import de.uni_marburg.sp21.filter.LocationBottomSheet;
import de.uni_marburg.sp21.filter.PickedTime;

public class MainActivity extends AppCompatActivity {

    //Map
    ImageView mapIcon;
    BottomSheetFilter.OnItemClickListener listener;
    BottomSheetFilter settingsDialog;
    //Database stuff
    private FirebaseFirestore database;
    public List<Company> companies;
    public static List<Company> filteredCompanies;
    private List<Company> defaultCompany;

    //RecyclerView
    private CompanyAdapter adapter;
    private RecyclerView recyclerView;

    //PushMessages
    private final Date MESSAGES_DATE = new Date(2020 - 1900,5,25,16,18,45);
    private List<Message> messages = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView messageRecyclerView;
    private FloatingActionButton removeAllMessages;

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

    //Favorites
    private FavoritesManager favoritesManager;

    //ShoppingList
    private ImageView shoppingList;

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

        buildFavorites();
        buildRecyclerView();
        buildFilterView();
        buildSearchView();
        buildLocationView();
        buildMessageRecyclerView();
        buildShoppingListView();
        buildMap();buildBottomSheet();
    }

    private void buildMap() {
        mapIcon = findViewById(R.id.main_map);

        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.setCompanyList(filteredCompanies);
                MapsActivity.setBottomSheetFilter(settingsDialog);
                MapsActivity.setPickedTime(pickedTime);
                Intent intent = new Intent( context, MapsActivity.class);
                MapsActivity.setCategories(categories);
                MapsActivity.setOrganisations(organisations);
                MapsActivity.setTypes(types);
                intent.putExtra("isOpen",isOpen);
                intent.putExtra("isDelivery",isDelivery);
                MapsActivity.setRestrictions( restrictions);
                startActivity(intent);
            }
        });
    }

    private void buildBottomSheet(){
        pickedTime = new PickedTime();
        settingsDialog = new BottomSheetFilter(MainActivity.this, organisations, categories, types, restrictions, isDelivery, isOpen, pickedTime);

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

    private void buildShoppingListView(){
        shoppingList = findViewById(R.id.shoppingList);
        shoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetShoppingList shoppingDialog = new BottomSheetShoppingList();
                shoppingDialog.show(getSupportFragmentManager(), "SHOPPING_SHEET");
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            messages.remove(position);
            messageAdapter.notifyItemRemoved(position);
            if(messages.isEmpty()){
                removeMessagesViews();
            }
        }
    };

    private void buildMessageRecyclerView(){
        messageRecyclerView = findViewById(R.id.pushMessagesRecyclerView);
        removeAllMessages = findViewById(R.id.removeAllMessagesButton);
        for (Company company : companies) {
            if (company.isFavorite()) {
                for (Message message : company.getMessages()) {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    try {
                        Date date = inputFormat.parse(message.getDate());
                        if (date.after(MESSAGES_DATE)) {
                            messages.add(message);
                        }
                        ;
                    } catch (ParseException e) {
                    }
                }
            }
        }
        if(messages.isEmpty()){
            removeMessagesViews();
        } else {
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(messageRecyclerView);

            messageAdapter = new MessageAdapter(messages,true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
            messageRecyclerView.setLayoutManager(layoutManager);
            messageRecyclerView.setAdapter(messageAdapter);
            messageAdapter.setOnMessageClickListener(new MessageAdapter.OnMessageClickListener() {
                @Override
                public void onClick(String companyName) {
                    for(Company company : companies){
                        if(company.getName() == companyName){
                            Intent intent = new Intent(context, CompanyActivity.class);
                            Company.save(company);
                            startActivity(intent);
                        }
                    }
                }
            });
        }
        removeAllMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messages.clear();
                messageAdapter.notifyDataSetChanged();
                removeMessagesViews();
            }
        });
    }

    private void removeMessagesViews(){
        messageRecyclerView.setVisibility(View.GONE);
        removeAllMessages.setVisibility(View.GONE);
    }

    private void sortFilteredCompanies(){
        Collections.sort(filteredCompanies, (o1, o2) -> o1.getName().compareTo(o2.getName()));
    }

    public void filterAndUpdateRecyclerview(){
        filteredCompanies.clear();
        filteredCompanies.addAll(Filter.filter(searchView.getQuery().toString(), companies, types, organisations, categories, restrictions, isDelivery, isOpen, pickedTime));
        sortFilteredCompanies();
       MapsActivity.setCompanyList(filteredCompanies);
        adapter.notifyDataSetChanged();
    }

    public void buildFilterView(){
        filterButton.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsDialog.show(getSupportFragmentManager(), "SETTINGS_SHEET");
            }
        });
    }

    private void buildFavorites(){
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
        favoritesManager = new FavoritesManager(companies);
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
                Company currentCompany = filteredCompanies.get(pos);
                boolean isFav = currentCompany.isFavorite();
                currentCompany.setFavorite(!isFav);
                favoritesManager.save(currentCompany);
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


    public static void setCompanyList(List<Company> companies) {
        filteredCompanies = companies;
    }

    @Override
    protected void onResume() {
        super.onResume();
        filterAndUpdateRecyclerview();
    }
}