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


    public static CheckItem[] createCheckItemList(){
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
}
