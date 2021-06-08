package de.uni_marburg.sp21.filter;

import android.content.Context;
import android.util.Log;

import com.google.type.DateTime;

import java.sql.SQLOutput;
import java.text.ParseException;
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

    /**
     * @param s the searchTherm
     * @param companies the initial List of Companies that has been downloaded from the database
     * @param types Array of CheckItems for the types.
     * @param organisations Array of CheckItems for the Organisations.
     * @param categories Array of CheckItems for the Categories.
     * @param restrictions Array of CheckItems for the Restrictions (search in).
     * @param isDelivery true if is delivery field has been clicked.
     * @param isOpen true if is open field has been clicked.
     * @param context context of the activity
     * @param weekday weekday string from Calend
     * @param startTime
     * @param endTime
     * @return a filtered list of companies.
     */
    public static List<Company> filter(String s, List<Company> companies, CheckItem[] types, CheckItem[] organisations, CheckItem[] categories, CheckItem[] restrictions, boolean isDelivery, boolean isOpen, Context context, String weekday, Date startTime, Date endTime) {
        //the search is not case sensitive
        s = s.toLowerCase();

        //HashSets doesn't insert duplicates
        HashSet<Company> filterCompaniesSet = new HashSet<>();

        //multiple search
        String[] splitString = s.split(" ");
        boolean multiple;
        String rest = "";
        if (multiple = (splitString.length > 1)){
            rest = s.substring(s.indexOf(" ") + 1);
            s = splitString[0];
        }
        for (Company c : companies) {
            if(isCompanyFulfillingAllFilters(c, types, organisations, categories, isDelivery, isOpen, context, weekday, startTime, endTime)){
                //search in that company
                if(isDefaultSearch(restrictions)){
                    //default search (only company name and city)
                    defaultSearch(s, c, filterCompaniesSet);
                } else {
                    //restrictions (search in all restrictions)
                    searchWithRestrictions(s, restrictions, c, filterCompaniesSet, context);
                }
            }
        }
        // multiple Search recursion
        if (multiple){
            filterCompaniesSet.addAll(filter(rest, companies, types, organisations, categories, restrictions, isDelivery, isOpen, context, weekday, startTime, endTime));
        }
        List<Company> filteredCompanies = new ArrayList<>();
        filteredCompanies.addAll(filterCompaniesSet);
        return filteredCompanies;
    }

    private static boolean isCompanyFulfillingAllFilters(Company company, CheckItem[] types, CheckItem[] organisations, CheckItem[] categories, boolean isDelivery, boolean isOpen, Context context, String weekday, Date startTime, Date endTime){
        //timepicker
        if(!isInTimePickerRange(company, weekday, startTime, endTime)){
            return false;
        }

        //open
        if(isOpen && !filterIsOpen(company)){
            return false;
        }

        //delivery
        if(isDelivery && !company.isDeliveryService()){
            return false;
        }

        //types (the company must have every type)
        if(!filterType(types, company, context)){
            return false;
        }

        //organisation (the company must have every organisation)
        if(!filterOrganisation(organisations, company)){
            return false;
        }

        //category (the company must have every category)
        if(!filterCategory(categories, company, context)){
            return false;
        }
        return true;
    }

    /**
     * @return true if company is open
     */
    private static boolean filterIsOpen(Company company){
            Calendar calendar = Calendar.getInstance();
            Date currentTime = calendar.getTime();
            String dayName = currentWeekdayString();
            ArrayList<Map<String, String>> openingList = company.getOpeningHours().get(dayName);
            boolean isCurrentlyOpen = false;
            if(openingList != null){
                for(Map<String, String> m : openingList){
                    Date start = MainActivity.convertToDate(m.get("start"));
                    Date end = MainActivity.convertToDate(m.get("end"));

                    isCurrentlyOpen = isCurrentlyOpen || (currentTime.after(start) && currentTime.before(end));
                }
            }
            return isCurrentlyOpen;
    }

    /**
     * @return true if the company is open at the timepicker
     */
    private static boolean isInTimePickerRange(Company company, String weekday, Date startTime, Date endTime){
        String chosenWeekDay = "";
        if(weekday.equals("")){
            chosenWeekDay = currentWeekdayString();
        } else {
            try {
                chosenWeekDay = getWeekDayDatabaseStringFromWeekDayFormatString(weekday);
            } catch (ParseException e) {
                e.printStackTrace();
                chosenWeekDay = currentWeekdayString();
            }
        }
        ArrayList<Map<String, String>> openingList = company.getOpeningHours().get(chosenWeekDay);
        boolean isInRange = false;
        if(openingList != null){
            for(Map<String, String> m : openingList){
                Date companyStart = MainActivity.convertToDate(m.get("start"));
                Date companyEnd = MainActivity.convertToDate(m.get("end"));
                if(startTime != null && endTime != null){
                    // isInRange should be false, when the company interval is outside the chosen one
                    isInRange = isInRange || !(endTime.before(companyStart) || startTime.after(companyEnd));
                }
                else if(startTime != null && endTime == null){
                    isInRange = isInRange || startTime.before(companyEnd);
                }
                else if(startTime == null && endTime != null){
                    isInRange = isInRange || endTime.after(companyStart);
                }
                else if(startTime == null && endTime == null){
                    isInRange = true;
                }
            }
        }
        return isInRange;
    }

    /**
     * @return true if company is has all types that are checked or no types are checked
     */
    private static boolean filterType(CheckItem[] types, Company company, Context context){
        boolean isDefaultType = true;
        for (CheckItem check : types) {
            if(check.isChecked()){
                isDefaultType = false;
            }
        }
        if(!isDefaultType) {
            boolean hasAllTypes = true;
            List<ShopType> shopTypes = company.getTypes();
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
            return hasAllTypes;
        }
        return isDefaultType;
    }

    /**
     * @return true if company is has all organisations that are checked or no organisations are checked
     */
    private  static boolean filterOrganisation(CheckItem[] organisations, Company company){
        boolean isDefaultOrganisation = true;
        for (CheckItem check : organisations) {
            if(check.isChecked()){
                isDefaultOrganisation = false;
            }
        }
        if(!isDefaultOrganisation) {
            boolean hasAllOrganisations = true;
            List<Organization> companyOrganisations = company.getOrganizations();
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
            return hasAllOrganisations;
        }
        return isDefaultOrganisation;
    }

    /**
     * @return true if company is has all categories that are checked or no categories are checked
     */
    private static boolean filterCategory(CheckItem[] categories, Company company, Context context){
        //isDefaultCategory is true when no category field has been clicked
        boolean isDefaultCategory = true;
        for (CheckItem check : categories) {
            if (check.isChecked()) {
                isDefaultCategory = false;
            }
        }
        //at least one category has been clicked
        if (!isDefaultCategory) {
            //hasAllCategories is true when the company has all the clicked categories or more
            boolean hasAllCategories = true;
            List<ProductGroup> companyProductGroups = company.getProductGroups();
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
            return hasAllCategories;
        }
        //no category has been clicked
        return true;
    }

    /**
     * @return true if no search-restrictions are checked
     */
    private static boolean isDefaultSearch(CheckItem[] restrictions){
        boolean isDefaultRestrictions = true;
        for (CheckItem r : restrictions) {
            if(r.isChecked()){
                isDefaultRestrictions = false;
            }
        }
        return isDefaultRestrictions;
    }


    private static void searchWithRestrictions(String searchString, CheckItem[] restrictions, Company company, HashSet<Company> filterCompaniesSet, Context context) {
        for (CheckItem r : restrictions) {
            if (r.isChecked()) {
                //company name
                if (restrictions[0].getText().equals(r.getText())) {
                    if (company.getName().toLowerCase().contains(searchString)) {
                        filterCompaniesSet.add(company);
                    }
                }
                //name owner
                else if (restrictions[1].getText().equals(r.getText())) {
                    if (company.getOwner().toLowerCase().contains(searchString)) {
                        filterCompaniesSet.add(company);
                    }
                }
                //type
                else if (restrictions[2].getText().equals(r.getText())) {
                    for (ShopType shopType : ShopType.values()) {
                        if (shopType.toString(context).toLowerCase().contains(searchString)) {
                            filterCompaniesSet.add(company);
                            break;
                        }
                    }
                }
                //adress
                else if (restrictions[3].getText().equals(r.getText())) {
                    if (company.getAddress().getCity().toLowerCase().contains(searchString)) {
                        filterCompaniesSet.add(company);
                    }
                    if (company.getAddress().getStreet().toLowerCase().contains(searchString)) {
                        filterCompaniesSet.add(company);
                    }
                    if (company.getAddress().getZip().toLowerCase().contains(searchString)) {
                        filterCompaniesSet.add(company);
                    }
                }
                //description companie
                else if (restrictions[4].getText().equals(r.getText())) {
                    if (company.getDescription().toLowerCase().contains(searchString)) {
                        filterCompaniesSet.add(company);
                    }
                }
                //description products
                else if (restrictions[5].getText().equals(r.getText())) {
                    if (company.getProductsDescription().toLowerCase().contains(searchString)) {
                        filterCompaniesSet.add(company);
                    }
                }
                //product tags
                else if (restrictions[6].getText().equals(r.getText())) {
                    List<ProductGroup> productGroups = company.getProductGroups();
                    boolean isAdded = false;
                    for (ProductGroup p : productGroups) {
                        for (String tag : p.getProductTags()) {
                            if (tag.toLowerCase().contains(searchString)) {
                                filterCompaniesSet.add(company);
                                isAdded = true;
                                break;
                            }
                        }
                        if (isAdded) break;
                    }
                }
                //opening hours comments
                else if (restrictions[7].getText().equals(r.getText())) {
                    if (company.getOpeningHoursComments().toLowerCase().contains(searchString)) {
                        filterCompaniesSet.add(company);
                    }
                }
                //organisation names
                else if (restrictions[8].getText().equals(r.getText())) {
                    List<Organization> orgs = company.getOrganizations();
                    for (Organization o : orgs) {
                        if (o.getName().toLowerCase().contains(searchString)) {
                            filterCompaniesSet.add(company);
                            break;
                        }
                    }
                }
                //messages
                else if (restrictions[9].getText().equals(r.getText())) {
                    List<Message> messages = company.getMessages();
                    for (Message m : messages) {
                        if (m.getContent().toLowerCase().contains(searchString)) {
                            filterCompaniesSet.add(company);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void defaultSearch(String searchString, Company company, HashSet<Company> filterCompaniesSet){
        if (company.getName().toLowerCase().contains(searchString)) {
            filterCompaniesSet.add(company);
        }
        if (company.getAddress().getCity().toLowerCase().contains(searchString)) {
            filterCompaniesSet.add(company);
        }
    }

    private static String currentWeekdayString(){
        Calendar calendar = Calendar.getInstance();
        String weekday = "";
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day){
            case Calendar.MONDAY: weekday = CompanyActivity.WEEKDAYS[0];
                break;
            case Calendar.TUESDAY: weekday = CompanyActivity.WEEKDAYS[1];
                break;
            case Calendar.WEDNESDAY: weekday = CompanyActivity.WEEKDAYS[2];
                break;
            case Calendar.THURSDAY: weekday = CompanyActivity.WEEKDAYS[3];
                break;
            case Calendar.FRIDAY: weekday = CompanyActivity.WEEKDAYS[4];
                break;
            case Calendar.SATURDAY: weekday = CompanyActivity.WEEKDAYS[5];
                break;
            case Calendar.SUNDAY: weekday = CompanyActivity.WEEKDAYS[6];
                break;
        }
        return weekday;
    }

    private static String getWeekDayDatabaseStringFromWeekDayFormatString(String weekDayFormatString) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("EEE");
        Date date = format.parse(weekDayFormatString);
        calendar.setTime(date);
        String weekday = "";
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day){
            case Calendar.MONDAY: weekday = CompanyActivity.WEEKDAYS[0];
                break;
            case Calendar.TUESDAY: weekday = CompanyActivity.WEEKDAYS[1];
                break;
            case Calendar.WEDNESDAY: weekday = CompanyActivity.WEEKDAYS[2];
                break;
            case Calendar.THURSDAY: weekday = CompanyActivity.WEEKDAYS[3];
                break;
            case Calendar.FRIDAY: weekday = CompanyActivity.WEEKDAYS[4];
                break;
            case Calendar.SATURDAY: weekday = CompanyActivity.WEEKDAYS[5];
                break;
            case Calendar.SUNDAY: weekday = CompanyActivity.WEEKDAYS[6];
                break;
        }
        return weekday;
    }
}
