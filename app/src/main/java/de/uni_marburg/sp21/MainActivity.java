package de.uni_marburg.sp21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import de.uni_marburg.sp21.data_structure.Category;
import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.ShopType;
import de.uni_marburg.sp21.filter.BottomSheetFilter;
import de.uni_marburg.sp21.filter.CheckItem;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "REGIO";
    private FirebaseFirestore database;

    public List<Company> companies;
    public List<Company> filteredCompanies;

    private CompanyAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ImageView filterButton;
    private SearchView searchView;

    private CheckItem[] categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);

        database = FirebaseFirestore.getInstance();
        companies = DataBaseManager.getCompanyList(database, MainActivity.this);
        filteredCompanies = new ArrayList<>(companies);

        filterButton = findViewById(R.id.filterButton);
        buildRecyclerView();
        buildFilter();
        Button button = findViewById(R.id.testButton);

        categories = Category.createCheckItemArray();
        //TODO for other checkitem arrays..

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = searchView.getQuery().toString();
                filter(string);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!(newText == null || newText.length()== 0 )){
                    filter(newText);
                }
                return false;
            }

        });

    }

    private void filter(String s){
        filteredCompanies.clear();
        for(Company c : companies){
            if(c.getName().toLowerCase().contains(s.toLowerCase())){
                filteredCompanies.add(c);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * builds the Filter BottomSheetFragment, when clicked on the filterButton
     */
    private void buildFilter(){
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Array aller organizations übergeben
                BottomSheetFilter settingsDialog = new BottomSheetFilter(MainActivity.this, new CheckItem[]{new CheckItem("test")}, categories, ShopType.createCheckItemArray());
                settingsDialog.show(getSupportFragmentManager(), "SETTINGS_SHEET");
                settingsDialog.setOnItemClickListener(new BottomSheetFilter.OnItemClickListener() {
                    @Override
                    public void onOrganisationClick(int position, boolean isChecked) {
                    }

                    @Override
                    public void onTypeClick(int position, boolean isChecked) {
                    }

                    @Override
                    public void onCategoryClick(int position, boolean isChecked) {
                        categories[position].check();
                    }

                    @Override
                    public void onTimeStartChanged(String time) {

                    }

                    @Override
                    public void onTimeEndChanged(String time) {

                    }

                    @Override
                    public void onTimeDateChanged(String time) {

                    }

                    @Override
                    public void onDeliveryClick(boolean isDelivery) {

                    }

                    @Override
                    public void onOpenedClick(boolean isOpen) {

                    }
                });
            }
        });
    }

    /**
     * builds the main RecyclerView
     */
    private void buildRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new CompanyAdapter(filteredCompanies);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}