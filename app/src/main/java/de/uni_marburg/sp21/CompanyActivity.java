package de.uni_marburg.sp21;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Map;
import de.uni_marburg.sp21.company_data_structure.Address;
import de.uni_marburg.sp21.company_data_structure.Company;
import de.uni_marburg.sp21.company_data_structure.Organization;
import de.uni_marburg.sp21.company_data_structure.ProductGroup;

public class CompanyActivity extends AppCompatActivity {

    private Company company;

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

    private ImageView mailIcon;
    private ImageView urlIcon;

    private ImageView organisationsIcon;
    private TextView organisationTitle;

    private ImageView productCategoriesIcon;
    private TextView productCategoriesTitle;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        context = CompanyActivity.this;
        company = Company.load();
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

        urlIcon = findViewById(R.id.urlIcon);
        mailIcon = findViewById(R.id.mailIcon);
        productCategoriesIcon = findViewById(R.id.productGroupIcon);
        organisationsIcon = findViewById(R.id.organisationIcon);
        organisationTitle = findViewById(R.id.organisationsTitle);
        productCategoriesTitle = findViewById(R.id.productGroupsTitle);
    }

    private void setViewValues(){
        //name and owner
        String owner = company.getOwner();
        if(owner.isEmpty()){
            nameText.setText(company.getName());
        } else {
            nameText.setText(company.getName() + ", " + company.getOwner());
        }
        //description
        removeViewWhenEmpty(descriptionText, company.getDescription());
        //mail
        removeViewWhenEmpty(mailIcon ,mailText, company.getMail());
        //url
        removeViewWhenEmpty(urlIcon ,urlText, company.getUrl());
        //openingHoursComment
        removeViewWhenEmpty(openingHoursDescriptionText, company.getOpeningHoursComments());
        //productCategoriesComment
        removeViewWhenEmpty(productGroupsDescriptionText, company.getProductsDescription());
        //address
        Address address = company.getAddress();
        addressText.setText(address.getZip() + " " + address.getCity() + " " +address.getStreet());
        //organisations
        String orgs = "";
        for(Organization o : company.getOrganizations()){
            orgs += o.getName() + ", ";
        }
        if(!orgs.isEmpty()){
            orgs = orgs.substring(0,orgs.length() - 2);
        }
        removeViewWhenEmpty(organisationTitle, organisationsIcon, organisationsText, orgs);
        //product categories
        String prods = "";
        for(ProductGroup p : company.getProductGroups()){
            prods += p.getCategory().toString() + ", ";
        }
        if(!prods.isEmpty()){
            prods = prods.substring(0,prods.length() - 2);
        }
        removeViewWhenEmpty(productCategoriesTitle, productCategoriesIcon, productGroupsText, prods);
        //delivery service
        if(company.isDeliveryService()){
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
        Map<String,ArrayList<Map<String, String>>> openingHours = company.getOpeningHours();
        for(int i = 0; i < TimeConverter.WEEKDAYS.length; i++){
            String openingHoursAtDay = "";
            ArrayList<Map<String, String>> list = (ArrayList<Map<String, String>>) openingHours.get(TimeConverter.WEEKDAYS[i]);
            if(list != null){
                for(Map<String, String> m : list){
                    openingHoursAtDay += m.get("start") + " - " + m.get("end") + "    ";
                }
                if(!openingHoursAtDay.isEmpty()){
                    openingHoursAtDay = openingHoursAtDay.substring(0, openingHoursAtDay.length()-4);
                }
            } else {
                openingHoursAtDay = getResources().getString(R.string.closed);
            }
            weekdaysText[i].setText(openingHoursAtDay);
        }
    }

    private void removeViewWhenEmpty(TextView textView, String s){
        if(s.isEmpty()){
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(s);
        }
    }

    private void removeViewWhenEmpty(ImageView imageView, TextView textView, String s){
        if(s.isEmpty()){
            textView.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
        } else {
            textView.setText(s);
        }
    }

    private void removeViewWhenEmpty(TextView title, ImageView icon, TextView textView, String s){
        if(s.isEmpty()){
            textView.setVisibility(View.GONE);
            icon.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
        } else {
            textView.setText(s);
        }
    }
}