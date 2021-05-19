package de.uni_marburg.sp21.filter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import de.uni_marburg.sp21.R;

public class BottomSheetFilter extends BottomSheetDialogFragment {

    private View itemView;
    private Context context;
    private OnItemClickListener listener;

    //categoryRV (ProduktKategorien)
    private RecyclerView recyclerViewCategories;
    private FilterAdapter adapterCategories;
    private CheckItem[] CATEGORIES;

    //typeRV (Betriebsarten)
    private RecyclerView recyclerViewTypes;
    private FilterAdapter adapterTypes;
    private CheckItem[] TYPES;

    //organisationRV (Organisationen)
    private RecyclerView recyclerViewOrganisations;
    private FilterAdapter adapterOrganisations;
    private CheckItem[] ORGANISATIONS;

    private ImageView timePickerStart;
    private ImageView timePickerEnd;
    private ImageView timePickerDate;

    private TextView startTime;
    private TextView endTime;
    private TextView dateTime;

    private View isDelivery;
    private View isOpened;
    private ImageView deliveryCheckbox;
    private ImageView openedCheckbox;

    private boolean isCheckedOpen;
    private boolean isCheckedDelivery;

    public BottomSheetFilter(Context context, final CheckItem[] ORGANIZATIONS, final CheckItem[] CATEGORIES, final CheckItem[] TYPES){
        this.context = context;
        this.ORGANISATIONS = ORGANISATIONS;
        this.TYPES = TYPES;
        this.CATEGORIES = CATEGORIES;
    }

    /**
     * builds the three RecyclerViews and sets some onClicks
     */
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

    private void buildTimePicker(){
        timePickerStart = itemView.findViewById(R.id.timePickerStart);
        timePickerEnd = itemView.findViewById(R.id.timePickerEnd);
        timePickerDate = itemView.findViewById(R.id.timePickerDate);

        startTime = itemView.findViewById(R.id.startTime);
        endTime = itemView.findViewById(R.id.endTime);
        dateTime = itemView.findViewById(R.id.dateTime);

        Calendar calendar = Calendar.getInstance();

        timePickerStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    Calendar calendar = Calendar.getInstance();
                    int hours = calendar.get(Calendar.HOUR_OF_DAY);
                    int mins = calendar.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String time = hourOfDay + ":" + minute;
                            startTime.setText(time);
                        }
                    }, hours, mins, true);
                    timePickerDialog.show();
                }
            }
        });

        timePickerEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    Calendar calendar = Calendar.getInstance();
                    int hours = calendar.get(Calendar.HOUR_OF_DAY);
                    int mins = calendar.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            String time = hourOfDay + ":" + minute;
                            endTime.setText(time);
                        }

                    }, hours, mins, true);
                    timePickerDialog.show();
                }
            }
        });
        timePickerDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat format = new SimpleDateFormat("EEE");
                        c.set(year, month, dayOfMonth);
                        String time = format.format(c.getTime());
                        dateTime.setText(time);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void onClicks(){
        isDelivery = itemView.findViewById(R.id.isDelivery);
        isOpened = itemView.findViewById(R.id.isOpened);
        deliveryCheckbox = itemView.findViewById(R.id.deliveryCheckbox);
        openedCheckbox = itemView.findViewById(R.id.openedCheckbox);
        isCheckedDelivery = false;
        isCheckedOpen = false;

        isDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheckedDelivery = !isCheckedDelivery;
                if (isCheckedDelivery){
                    deliveryCheckbox.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
                }else {
                    deliveryCheckbox.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
                }
            }
        });

        isOpened.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheckedOpen = !isCheckedOpen;
                if (isCheckedOpen){
                    openedCheckbox.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
                }else {
                    openedCheckbox.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
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

    @Override
    public void onStart() {
        super.onStart();
        //this expands the bottom sheet even after a config change
        BottomSheetBehavior.from((View) itemView.getParent()).setState(BottomSheetBehavior.STATE_EXPANDED);
        buildRecyclerView();
        buildTimePicker();
        onClicks();
    }

    /**
     * Sets the listener
     * @param listener an instance of the Interface below, to pass values
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * This interface is for passing values to the MainActivity
     */
    public interface OnItemClickListener{
        void onOrganisationClick(int position, boolean isChecked);
        void onTypeClick(int position, boolean isChecked);
        void onCategoryClick(int position, boolean isChecked);
        void onTimeStartChanged(String time);
        void onTimeEndChanged(String time);
        void onTimeDateChanged(String time);
        void onDeliveryClick(boolean isDelivery);
        void onOpenedClick(boolean isOpen);
    }
}
