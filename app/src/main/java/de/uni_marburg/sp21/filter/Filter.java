package de.uni_marburg.sp21.filter;

import android.content.Context;
import android.util.Log;

import com.google.type.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.uni_marburg.sp21.CompanyActivity;
import de.uni_marburg.sp21.MainActivity;
import de.uni_marburg.sp21.data_structure.Category;
import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.Message;
import de.uni_marburg.sp21.data_structure.Organization;
import de.uni_marburg.sp21.data_structure.ProductGroup;
import de.uni_marburg.sp21.data_structure.ShopType;

public class Filter {

    public static List<Company> filter(String s, List<Company> companies, CheckItem[] types, CheckItem[] organisations, CheckItem[] categories, CheckItem[] restrictions, boolean isDelivery, boolean isOpen, Context context, String weekday, Date startTime, Date endTime) {
        //HashSets doesn't insert duplicates
        s = s.toLowerCase();
        HashSet<Company> filterCompaniesSet = new HashSet<>();
        // multiple search
        String[] splitString = s.split(" ");
        boolean multiple;
        String rest = "";
        if (multiple = (splitString.length > 1)){
            rest = s.substring(s.indexOf(" ") + 1);
            s = splitString[0];
        }

        for (Company c : companies) {
            Log.d("REGIO", c.getOpeningHours().values().toString());

            //TODO timepicker
            if(weekday != null && startTime != null && endTime != null){
                for(Map<String, String> time : c.getOpeningHours().get(weekday)){
                    Date chosenStart =  new Date(time.get("start")) ;
                    Date chosenEnd =  new Date(time.get("end")) ;
                    if (chosenStart.after(startTime)){
                        if(chosenEnd.before(endTime)){

                        }else {
                            continue;
                        }
                    }else {
                        continue;
                    }

                }

            }

            //isOpen
            if(isOpen){
                Calendar calendar = Calendar.getInstance();
                Date currentTime = calendar.getTime();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                String dayName = "";

                switch (day){
                    case Calendar.MONDAY: dayName = CompanyActivity.WEEKDAYS[0];
                        break;
                    case Calendar.TUESDAY: dayName = CompanyActivity.WEEKDAYS[1];
                        break;
                    case Calendar.WEDNESDAY: dayName = CompanyActivity.WEEKDAYS[2];
                        break;
                    case Calendar.THURSDAY: dayName = CompanyActivity.WEEKDAYS[3];
                        break;
                    case Calendar.FRIDAY: dayName = CompanyActivity.WEEKDAYS[4];
                        break;
                    case Calendar.SATURDAY: dayName = CompanyActivity.WEEKDAYS[5];
                        break;
                    case Calendar.SUNDAY: dayName = CompanyActivity.WEEKDAYS[6];
                        break;
                }
                ArrayList<Map<String, String>> openingList = c.getOpeningHours().get(dayName);
                boolean isCurrentlyOpen = false;
                for(Map<String, String> m : openingList){
                    Date start = MainActivity.convertToDate(m.get("start"));
                    Date end = MainActivity.convertToDate(m.get("end"));
                    isCurrentlyOpen = isCurrentlyOpen || (currentTime.after(start) && currentTime.before(end));
                }

                if(!isCurrentlyOpen){
                    continue;
                }
            }

            //delivery
            if(isDelivery && !c.isDeliveryService()){
                continue;
            }

            //types (the company must have every type)
            boolean isDefaultType = true;
            for (CheckItem check : types) {
                if(check.isChecked()){
                    isDefaultType = false;
                }
            }
            if(!isDefaultType) {
                boolean hasAllTypes = true;
                List<ShopType> shopTypes = c.getTypes();
                for (CheckItem type : types) {
                    boolean hasType = false;
                    if(type.isChecked()) {
                        for (ShopType companyTypes : shopTypes) {
                            if (companyTypes.toString(context).equals(type.getText())) {
                                hasType = true;
                                break;
                            }
                        }
                        hasAllTypes = hasAllTypes && hasType;
                    }
                }
                if (!hasAllTypes) {
                    continue;
                }
            }

            //organisation (the company must have every organisation)
            boolean isDefaultOrganisation = true;
            for (CheckItem check : organisations) {
                if(check.isChecked()){
                    isDefaultOrganisation = false;
                }
            }
            if(!isDefaultOrganisation) {
                boolean hasAllOrganisations = true;
                List<Organization> companyOrganisations = c.getOrganizations();
                for (CheckItem organisation : organisations) {
                    boolean hasOrganisation = false;
                    if(organisation.isChecked()) {
                        for (Organization companyOrganisation : companyOrganisations) {
                            if (companyOrganisation.getName().equals(organisation.getText())) {
                                hasOrganisation = true;
                                break;
                            }
                        }
                        hasAllOrganisations = hasAllOrganisations && hasOrganisation;
                    }
                }
                if (!hasAllOrganisations) {
                    continue;
                }
            }

            //category (the company must have every category)
            boolean isDefaultCategory = true;
            for (CheckItem check : categories) {
                if (check.isChecked()) {
                    isDefaultCategory = false;
                }
            }
            if (!isDefaultCategory) {
                boolean hasAllCategories = true;
                List<ProductGroup> companyProductGroups = c.getProductGroups();
                List<Category> companyCategories = new ArrayList<>();
                for (ProductGroup p : companyProductGroups) {
                    companyCategories.add(p.getCategory());
                }

                for (CheckItem category : categories) {
                    boolean hasCategory = false;
                    if (category.isChecked()) {
                        for (Category companyCategory : companyCategories) {
                            if (companyCategory.toString(context).equals(category.getText())) {
                                hasCategory = true;
                                break;
                            }
                        }
                        hasAllCategories = hasAllCategories && hasCategory;
                    }
                }
                if (!hasAllCategories) {
                    continue;
                }
            }


            //restrictions (search in all restrictions)
            boolean isDefaultRestrictions = true;
            for (CheckItem r : restrictions) {
                if(r.isChecked()){
                    isDefaultRestrictions = false;
                }
            }


            //default search (only company name and city)
            if(isDefaultRestrictions){
                if (c.getName().toLowerCase().contains(s)) {
                    filterCompaniesSet.add(c);
                }
                if (c.getAddress().getCity().toLowerCase().contains(s)) {
                    filterCompaniesSet.add(c);
                }

            } else {
                for (CheckItem r : restrictions) {
                    if (r.isChecked()) {
                        //company name
                        if (restrictions[0].getText().equals(r.getText())) {
                            if (c.getName().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                        }
                        //name owner
                        else if (restrictions[1].getText().equals(r.getText())) {
                            if (c.getOwner().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                        }
                        //type
                        else if (restrictions[2].getText().equals(r.getText())) {
                            for (ShopType shopType : ShopType.values()) {
                                if (shopType.toString(context).toLowerCase().contains(s)) {
                                    filterCompaniesSet.add(c);
                                    break;
                                }
                            }
                        }
                        //adress
                        else if (restrictions[3].getText().equals(r.getText())) {
                            if (c.getAddress().getCity().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                            if (c.getAddress().getStreet().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                            if (c.getAddress().getZip().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                        }
                        //description companie
                        else if (restrictions[4].getText().equals(r.getText())) {
                            if (c.getDescription().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                        }
                        //description products
                        else if (restrictions[5].getText().equals(r.getText())) {
                            if (c.getProductsDescription().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                        }
                        //product tags
                        else if (restrictions[6].getText().equals(r.getText())) {
                            List<ProductGroup> productGroups = c.getProductGroups();
                            boolean isAdded = false;
                            for (ProductGroup p : productGroups) {
                                for (String tag : p.getProductTags()) {
                                    if (tag.toLowerCase().contains(s)) {
                                        filterCompaniesSet.add(c);
                                        isAdded = true;
                                        break;
                                    }
                                }
                                if (isAdded) break;
                            }
                        }
                        //opening hours comments
                        else if (restrictions[7].getText().equals(r.getText())) {
                            if (c.getOpeningHoursComments().toLowerCase().contains(s)) {
                                filterCompaniesSet.add(c);
                            }
                        }
                        //organisation names
                        else if (restrictions[8].getText().equals(r.getText())) {
                            List<Organization> orgs = c.getOrganizations();
                            for (Organization o : orgs) {
                                if (o.getName().toLowerCase().contains(s)) {
                                    filterCompaniesSet.add(c);
                                    break;
                                }
                            }
                        }
                        //messages
                        else if (restrictions[9].getText().equals(r.getText())) {
                            List<Message> messages = c.getMessages();
                            for (Message m : messages) {
                                if (m.getContent().toLowerCase().contains(s)) {
                                    filterCompaniesSet.add(c);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        // multiple Search
        if (multiple){
            filterCompaniesSet.addAll(filter(rest, companies, types, organisations, categories, restrictions, isDelivery, isOpen, context, weekday, startTime, endTime));
        }
        List<Company> filteredCompanies = new ArrayList<>();
        filteredCompanies.addAll(filterCompaniesSet);
        return filteredCompanies;
    }
}
