package de.uni_marburg.sp21.data_structure;

import java.util.ArrayList;

public class ProductGroup {
    private Category category;
    private boolean rawProd;
    private Double producer;
    private ArrayList<String> productTags;
    private ArrayList<Season> seasons;

    public void setRawProd(boolean rawProd) {
        this.rawProd = rawProd;
    }

    public void setProductTags(ArrayList<String> productTags) {
        this.productTags = productTags;
    }

    public void setSeasons(ArrayList<String> seasons) {
     //TODO: Convert Strings to Seasons
        //   this.seasons = seasons;
    }

    public ProductGroup(String category, Double producer) {
        //TODO: String to category
     //   this.category = category;
        this.producer = producer;
    }
}
