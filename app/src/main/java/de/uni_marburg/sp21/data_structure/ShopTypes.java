package de.uni_marburg.sp21.data_structure;

import java.io.Serializable;
import java.util.ArrayList;

public class ShopTypes implements Serializable {
    private ArrayList<ShopType> shopTypes;

    public ShopTypes(ArrayList<String> shopTypes) {
        //TODO: Strings to ShopTypes
    }

    public ArrayList<ShopType> getShopTypes() {
        return shopTypes;
    }
}
