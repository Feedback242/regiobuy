package de.uni_marburg.sp21;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import de.uni_marburg.sp21.data_structure.Address;
import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.Organization;
import de.uni_marburg.sp21.data_structure.ProductGroup;

public class CompanyActivity extends AppCompatActivity {

    private Company company;

    private final String[] WEEKDAYS = new String[]{"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};
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

    private ImageView mainIcon;
    private ImageView deliveryIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        company = Company.load(CompanyActivity.this);
        initializeViews();
        setViewValues();
    }

    private void initializeViews() {
        nameText = findViewById(R.id.companyName);
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
    }

    private void setViewValues(){
        nameText.setText(company.getName() + ", " + company.getOwner());
        descriptionText.setText(company.getDescription());
        mailText.setText(company.getMail());
        urlText.setText(company.getUrl());
        openingHoursDescriptionText.setText(company.getOpeningHoursComments());
        productGroupsDescriptionText.setText(company.getProductsDescription());

        Address address = company.getAddress();
        addressText.setText(address.getZip() + " " + address.getCity() + " " +address.getStreet());

        String orgs = "";
        for(Organization o : company.getOrganizations()){
            orgs += o.getName() + ", ";
        }
        if(!orgs.isEmpty()){
            orgs = orgs.substring(0,orgs.length() - 2);
        }
        organisationsText.setText(orgs);

        String prods = "";
        for(ProductGroup p : company.getProductGroups()){
            prods += p.getCategory().toString() + ", ";
        }
        if(!prods.isEmpty()){
            prods = prods.substring(0,prods.length() - 2);
        }
        productGroupsText.setText(prods);

        if(company.isDeliveryService()){
            deliveryText.setText("Dieses Unternehmen liefert!");
            deliveryText.setTextColor(ContextCompat.getColor(CompanyActivity.this, R.color.green_delivery));
            deliveryIcon.setColorFilter(ContextCompat.getColor(CompanyActivity.this, R.color.green_delivery), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            deliveryText.setText("Dieses Unternehmen liefert leider nicht!");
            deliveryText.setTextColor(ContextCompat.getColor(CompanyActivity.this, R.color.red_delivery));
            deliveryIcon.setColorFilter(ContextCompat.getColor(CompanyActivity.this, R.color.red_delivery), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        mainIcon.setImageResource(company.getImageResource());

        Map<String,Map<String, ArrayList<Map<String, String>>>> openingHours = company.getOpeningHours();
        for(int i = 0; i < WEEKDAYS.length; i++){
            String openingHoursAtDay = "";
            ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) openingHours.get(WEEKDAYS[i]);
            if(list != null){
                for(Map<String, String> m : list){
                    openingHoursAtDay += m.get("start") + " - " + m.get("end") + "    ";
                }
                if(!openingHoursAtDay.isEmpty()){
                    openingHoursAtDay = openingHoursAtDay.substring(0, openingHoursAtDay.length()-4);
                }
            } else {
                openingHoursAtDay = "Geschlossen";
            }
            weekdaysText[i].setText(openingHoursAtDay);
        }
    }
}