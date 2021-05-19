package de.uni_marburg.sp21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.firestore.FirebaseFirestore;

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

    private CompanyAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private ImageView filterButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseFirestore.getInstance();
        DataBaseManager.getCompanyList(database);
        filterButton = findViewById(R.id.filterButton);

      //  createTestList();

     //   buildRecyclerView();
     //   buildFilter();


    }

    private void buildFilter(){
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Array aller organizations übergeben
                BottomSheetFilter settingsDialog = new BottomSheetFilter(MainActivity.this, new CheckItem[]{new CheckItem("test")}, Category.createCheckItemList(), ShopType.createCheckItemList());
                settingsDialog.show(getSupportFragmentManager(), "SETTINGS_SHEET");
                settingsDialog.setOnItemClickListener(new BottomSheetFilter.OnItemClickListener() {
                    @Override
                    public void onOrganisationClick(int position, boolean isChecked) {
                        //TODO
                    }

                    @Override
                    public void onTypeClick(int position, boolean isChecked) {
                        //TODO
                    }

                    @Override
                    public void onCategoryClick(int position, boolean isChecked) {
                        //TODO
                    }
                });
            }
        });
    }

    private void buildRecyclerView(){
        recyclerView = findViewById(R.id.recyclerView);
        adapter = new CompanyAdapter(companies);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

 //   private void createTestList(){
 //       companies = new ArrayList<>(Arrays.asList(new Company("name", 1, new Address("Gladenbach", "Bahnhofstraße 1", "35075"), "geoHash")));
 //   }
}