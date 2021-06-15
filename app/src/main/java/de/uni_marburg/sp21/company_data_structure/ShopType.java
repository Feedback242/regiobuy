package de.uni_marburg.sp21.company_data_structure;

import android.content.res.Resources;

import java.io.Serializable;

import de.uni_marburg.sp21.MyApplication;
import de.uni_marburg.sp21.R;
import de.uni_marburg.sp21.filter.CheckItem;

public enum ShopType implements Serializable {
    PRODUCER,
    SHOP,
    RESTAURANT,
    HOTEL,
    MART;

    /**
     * Creates a Array of CheckItems from all ShopType
     * @return Array of CheckItems that represent all ShopType
     */
    public static CheckItem[] createCheckItemArray() {
        CheckItem[] checkItem = new CheckItem[ShopType.values().length];
        for (int i = 0; i < checkItem.length; i++) {
            checkItem[i] = new CheckItem(ShopType.values()[i].toString());
        }
        return checkItem;
    }

    @Override
    public String toString() {
        Resources res = MyApplication.getAppContext().getResources();
        switch (this) {
            case MART:
                return res.getString(R.string.mart);
            case SHOP:
                return res.getString(R.string.shop);
            case HOTEL:
                return res.getString(R.string.hotel);
            case PRODUCER:
                return res.getString(R.string.producer);
            case RESTAURANT:
                return res.getString(R.string.restaurant);
            default:
                return "";
        }
    }

    /**
     * Generates a ShopType from the String, that has been passed from the Database
     * @param s the Database-String
     * @return the ShopType representation of the Database-String
     */
    public static ShopType fromDatabaseString(String s) {
        switch (s) {
            case "producer":
                return PRODUCER;
            case "mart":
                return MART;
            case "restaurant":
                return RESTAURANT;
            case "hotel":
                return HOTEL;
            default:
                return SHOP;
        }
    }
}