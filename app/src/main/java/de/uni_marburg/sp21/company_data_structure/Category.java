package de.uni_marburg.sp21.company_data_structure;

import android.content.res.Resources;

import java.io.Serializable;

import de.uni_marburg.sp21.MyApplication;
import de.uni_marburg.sp21.R;
import de.uni_marburg.sp21.filter.CheckItem;

public enum Category implements Serializable {
    VEGETABLES,
    FRUITS,
    MEAT,
    MEAT_PRODUCTS,
    CEREALS,
    MILK,
    MILK_PRODUCTS,
    EGGS,
    HONEY,
    BEVERAGES,
    BAKED_GOODS,
    PASTA;


    /**
     * Creates a Array of CheckItems from all Categories
     * @return Array of CheckItems that represent all Categories
     */
    public static CheckItem[] createCheckItemArray(){
     CheckItem[] checkItem = new CheckItem[Category.values().length];
     for (int i = 0; i < checkItem.length; i++){
         checkItem[i] = new CheckItem(Category.values()[i].toString());
     }
     return checkItem;
    }

    @Override
    public String toString() {
        Resources res = MyApplication.getAppContext().getResources();
        switch (this){
            case EGGS: return res.getString(R.string.eggs);
            case MEAT: return res.getString(R.string.meat);
            case MILK: return res.getString(R.string.milk);
            case HONEY: return res.getString(R.string.honey);
            case PASTA: return res.getString(R.string.pasta);
            case FRUITS: return res.getString(R.string.fruits);
            case CEREALS: return res.getString(R.string.cereals);
            case BEVERAGES: return res.getString(R.string.beverages);
            case VEGETABLES: return res.getString(R.string.vegetables);
            case BAKED_GOODS: return res.getString(R.string.baked_goods);
            case MEAT_PRODUCTS: return res.getString(R.string.meat_products);
            case MILK_PRODUCTS: return res.getString(R.string.milk_products);
            default: return "";
        }
    }

    /**
     * Generates a Category from the String, that has been passed from the Database
     * @param s the Database-String
     * @return the Category representation of the Database-String
     */
    public static Category fromDatabaseString(String s) {
        switch (s) {
            case "vegetables": return VEGETABLES;
            case "fruits": return FRUITS;
            case "meat": return MEAT;
            case "meatproducts": return MEAT_PRODUCTS;
            case "cereals": return CEREALS;
            case "milk": return MILK;
            case "milkproducts": return MILK_PRODUCTS;
            case "eggs": return EGGS;
            case "honey": return HONEY;
            case "beverages": return BEVERAGES;
            case "bakedgoods": return PASTA;
            default: return PASTA;
        }
    }
}
