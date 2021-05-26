package de.uni_marburg.sp21.data_structure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProductGroup implements Serializable {
    private Category category;
    private boolean rawProd;
    private Double producer;
    private List<String> productTags;
    private List<Season> seasons;

    public void setRawProd(boolean rawProd) {
        this.rawProd = rawProd;
    }

    public void setProductTags(List<String> productTags) {
        this.productTags = productTags;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public Category getCategory() {
        return category;
    }

    public ProductGroup(Category category, Double producer) {
        this.category = category;
        this.producer = producer;
    }
}
