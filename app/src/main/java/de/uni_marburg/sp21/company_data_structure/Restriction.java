package de.uni_marburg.sp21.company_data_structure;

import android.content.res.Resources;

import de.uni_marburg.sp21.MyApplication;
import de.uni_marburg.sp21.R;
import de.uni_marburg.sp21.filter.CheckItem;

public enum Restriction {
    NAME_COMPANY,
    NAME_OWNER,
    SHOP_TYPES,
    ADDRESS,
    DESCRIPTION_COMPANY,
    DESCRIPTION_PRODUCT,
    PRODUCT_TAGS,
    OPENING_HOURS_COMMENT,
    NAME_ORGANIZATION,
    MESSAGES_COMPANY;


    /**
     * Creates a Array of CheckItems from all Restrictions
     * @return Array of CheckItems that represent all Restrictions
     */
    public static CheckItem[] createCheckItemArray(){
        CheckItem[] checkItem = new CheckItem[Restriction.values().length];
        for (int i = 0; i < checkItem.length; i++){
            checkItem[i] = new CheckItem(Restriction.values()[i].toString());
        }
        return checkItem;
    }

    @Override
    public String toString() {
        Resources res = MyApplication.getAppContext().getResources();
        switch (this){
            case NAME_COMPANY: return res.getString(R.string.name_company);
            case NAME_OWNER: return res.getString(R.string.name_owner);
            case SHOP_TYPES: return res.getString(R.string.shop_types_without_colon);
            case ADDRESS: return res.getString(R.string.address);
            case DESCRIPTION_COMPANY: return res.getString(R.string.description_company);
            case DESCRIPTION_PRODUCT: return res.getString(R.string.description_products);
            case PRODUCT_TAGS: return res.getString(R.string.product_tags);
            case OPENING_HOURS_COMMENT: return res.getString(R.string.opening_hours_comment);
            case NAME_ORGANIZATION: return res.getString(R.string.name_organisation);
            case MESSAGES_COMPANY: return res.getString(R.string.messages_company);
            default: return "";
        }
    }

    /**
     * Generates a Category from the String, that has been passed from the Database
     * @param s the Database-String
     * @return the Category representation of the Database-String
     */
    public static Restriction fromDatabaseString(String s) {
        switch (s) {
            case "Name of company": return NAME_COMPANY;
            case "Name of owner": return NAME_OWNER;
            case "Shop types": return SHOP_TYPES;
            case "Address": return ADDRESS;
            case "Company description": return DESCRIPTION_COMPANY;
            case "Product description": return DESCRIPTION_PRODUCT;
            case "Product group tags": return PRODUCT_TAGS;
            case "Opening hours comment": return OPENING_HOURS_COMMENT;
            case "Name of organizations": return NAME_ORGANIZATION;
            case "Messages of company": return MESSAGES_COMPANY;
            default: return NAME_COMPANY;
        }
    }
}
