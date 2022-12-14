package de.uni_marburg.sp21;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.uni_marburg.sp21.company_data_structure.Address;
import de.uni_marburg.sp21.company_data_structure.Company;
import de.uni_marburg.sp21.company_data_structure.Organization;
import de.uni_marburg.sp21.company_data_structure.ProductGroup;
import de.uni_marburg.sp21.company_data_structure.ShopType;
import de.uni_marburg.sp21.glide.FullscreenGalleryActivity;

public class CompanyActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private final int LAYOUT_COLUMNS = 3;
    private Company company;
    private MapView mapView;
    private TextView nameText;
    private TextView descriptionText;
    private TextView mailText;
    private TextView urlText;
    private TextView[] weekdaysText = new TextView[7];
    private TextView organisationsText;
    private TextView productGroupsText;
    private TextView productGroupsDescriptionText;
    private TextView deliveryText;
    private TextView addressText;
    private TextView openingHoursDescriptionText;
    private TextView shopTypes;
    private TextView messagesTitle;
    private TextView picturesTitle;
    private TextView productCategoriesTitle;


    private ImageView mainIcon;
    private ImageView messagesIcon;
    private ImageView picturesIcon;
    private ImageView deliveryIcon;
    private ImageView mailIcon;
    private ImageView urlIcon;
    private ImageView organisationsIcon;
    private TextView organisationTitle;
    private ImageView productCategoriesIcon;

    //RecyclerView PictureGallery
    private PictureGalleryAdapter galleryAdapter;
    private RecyclerView galleryRecyclerview;
    private RecyclerView.LayoutManager galleryLayoutManager;

    //RecyclerView Messages
    private MessageAdapter messageAdapter;
    private RecyclerView messageRecyclerView;
    private RecyclerView.LayoutManager messageLayoutManager;

    private ConstraintLayout constraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        company = Company.load();
        initializeViews();
        setViewValues();
        buildRecyclerViews();
        constraintLayout = findViewById(R.id.cl);
        // Get a handle to the fragment and register the callback.
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView = (MapView) findViewById(R.id.mapView);
        mapView. onCreate(mapViewBundle);
        mapView.getMapAsync(this);
    }


    private void buildRecyclerViews() {
        messageRecyclerView = findViewById(R.id.rvMessages);
        messageAdapter = new MessageAdapter(company.getMessages(), true);
        messageLayoutManager = new LinearLayoutManager(CompanyActivity.this);
        messageRecyclerView.setLayoutManager(messageLayoutManager);
        messageRecyclerView.setAdapter(messageAdapter);

        galleryRecyclerview = findViewById(R.id.rvPictures);
        galleryAdapter = new PictureGalleryAdapter(company.getImagePaths());
        galleryLayoutManager = new GridLayoutManager(CompanyActivity.this, LAYOUT_COLUMNS);
        galleryRecyclerview.setLayoutManager(galleryLayoutManager);
        galleryRecyclerview.setAdapter(galleryAdapter);
        galleryAdapter.setListerner(new PictureGalleryAdapter.OnPhotoClickListerner() {
            @Override
            public void onPhotoClickListerner(int pos) {
                FullscreenGalleryActivity.setImagePath(company.getImagePaths());
                FullscreenGalleryActivity.setPosition(pos);
                Intent intent = new Intent(getBaseContext(), FullscreenGalleryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initializeViews() {
        nameText = findViewById(R.id.companyName);
        shopTypes = findViewById(R.id.shopTypes);
        descriptionText = findViewById(R.id.companyDescription);
        mailText = findViewById(R.id.companyMail);
        urlText = findViewById(R.id.companyURL);
        weekdaysText[0] = findViewById(R.id.companyMonday);
        weekdaysText[1] = findViewById(R.id.companyTuesday);
        weekdaysText[2] = findViewById(R.id.companyWednesday);
        weekdaysText[3] = findViewById(R.id.companyThursday);
        weekdaysText[4] = findViewById(R.id.companyFriday);
        weekdaysText[5] = findViewById(R.id.companySaturday);
        weekdaysText[6] = findViewById(R.id.companySunday);
        organisationsText = findViewById(R.id.companyOrganisations);
        productGroupsText = findViewById(R.id.companyProductGroups);
        productGroupsDescriptionText = findViewById(R.id.companyProductGroupsDescription);
        openingHoursDescriptionText = findViewById(R.id.companyOpeningHoursDescription);
        deliveryText = findViewById(R.id.companyDeliveryText);
        addressText = findViewById(R.id.companyAdress);
        mainIcon = findViewById(R.id.companyImage);
        deliveryIcon = findViewById(R.id.companyDeliveryIcon);
        urlIcon = findViewById(R.id.urlIcon);
        mailIcon = findViewById(R.id.mailIcon);
        productCategoriesIcon = findViewById(R.id.productGroupIcon);
        organisationsIcon = findViewById(R.id.organisationIcon);
        organisationTitle = findViewById(R.id.organisationsTitle);
        productCategoriesTitle = findViewById(R.id.productGroupsTitle);
        messagesTitle = findViewById(R.id.messagesText);
        picturesTitle = findViewById(R.id.picturesText);
        picturesIcon = findViewById(R.id.picturesIcon);
        messagesIcon = findViewById(R.id.messagesIcon);
    }

    private void setViewValues() {
        //name and owner
        String owner = company.getOwner();
        if (owner.isEmpty()) {
            nameText.setText(company.getName());
        } else {
            nameText.setText(company.getName() + ", " + company.getOwner());
        }
        //description
        removeViewWhenEmpty(descriptionText, company.getDescription());
        //shopTypes
        String typesString = "";
        List<ShopType> shopTypesList = company.getTypes();
        for (ShopType s : shopTypesList) {
            typesString += s.toString() + ", ";
        }
        if (!typesString.isEmpty()) {
            typesString = typesString.substring(0, typesString.length() - 2);
        } else {
            removeViewWhenEmpty(shopTypes, typesString);
        }
        shopTypes.setText(typesString);
        //mail
        removeViewWhenEmpty(mailIcon, mailText, company.getMail());
        //url
        removeViewWhenEmpty(urlIcon, urlText, company.getUrl());
        //openingHoursComment
        removeViewWhenEmpty(openingHoursDescriptionText, company.getOpeningHoursComments());
        //productCategoriesComment
        removeViewWhenEmpty(productGroupsDescriptionText, company.getProductsDescription());
        //address
        Address address = company.getAddress();
        addressText.setText(address.getZip() + " " + address.getCity() + " " + address.getStreet());
        //organisations
        String orgs = "";
        for (Organization o : company.getOrganizations()) {
            orgs += o.getName() + ", ";
        }
        if (!orgs.isEmpty()) {
            orgs = orgs.substring(0, orgs.length() - 2);
        }
        removeViewWhenEmpty(organisationTitle, organisationsIcon, organisationsText, orgs);
        //product categories
        String prods = "";
        for (ProductGroup p : company.getProductGroups()) {
            prods += p.getCategory().toString() + ", ";
        }
        if (!prods.isEmpty()) {
            prods = prods.substring(0, prods.length() - 2);
        }
        removeViewWhenEmpty(productCategoriesTitle, productCategoriesIcon, productGroupsText, prods);
        //delivery service
        if (company.isDeliveryService()) {
            deliveryText.setText(getResources().getString(R.string.delivery_true));
            deliveryText.setTextColor(ContextCompat.getColor(CompanyActivity.this, R.color.green_delivery));
            deliveryIcon.setColorFilter(ContextCompat.getColor(CompanyActivity.this, R.color.green_delivery), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            deliveryText.setText(getResources().getString(R.string.delivery_false));
            deliveryText.setTextColor(ContextCompat.getColor(CompanyActivity.this, R.color.red_delivery));
            deliveryIcon.setColorFilter(ContextCompat.getColor(CompanyActivity.this, R.color.red_delivery), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        //image
        company.setImageToImageView(mainIcon);
        //opening hours
        Map<String, ArrayList<Map<String, String>>> openingHours = company.getOpeningHours();
        for (int i = 0; i < TimeConverter.WEEKDAYS.length; i++) {
            String openingHoursAtDay = "";
            ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) openingHours.get(TimeConverter.WEEKDAYS[i]);
            if (list != null) {
                for (Map<String, String> m : list) {
                    openingHoursAtDay += m.get("start") + " - " + m.get("end") + "    ";
                }
                if (!openingHoursAtDay.isEmpty()) {
                    openingHoursAtDay = openingHoursAtDay.substring(0, openingHoursAtDay.length() - 4);
                }
            } else {
                openingHoursAtDay = getResources().getString(R.string.closed);
            }
            weekdaysText[i].setText(openingHoursAtDay);
        }
        //messages
        if (company.getMessages().isEmpty()) {
            removeViewWhenEmpty(messagesIcon, messagesTitle, "");
        }
        if (company.getImagePaths().isEmpty()) {
            removeViewWhenEmpty(picturesIcon, picturesTitle, "");
        }
    }

    private void removeViewWhenEmpty(TextView textView, String s) {
        if (s.isEmpty()) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(s);
        }
    }

    private void removeViewWhenEmpty(ImageView imageView, TextView textView, String s) {
        if (s.isEmpty()) {
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        } else {
            textView.setText(s);
        }
    }

    private void removeViewWhenEmpty(TextView title, ImageView icon, TextView textView, String s) {
        if (s.isEmpty()) {
            textView.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        } else {
            textView.setText(s);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Get a handle to the GoogleMap object and display marker.
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(company.getLocation().getLatitude(), company.getLocation().getLongitude()))
                .title(company.getName() + " Marker"));
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(company.getLocation().getLatitude() , company.getLocation().getLongitude()), 16f));
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}