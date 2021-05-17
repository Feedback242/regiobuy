package de.uni_marburg.sp21.filter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

import de.uni_marburg.sp21.CompanyAdapter;
import de.uni_marburg.sp21.R;
import de.uni_marburg.sp21.data_structure.Category;
import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.Organization;
import de.uni_marburg.sp21.data_structure.ShopTypes;

public class BottomSheetFilter extends BottomSheetDialogFragment {

    //View that gets created by onCreateView
    private View itemView;

    private Context context;

    //categoryRV (ProduktKategorien)
    private RecyclerView recyclerViewCategories;
    private FilterAdapter adapterCategories;
    //typeRV (Betriebsarten)
    private RecyclerView recyclerViewTypes;
    private FilterAdapter adapterTypes;
    //organisationRV (Organisationen)
    private RecyclerView recyclerViewOrganisations;
    private FilterAdapter adapterOrganisations;

    private CheckItem[] CATEGORIES;
    private CheckItem[] TYPES;
    private CheckItem[] ORGANISATIONS;
    private OnItemClickListener listener;

    public BottomSheetFilter(Context context, final CheckItem[] ORGANIZATIONS, final CheckItem[] CATEGORIES, final CheckItem[] TYPES){
        this.context = context;
        this.ORGANISATIONS = ORGANISATIONS;
        this.TYPES = TYPES;
        this.CATEGORIES = CATEGORIES;
    }

    @Override
    public void onStart() {
        super.onStart();
        //this expands the bottom sheet even after a config change
        BottomSheetBehavior.from((View) itemView.getParent()).setState(BottomSheetBehavior.STATE_EXPANDED);
        buildRecyclerView();
    }

    private void buildRecyclerView(){
        recyclerViewCategories = itemView.findViewById(R.id.categoryRV);
        recyclerViewTypes = itemView.findViewById(R.id.typesRV);
        recyclerViewOrganisations = itemView.findViewById(R.id.organisationRV);

        adapterCategories = new FilterAdapter(CATEGORIES);
        adapterTypes = new FilterAdapter(TYPES);
        adapterOrganisations = new FilterAdapter(new CheckItem[]{new CheckItem("name")});

        RecyclerView.LayoutManager layoutManagerCategory = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager layoutManagerTypes = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.LayoutManager layoutManagerOrganisations = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);

        recyclerViewCategories.setLayoutManager(layoutManagerCategory);
        recyclerViewTypes.setLayoutManager(layoutManagerTypes);
        recyclerViewOrganisations.setLayoutManager(layoutManagerOrganisations);

        recyclerViewCategories.setAdapter(adapterCategories);
        recyclerViewTypes.setAdapter(adapterTypes);
        recyclerViewOrganisations.setAdapter(adapterOrganisations);

        adapterCategories.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isChecked) {
                if(listener != null) {
                    listener.onCategoryClick(position, isChecked);
                }
            }
        });

        adapterTypes.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isChecked) {
                if(listener != null) {
                    listener.onTypeClick(position, isChecked);
                }
            }
        });

        adapterOrganisations.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isChecked) {
                if(listener != null) {
                    listener.onOrganisationClick(position, isChecked);
                }
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
        itemView = inflater.inflate(R.layout.filter_bottom_sheet, container, false);
        return itemView;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onOrganisationClick(int position, boolean isChecked);
        void onTypeClick(int position, boolean isChecked);
        void onCategoryClick(int position, boolean isChecked);
    }
}
