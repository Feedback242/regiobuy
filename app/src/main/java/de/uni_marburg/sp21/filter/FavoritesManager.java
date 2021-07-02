package de.uni_marburg.sp21.filter;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.uni_marburg.sp21.MyApplication;
import de.uni_marburg.sp21.company_data_structure.Company;
import de.uni_marburg.sp21.filter.FavoriteItem;

public class FavoritesManager {
    private static final String FAVORITES_IDS = "fav.ser";
    private final List<Company> companies;

    public FavoritesManager(List<Company> companies) {
        this.companies = companies;
        loadFavoriteItems();
    }

    /**
     * Saves Favorites
     */
    public void save(Company company){
        File path = MyApplication.getAppContext().getExternalFilesDir(null);
        File file = new File(path, FAVORITES_IDS);

        List<FavoriteItem> favoriteItems = new ArrayList<>();
        for (Company c : companies) {
            if(company.getID() == c.getID()){
                c.setFavorite(company.isFavorite());
            }
            favoriteItems.add(new FavoriteItem(c.getID(), c.isFavorite()));
        }

        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(favoriteItems);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void loadFavoriteItems(){
        File path = MyApplication.getAppContext().getExternalFilesDir(null);
        File file = new File(path, FAVORITES_IDS);
        List<FavoriteItem> favoriteItems;
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            favoriteItems = (List<FavoriteItem>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            favoriteItems = new ArrayList<>();
        }
        for(FavoriteItem f : favoriteItems){
            for(Company c : companies){
                if(f.getID().equals(c.getID())){
                    c.setFavorite(f.isFavorite());
                    Log.d(MyApplication.APP_TAG, "Favorite loaded! Is Favorite:" + c.isFavorite() + " Company:" + c.getName());
                }
            }
        }
    }
}
