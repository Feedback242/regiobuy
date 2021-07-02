package de.uni_marburg.sp21;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.uni_marburg.sp21.company_data_structure.Category;
import de.uni_marburg.sp21.company_data_structure.Company;
import de.uni_marburg.sp21.company_data_structure.ProductGroup;
import de.uni_marburg.sp21.filter.CheckItem;
import de.uni_marburg.sp21.filter.FilterAdapter;

public class BottomSheetShoppingList extends BottomSheetDialogFragment {

    private final static String POSTS_FILENAME = "Posts.ser";

    private List<Post> posts;
    private CheckItem[] categories;

    //RecyclerViewPosts
    private ShoppingListAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    //RecyclerViewProductGroups
    private FilterAdapter adapterCategory;
    private RecyclerView recyclerViewCategory;
    private RecyclerView.LayoutManager layoutManagerCategory;
    boolean isCategoriesOpen = true;

    //New Post
    private ImageView add;
    private ImageView configureProductCategory;
    private EditText addText;

    private View itemView;

    public BottomSheetShoppingList(){
        posts = new ArrayList<>();
        load();
        categories = Category.createCheckItemArray();
     }

     private void buildAddPostsView(){
         add = itemView.findViewById(R.id.addButton);
         configureProductCategory = itemView.findViewById(R.id.productGroupsConfig);
         addText = itemView.findViewById(R.id.addText);

         configureProductCategory.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 isCategoriesOpen = !isCategoriesOpen;
                 if(isCategoriesOpen){
                     recyclerViewCategory.setVisibility(View.VISIBLE);
                     configureProductCategory.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
                     adapterCategory.notifyDataSetChanged();
                 } else {
                     recyclerViewCategory.setVisibility(View.GONE);
                     configureProductCategory.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
                     adapterCategory.notifyDataSetChanged();
                 }
             }
         });

         add.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 List<Category> cats = new ArrayList<>();
                 String postName = addText.getText().toString();
                 if(!postName.isEmpty()){
                     for (CheckItem checkItem : categories) {
                         for (Category category : Category.values()) {
                             if (checkItem.isChecked() && category.toString().equals(checkItem.getText())) {
                                 cats.add(category);
                             }
                         }
                     }
                     posts.add(new Post(addText.getText().toString(), cats));
                     adapter.notifyItemInserted(posts.size());
                     clearCategories();
                     adapterCategory.notifyDataSetChanged();
                 }
                 addText.setText("");
             }
         });
     }

     private void buildRecyclerViewCategories(){
         adapterCategory = new FilterAdapter(categories);
         recyclerViewCategory = itemView.findViewById(R.id.shoppingCategories);
         layoutManagerCategory = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
         recyclerViewCategory.setAdapter(adapterCategory);
         recyclerViewCategory.setLayoutManager(layoutManagerCategory);
         adapterCategory.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {
             @Override
             public void onItemClick(int position, boolean isChecked) {
                 categories[position].check(isChecked);
             }
         });
     }

    private void buildRecyclerView(){
        adapter = new ShoppingListAdapter(posts);
        recyclerView = itemView.findViewById(R.id.shoppingRecyclerView);
        layoutManager = new LinearLayoutManager(MyApplication.getAppContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        adapter.setListener(new ShoppingListAdapter.OnPostClickListener() {
            @Override
            public void onSearchClick(int pos) {

            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemView = inflater.inflate(R.layout.shopping_list, container, false);
        return itemView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //this expands the bottom sheet even after a config change
        BottomSheetBehavior.from((View) itemView.getParent()).setState(BottomSheetBehavior.STATE_EXPANDED);
        buildRecyclerView();
        buildAddPostsView();
        buildRecyclerViewCategories();
    }

    @Override
    public void onStop() {
        super.onStop();
        save();
    }

    private void clearCategories(){
        for(CheckItem c : categories){
            c.check(false);
            adapterCategory.notifyDataSetChanged();
        }
    }
    private void save(){
        File path = MyApplication.getAppContext().getExternalFilesDir(null);
        File file = new File(path, POSTS_FILENAME);
        List<Post> temp = new ArrayList<>();
        for (Post p : posts){
            if(!p.isChecked()){
                temp.add(p);
            }
        }
        try{
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(temp);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void load() {
        File path = MyApplication.getAppContext().getExternalFilesDir(null);
        File file = new File(path, POSTS_FILENAME);
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            posts = (List<Post>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
    }
}
