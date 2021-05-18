package de.uni_marburg.sp21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.uni_marburg.sp21.data_structure.Address;
import de.uni_marburg.sp21.data_structure.Category;
import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.ShopTypes;
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

        createTestList();

        buildRecyclerView();
        buildFilter();

    }

    /**
     * builds the Filter BottomSheetFragment, when clicked on the filterButton
     */
    private void buildFilter(){
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Array aller organizations übergeben
                BottomSheetFilter settingsDialog = new BottomSheetFilter(MainActivity.this, new CheckItem[]{new CheckItem("test")}, Category.createCheckItemList(), ShopTypes.createCheckItemList());
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
        adapter = new CompanyAdapter(companies);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    /**
     * creates a test list, with one company
     */
    private void createTestList(){
        companies = new ArrayList<>(Arrays.asList(new Company("name", 1, new Address("Gladenbach", "Bahnhofstraße 1", "35075"), "geoHash"),
                new Company("name", 1, new Address("Gladenbach", "Bahnhofstraße 1", "35075"), "geoHash")));
    }
}