package de.uni_marburg.sp21;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.animation.Animator;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.uni_marburg.sp21.company_data_structure.Company;
import de.uni_marburg.sp21.company_data_structure.Organization;
import de.uni_marburg.sp21.company_data_structure.ShopType;
import de.uni_marburg.sp21.databinding.ActivityMapsBinding;
import de.uni_marburg.sp21.filter.BottomSheetFilter;
import de.uni_marburg.sp21.filter.CheckItem;
import de.uni_marburg.sp21.filter.Filter;
import de.uni_marburg.sp21.filter.PickedTime;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final float DEFAULT_ZOOM = 16;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static BottomSheetFilter bottomSheetFilterMap;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static List<Company> companyList;
    private  List<Company> filteredCompanies =  new ArrayList<>();
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    static BottomSheetFilter.OnItemClickListener listener;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    ImageButton closeSearchButton;
    EditText inputText;
    ImageButton sendSearchButton;
    ImageButton mapFilterButton;
    ImageButton mapSearchButton;
    HashSet<MarkerOptions> markers =  new HashSet<MarkerOptions>();
    SupportMapFragment mapFragment;
    FusedLocationProviderClient fusedLocationProviderClient;
    PlacesClient placesClient;

    //Filter stuff
    private static CheckItem[] categories;
    private static CheckItem[] organisations;
    private static CheckItem[] types;
    private static CheckItem[] restrictions;
    private static boolean isOpen;
    private static boolean isDelivery;
    private static PickedTime pickedTime;
    private boolean favoriteIsClicked;
    private int radius;
    private boolean locationPermissionGranted;
    private View openSeachView;
    private String searchedText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Construct a PlacesClient
        Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        sendSearchButton = findViewById(R.id.execute_search_button);
        sendSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchedText = inputText.getText().toString().trim();
                closeSearch();
                filterAndUpdateMap();
            }
        });
        closeSearchButton = findViewById(R.id.close_search_button);
        closeSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearch();
            }
        });
        inputText = findViewById(R.id.search_input_text);
        mapFilterButton =  findViewById(R.id.map_filter_button);
        mapSearchButton = findViewById(R.id.map_search_button);
        openSeachView = findViewById(R.id.search_open_view);
        mapSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LinearLayout layout = findViewById(R.id.map_search);
                View child = getLayoutInflater().inflate(R.layout.view_search, null);
                //child.animate();
                //layout.addView(child);
                openSearch();
            }
        });
        pickedTime =  new PickedTime();
        //getData();
        mapFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetFilter settingsDialog = new BottomSheetFilter(MapsActivity.this, organisations, categories, types, restrictions, isDelivery, isOpen, pickedTime);
                settingsDialog.show(getSupportFragmentManager(),"SETTING_SHEET");
                settingsDialog.setOnItemClickListener(new BottomSheetFilter.OnItemClickListener() {
                    @Override
                    public void onOrganisationClick(int position, boolean isChecked) {
                        organisations[position].check(isChecked);
                        filterAndUpdateMap();
                    }

                    @Override
                    public void onTypeClick(int position, boolean isChecked) {
                        types[position].check(isChecked);
                        filterAndUpdateMap();
                    }

                    @Override
                    public void onCategoryClick(int position, boolean isChecked) {
                        categories[position].check(isChecked);
                        filterAndUpdateMap();
                    }

                    @Override
                    public void onRestrictionClick(int position, boolean isChecked) {
                        restrictions[position].check(isChecked);
                        filterAndUpdateMap();
                    }

                    @Override
                    public void onTimeStartChanged(String time) {
                        pickedTime.setStartTime(TimeConverter.convertToDate(time));
                        filterAndUpdateMap();
                    }

                    @Override
                    public void onTimeEndChanged(String time) {
                        pickedTime.setEndTime(TimeConverter.convertToDate(time));
                        filterAndUpdateMap();
                    }

                    @Override
                    public void onTimeDateChanged(String weekday) {
                        pickedTime.setWeekday(weekday);
                        filterAndUpdateMap();
                    }

                    @Override
                    public void onDeliveryClick(boolean isD) {
                        isDelivery = isD;
                        filterAndUpdateMap();
                    }

                    @Override
                    public void onOpenedClick(boolean isO) {
                        isOpen = isO;
                        filterAndUpdateMap();
                    }

                    @Override
                    public void onResetTimePickerClick() {
                        resetTimePicker();
                        filterAndUpdateMap();
                    }
                });

            }
        });

        assert mapFragment != null;
        mapFragment.getMapAsync(this::onMapReady);
    }

    private void openSearch() {
        inputText.setText("");
        mapSearchButton.setVisibility(View.INVISIBLE);
        openSeachView.setVisibility(View.VISIBLE);
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                openSeachView,
                mapSearchButton.getScrollX(),
                (mapSearchButton.getTop()+ mapSearchButton.getBottom()) / 2,
                0f, openSeachView.getWidth()
        );
        circularReveal.setDuration(800);
        circularReveal.start();
    }

    private void closeSearch() {
        Animator circularConceal = ViewAnimationUtils.createCircularReveal(
                openSeachView,
                mapSearchButton.getScrollX(),
                (mapSearchButton.getTop()+ mapSearchButton.getBottom()) / 2,
                 openSeachView.getWidth(), 0f
        );

        circularConceal.setDuration(800);
        circularConceal.start();
        circularConceal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                openSeachView.setVisibility(View.INVISIBLE);
                mapSearchButton.setVisibility(View.VISIBLE);
                inputText.setText("");
                circularConceal.removeAllListeners();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }

    /* private void getData(){
        categories = Category.createCheckItemArray();
        types = ShopType.createCheckItemArray();
        organisations = getOrganisations();
        restrictions = Restriction.createCheckItemArray();
        pickedTime = new PickedTime();
        resetTimePicker();
    }
    */


    private void resetTimePicker(){
        pickedTime.reset();
    }





    private CheckItem[] getOrganisations(){
        HashSet<String> set = new HashSet<>();
        for(Company c : companyList){
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




    private void filterAndUpdateMap(){

        filteredCompanies.clear();
        filteredCompanies.addAll(Filter.filter(searchedText, companyList, types, organisations, categories, restrictions, isDelivery, isOpen, pickedTime));
        updateMarker(mMap);

    }

    private void updateMarker(GoogleMap mMap) {
        mMap.clear();
        for (Company company : filteredCompanies) {
            MarkerOptions marker = new MarkerOptions().icon(getIcon(company))
                    .position(new LatLng(company.getLocation().getLatitude(), company.getLocation().getLongitude()))
                    .title(company.getName())
                    .draggable(false);
            markers.add(marker);
            mMap.addMarker(marker);

        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        for (Company company : companyList) {
            MarkerOptions marker = new MarkerOptions().icon(getIcon(company))
                    .position(new LatLng(company.getLocation().getLatitude(), company.getLocation().getLongitude()))
                    .title(company.getName())
                    .draggable(false);
            markers.add(marker);
            mMap.addMarker(marker);

        }
        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(@NonNull  Marker marker) {
                final Company[] selectedCompany = new Company[1];
                Intent intent = new Intent(MapsActivity.this, CompanyActivity.class);
                companyList.iterator().forEachRemaining(x -> {
                    if (x.getName().equals(marker.getTitle()))
                        selectedCompany[0] = x;

                });
                Company.save(selectedCompany[0]);

                startActivity(intent);
            }
        });



        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(companyList.get(0).getLocation().getLatitude() , companyList.get(0).getLocation().getLongitude()), 10f));
    }


    public static void setOnClickListener(BottomSheetFilter.OnItemClickListener onClickListener){
        listener = onClickListener;
    }

    public static BottomSheetFilter.OnItemClickListener getListener() {
        return listener;
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

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("paused map");
        MainActivity.setCompanyList(companyList);
    }


    public static void setCompanyList(List<Company> companies) {
        companyList = companies;
    }

    public static void setCategories(CheckItem[] categories) {
        MapsActivity.categories = categories;
    }

    public static void setIsDelivery(boolean isDelivery) {
        MapsActivity.isDelivery = isDelivery;
    }

    public static void setIsOpen(boolean isOpen) {
        MapsActivity.isOpen = isOpen;
    }

    public static void setTypes(CheckItem[] types) {
        MapsActivity.types = types;
    }

    public static void setOrganisations(CheckItem[] organisations) {
        MapsActivity.organisations = organisations;
    }

    public static void setRestrictions(CheckItem[] restrictions) {
        MapsActivity.restrictions = restrictions;
    }

    public static void setPickedTime(PickedTime pickedTime) {
        MapsActivity.pickedTime = pickedTime;
    }
    static void setBottomSheetFilter(BottomSheetFilter bottomSheetFilter){
        bottomSheetFilterMap = bottomSheetFilter;
    }
}