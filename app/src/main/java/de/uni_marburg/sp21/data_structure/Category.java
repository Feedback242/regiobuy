package de.uni_marburg.sp21.data_structure;

import androidx.annotation.NonNull;

import java.io.Serializable;

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


    public static CheckItem[] createCheckItemArray(){
     CheckItem[] checkItem = new CheckItem[Category.values().length];
     for (int i = 0; i < checkItem.length; i++){
         checkItem[i] = new CheckItem(Category.values()[i].toString());
     }
     return checkItem;
    }

    @NonNull
    @Override
    public String toString() {
        switch (this){
            case EGGS: return "Eier";
            case MEAT: return "Fleisch";
            case MILK: return "Milch";
            case HONEY: return "Honig";
            case PASTA: return "Pasta";
            case FRUITS: return "Obst";
            case CEREALS: return "Getreide";
            case BEVERAGES: return "Getränke";
            case VEGETABLES: return "Gemüse";
            case BAKED_GOODS: return "Backwaren";
            case MEAT_PRODUCTS: return "Fleischprodukte";
            case MILK_PRODUCTS: return "Milchprodukte";
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
