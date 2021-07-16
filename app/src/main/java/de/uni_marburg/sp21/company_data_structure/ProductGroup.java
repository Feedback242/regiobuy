package de.uni_marburg.sp21.company_data_structure;

import java.io.Serializable;
import java.util.List;

public class ProductGroup implements Serializable {
    private Category category;
    private boolean isRawProduct;
    private double producer;
    private List<String> productTags;
    private List<Season> seasons;

    /**
     * Constructor
     * @param category The Category of the ProductGroup
     * @param producer The Producer of the ProductGroup represented by a double
     */
    public ProductGroup(Category category, double producer) {
        this.category = category;
        this.producer = producer;
    }

    //------------------ GET / SET -------------------

    public void setRawProd(boolean rawProd) {
        this.isRawProduct = rawProd;
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

    public List<String> getProductTags() {
        return productTags;
    }
}
