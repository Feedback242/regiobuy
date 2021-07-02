package de.uni_marburg.sp21;

import java.io.Serializable;
import java.util.List;

import de.uni_marburg.sp21.company_data_structure.Category;
import de.uni_marburg.sp21.company_data_structure.ProductGroup;

public class Post implements Serializable {

    private List<Category> categories;
    private boolean isChecked;
    private String name;

    public Post(String name, List<Category> categories){
        this.name = name;
        this.categories = categories;
        isChecked = false;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public String getName() {
        return name;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isChecked() {
        return isChecked;
    }
}
