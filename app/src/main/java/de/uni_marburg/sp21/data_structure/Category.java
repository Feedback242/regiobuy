package de.uni_marburg.sp21.data_structure;

import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.NonNull;

import java.io.Serializable;

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


    public static CheckItem[] createCheckItemArray(Context context){
     CheckItem[] checkItem = new CheckItem[Category.values().length];
     for (int i = 0; i < checkItem.length; i++){
         checkItem[i] = new CheckItem(Category.values()[i].toString(context));
     }
     return checkItem;
    }

    public String toString(Context context) {
        Resources res = context.getResources();
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
