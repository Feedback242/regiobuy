package de.uni_marburg.sp21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

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
import de.uni_marburg.sp21.company_data_structure.Restriction;

import de.uni_marburg.sp21.filter.FavoritesManager;
import de.uni_marburg.sp21.company_data_structure.Message;
import de.uni_marburg.sp21.company_data_structure.Organization;
import de.uni_marburg.sp21.company_data_structure.ShopType;
import de.uni_marburg.sp21.filter.BottomSheetFilter;
import de.uni_marburg.sp21.filter.CheckItem;
import de.uni_marburg.sp21.filter.Filter;
import de.uni_marburg.sp21.filter.LocationBottomSheet;
import de.uni_marburg.sp21.filter.PickedTime;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Map
    ImageView mapIcon;
    ImageView closeIcon;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 16;
    private static final String TAG = MainActivity.class.getSimpleName();
    BottomSheetFilter settingsDialog;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);

    //Database stuff
    private FirebaseFirestore database;
    public List<Company> companies;
    public static List<Company> filteredCompanies;
    private List<Company> defaultCompany;

    //RecyclerView
    private CompanyAdapter adapter;
    private RecyclerView recyclerView;

    //PushMessages
    private final Date MESSAGES_DATE = new Date(2020 - 1900, 5, 25, 16, 18, 45);
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
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private PlacesClient placesClient;

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
        buildBottomSheet();
        buildMap();
        mapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                mapIcon.setVisibility(View.INVISIBLE);
                closeIcon.setVisibility(View.VISIBLE);
            }
        });
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.getView().setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                mapIcon.setVisibility(View.VISIBLE);
                closeIcon.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void buildMap() {
        mapIcon = findViewById(R.id.main_map);
        closeIcon = findViewById(R.id.close_map);
        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getView().setVisibility(View.GONE);
        assert mapFragment != null;
        mapFragment.getMapAsync(this::onMapReady);
    }



    private void updateMarker(GoogleMap mMap) {
        mMap.clear();
        for (Company company : filteredCompanies) {
            MarkerOptions marker = new MarkerOptions().icon(getIcon(company))
                    .position(new LatLng(company.getLocation().getLatitude(), company.getLocation().getLongitude()))
                    .title(company.getName())
                    .draggable(false);
            //markers.add(marker);
            mMap.addMarker(marker);

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // Add a marker in Sydney and move the camera
        for (Company company : filteredCompanies) {
            MarkerOptions marker = new MarkerOptions().icon(getIcon(company))
                    .position(new LatLng(company.getLocation().getLatitude(), company.getLocation().getLongitude()))
                    .title(company.getName())
                    .draggable(false);
           // markers.add(marker);
            mMap.addMarker(marker);

        }
        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(@NonNull Marker marker) {
                final Company[] selectedCompany = new Company[1];
                Intent intent = new Intent(MainActivity.this, CompanyActivity.class);
                filteredCompanies.iterator().forEachRemaining(x -> {
                    if (x.getName().equals(marker.getTitle()))
                        selectedCompany[0] = x;

                });
                Company.save(selectedCompany[0]);

                startActivity(intent);
            }
        });

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        if(!filteredCompanies.isEmpty()){
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(filteredCompanies.get(0).getLocation().getLatitude() , filteredCompanies.get(0).getLocation().getLongitude()), 10f));
        }
    }



    private void updateLocationUI() {
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */

        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(MainActivity.this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private BitmapDescriptor getIcon(Company company) {
        BitmapDescriptor marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        for(ShopType shopType : company.getTypes()){
            switch (shopType){
                case MART:
                    marker =  BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                    break;
                case SHOP:
                    marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                    break;
                case HOTEL:
                    marker =BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                    break;
                case PRODUCER:
                    marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    break;
                case RESTAURANT:
                    marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);
                    break;
            }
        }
        if (company.isFavorite())
            marker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
        return marker;
    }

    private void buildBottomSheet(){
        pickedTime = new PickedTime();
        settingsDialog = new BottomSheetFilter(MainActivity.this, organisations, categories, types, restrictions, isDelivery, isOpen, pickedTime);

        settingsDialog.setOnItemClickListener(new BottomSheetFilter.OnItemClickListener() {
            @Override
            public void onOrganisationClick(int position, boolean isChecked) {
                organisations[position].check(isChecked);
                filterAndUpdateRecyclerview();
                updateMarker(mMap);
            }

            @Override
            public void onTypeClick(int position, boolean isChecked) {
                types[position].check(isChecked);
                filterAndUpdateRecyclerview();
                updateMarker(mMap);
            }

            @Override
            public void onCategoryClick(int position, boolean isChecked) {
                categories[position].check(isChecked);
                filterAndUpdateRecyclerview();
                updateMarker(mMap);
            }

            @Override
            public void onRestrictionClick(int position, boolean isChecked) {
                restrictions[position].check(isChecked);
                filterAndUpdateRecyclerview();
                updateMarker(mMap);
            }

            @Override
            public void onTimeStartChanged(String time) {
                pickedTime.setStartTime(TimeConverter.convertToDate(time));
                filterAndUpdateRecyclerview();
                updateMarker(mMap);
            }

            @Override
            public void onTimeEndChanged(String time) {
                pickedTime.setEndTime(TimeConverter.convertToDate(time));
                filterAndUpdateRecyclerview();
                updateMarker(mMap);
            }

            @Override
            public void onTimeDateChanged(String weekday) {
                pickedTime.setWeekday(weekday);
                filterAndUpdateRecyclerview();
                updateMarker(mMap);
            }

            @Override
            public void onDeliveryClick(boolean isD) {
                isDelivery = isD;
                filterAndUpdateRecyclerview();
                updateMarker(mMap);
            }

            @Override
            public void onOpenedClick(boolean isO) {
                isOpen = isO;
                filterAndUpdateRecyclerview();
                updateMarker(mMap);
            }

            @Override
            public void onResetTimePickerClick() {
                resetTimePicker();
                filterAndUpdateRecyclerview();
                updateMarker(mMap);
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
        filteredCompanies.addAll(Filter.filter(searchView.getQuery().toString(), companies, types, organisations, categories, restrictions, isDelivery, isOpen, pickedTime, radius));
        sortFilteredCompanies();
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
                            updateMarker(mMap);
                        }
                    }
                }else {
                    favoriteButton.setImageResource(R.drawable.ic_baseline_star_border_24);
                    companies.addAll(defaultCompany);
                    filterAndUpdateRecyclerview();
                    updateMarker(mMap);
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
                        filterAndUpdateRecyclerview();
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
        restrictions = Restriction.createCheckItemArray();
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
                updateMarker(mMap);
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