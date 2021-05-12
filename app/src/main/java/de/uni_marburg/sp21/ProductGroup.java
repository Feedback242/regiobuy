package de.uni_marburg.sp21;

import java.util.ArrayList;

public class ProductGroup {
    private Category category;
    private boolean rawProd;
    private int producer;
    private ArrayList<String> productTags;
    private ArrayList<Season> seasons;

    public ProductGroup(Category category, int producer) {
        this.category = category;
        this.producer = producer;
    }
}
