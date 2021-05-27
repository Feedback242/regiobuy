package de.uni_marburg.sp21;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.ImageView;
import android.widget.TextView;

import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.Organization;
import de.uni_marburg.sp21.data_structure.ProductGroup;

public class CompanyActivity extends AppCompatActivity {

    private Company company;

    private TextView nameText;
    private TextView descriptionText;
    private TextView mailText;
    private TextView urlText;
    private TextView mondayText;
    private TextView tuesdayText;
    private TextView wednesdayText;
    private TextView thursdayText;
    private TextView fridayText;
    private TextView saturdayText;
    private TextView sundayText;
    private TextView organisationsText;
    private TextView productGroupsText;
    private TextView productGroupsDescriptionText;
    private TextView deliveryText;

    private ImageView mainIcon;
    private ImageView deliveryIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        Intent intent = getIntent();
        //todo somehow company is null
        company = (Company) intent.getSerializableExtra(MainActivity.INTENT_TAG);
        initializeViews();
       // setViewValues();
    }

    private void initializeViews() {
        nameText = findViewById(R.id.companyName);
        descriptionText = findViewById(R.id.companyDescription);
        mailText = findViewById(R.id.companyMail);
        urlText = findViewById(R.id.companyURL);
        mondayText = findViewById(R.id.companyMonday);
        tuesdayText = findViewById(R.id.companyTuesday);
        wednesdayText = findViewById(R.id.companyWednesday);
        thursdayText = findViewById(R.id.companyThursday);
        fridayText = findViewById(R.id.companyFriday);
        saturdayText = findViewById(R.id.companySaturday);
        sundayText = findViewById(R.id.companySunday);
        organisationsText = findViewById(R.id.companyOrganisations);
        productGroupsText = findViewById(R.id.companyProductGroups);
        productGroupsDescriptionText = findViewById(R.id.companyProductGroupsDescription);
        deliveryText = findViewById(R.id.companyDeliveryText);

        mainIcon = findViewById(R.id.companyImage);
        deliveryIcon = findViewById(R.id.companyDeliveryIcon);
    }

    private void setViewValues(){
        nameText.setText(company.getName());
        descriptionText.setText(company.getDescription());
        mailText.setText(company.getMail());
        urlText.setText(company.getUrl());

        String orgs = "";
        for(Organization o : company.getOrganizations()){
            orgs += o.getName() + ",";
        }
        orgs.substring(0,orgs.length() - 1);
        organisationsText.setText(orgs);

        String prods = "";
        for(ProductGroup p : company.getProductGroups()){
            prods += p.getCategory().toString() + ",";
        }
        prods.substring(0,prods.length() - 1);
        productGroupsText.setText(prods);

        productGroupsDescriptionText.setText(company.getProductsDescription());

        if(company.isDeliveryService()){
            deliveryText.setText("Dieses Unternehmen liefert!");
            //todo set tint
        } else {
            deliveryText.setText("Dieses Unternehmen liefert leider nicht!");
            //todo set tint
        }
        mainIcon.setImageResource(company.getImageResource());
    }
}