package de.uni_marburg.sp21.company_data_structure;

import java.io.Serializable;

public class FavoriteItem implements Serializable {
    private String id;
    private boolean isFavorite;

    public FavoriteItem(String id, boolean isFavorite){
        this.id = id;
        this.isFavorite = isFavorite;
    }

    //------------------ GET / SET -------------------

    public String getID() {
        return id;
    }

    public boolean isFavorite() {
        return isFavorite;
    }
}