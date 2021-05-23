package de.uni_marburg.sp21.data_structure;

import androidx.annotation.NonNull;

import java.io.Serializable;

import de.uni_marburg.sp21.filter.CheckItem;

public enum ShopType implements Serializable {
    PRODUCER,
    SHOP,
    RESTAURANT,
    HOTEL,
    MART;

    public static CheckItem[] createCheckItemList(){
        CheckItem[] checkItem = new CheckItem[ShopType.values().length];
        for (int i = 0; i < checkItem.length; i++){
            checkItem[i] = new CheckItem(ShopType.values()[i].toString());
        }
        return checkItem;
    }

    @NonNull
    @Override
    public String toString() {
        switch (this){
            case MART: return "Markt";
            case SHOP: return "Geschäft";
            case HOTEL: return "Hotel";
            case PRODUCER: return "Direktproduzent";
            case RESTAURANT: return "Restaurant";
            default: return "";
        }
    }

    public static ShopType valueOF(String name){
        switch (name){
            case "Markt": return MART;
            case "Geschäft": return SHOP;
            case "Hotel": return HOTEL ;
            case "Direktproduzent": return PRODUCER;
            case "Restaurant": return RESTAURANT;
            default: return null;
        }
    }


}
