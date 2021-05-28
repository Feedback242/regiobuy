package de.uni_marburg.sp21.filter;

import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.uni_marburg.sp21.data_structure.Category;
import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.Message;
import de.uni_marburg.sp21.data_structure.Organization;
import de.uni_marburg.sp21.data_structure.ProductGroup;
import de.uni_marburg.sp21.data_structure.ShopType;
import de.uni_marburg.sp21.data_structure.TimeInterval;
import de.uni_marburg.sp21.filter.CheckItem;

public class Filter {

    public static List<Company> filter(String s, List<Company> companies, CheckItem[] types, CheckItem[] organisations, CheckItem[] categories, CheckItem[] restrictions, boolean isDelivery, boolean isOpen) {
        //HashSets doesn't insert duplicates
        s = s.toLowerCase();
        HashSet<Company> filterCompaniesSet = new HashSet<>();

        for (Company c : companies) {
            System.out.println(c.getOpeningHours().values());

           /* //time
            if(TimeInterval.getStart() != null && TimeInterval.getEnd() !=null && TimeInterval.getDate() != null){

                Time companyStartTime = Time.valueOf(c.getOpeningHours().get(TimeInterval.getDate()).get("start"));
                Time companyEndTime = Time.valueOf(c.getOpeningHours().get(TimeInterval.getDate()).get("end"));
                java.sql.Time selectedStartTime = TimeInterval.getStart();
                Time selectedEndTime = TimeInterval.getEnd();
                if( companyStartTime.compareTo(selectedStartTime) < 0){
                    continue;
                }else if(companyEndTime.compareTo(selectedEndTime) < 0 ) {
                    continue;
                }
            }

            */
            //isOpen
            if(isOpen){

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
                            if (companyTypes.toString().equals(type.getText())) {
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
                            if (companyCategory.toString().equals(category.getText())) {
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
                                if (shopType.toString().toLowerCase().contains(s)) {
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
        List<Company> filteredCompanies = new ArrayList<>();
        filteredCompanies.addAll(filterCompaniesSet);
        return filteredCompanies;
    }
}
