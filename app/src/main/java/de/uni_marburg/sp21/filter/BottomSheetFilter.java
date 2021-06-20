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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import de.uni_marburg.sp21.MyApplication;
import de.uni_marburg.sp21.R;

public class BottomSheetFilter extends BottomSheetDialogFragment {

    private View itemView;
    private Context context;
    private OnItemClickListener listener;

    //categoryRV (ProduktKategorien)
    private RecyclerView recyclerViewCategories;
    private FilterAdapter adapterCategories;
    private static CheckItem[] CATEGORIES;

    //typeRV (Betriebsarten)
    private RecyclerView recyclerViewTypes;
    private FilterAdapter adapterTypes;
    private static CheckItem[] TYPES;

    //organisationRV (Organisationen)
    private RecyclerView recyclerViewOrganisations;
    private FilterAdapter adapterOrganisations;
    private static CheckItem[] ORGANISATIONS;

    //restrictionRV (Einschränkungen)
    private RecyclerView recyclerViewRestrictions;
    private FilterAdapter adapterRestrictions;
    private static CheckItem[] RESTRICTIONS;

    private ImageView timePickerStart;
    private ImageView timePickerEnd;
    private ImageView timePickerDate;
    private ImageView resetPicker;

    private TextView startTime;
    private TextView endTime;
    private TextView dateTime;

    private PickedTime pickedTime;

    private View isDelivery;
    private View isOpened;
    private ImageView deliveryCheckbox;
    private ImageView openedCheckbox;

    private boolean isCheckedOpen;
    private boolean isCheckedDelivery;

    public BottomSheetFilter(Context context, final CheckItem[] ORGANIZATIONS, final CheckItem[] CATEGORIES, final CheckItem[] TYPES, final CheckItem[] RESTRICTIONS, boolean isDelivery, boolean isOpen, PickedTime pickedTime){
        this.context = context;
        this.ORGANISATIONS = ORGANIZATIONS;
        this.TYPES = TYPES;
        this.CATEGORIES = CATEGORIES;
        this.RESTRICTIONS = RESTRICTIONS;
        isCheckedOpen = isOpen;
        isCheckedDelivery = isDelivery;
        this.pickedTime = pickedTime;
    }

    private void buildRecyclerView(int recyclerViewID, RecyclerView recyclerView, FilterAdapter adapter, CheckItem[] checkItems){
        recyclerViewCategories = itemView.findViewById(R.id.categoryRV);
        recyclerViewTypes = itemView.findViewById(R.id.typesRV);
        recyclerViewOrganisations = itemView.findViewById(R.id.organisationRV);
        recyclerViewRestrictions = itemView.findViewById(R.id.restrictionsnRV);

        adapterRestrictions = new FilterAdapter(RESTRICTIONS);
        adapterCategories = new FilterAdapter(CATEGORIES);
        adapterTypes = new FilterAdapter(TYPES);
        adapterOrganisations = new FilterAdapter(ORGANISATIONS);

        RecyclerView.LayoutManager layoutManagerCategory = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
        RecyclerView.LayoutManager layoutManagerTypes = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
        RecyclerView.LayoutManager layoutManagerOrganisations = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
        RecyclerView.LayoutManager linearLayoutManagerRestrictions = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);

        recyclerViewCategories.setLayoutManager(layoutManagerCategory);
        recyclerViewTypes.setLayoutManager(layoutManagerTypes);
        recyclerViewOrganisations.setLayoutManager(layoutManagerOrganisations);
        recyclerViewRestrictions.setLayoutManager(linearLayoutManagerRestrictions);

        recyclerViewCategories.setAdapter(adapterCategories);
        recyclerViewTypes.setAdapter(adapterTypes);
        recyclerViewOrganisations.setAdapter(adapterOrganisations);
        recyclerViewRestrictions.setAdapter(adapterRestrictions);
    }

    /**
     * builds the three RecyclerViews and sets some onClicks
     */
    private void buildRecyclerViews(){
        buildRecyclerView(R.id.restrictionsnRV, recyclerViewRestrictions, adapterRestrictions, RESTRICTIONS);
        buildRecyclerView(R.id.typesRV, recyclerViewTypes, adapterTypes, TYPES);
        buildRecyclerView(R.id.organisationRV, recyclerViewOrganisations, adapterOrganisations, ORGANISATIONS);
        buildRecyclerView(R.id.categoryRV, recyclerViewCategories, adapterCategories, CATEGORIES);

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

        adapterRestrictions.setOnItemClickListener(new FilterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, boolean isChecked) {
                if(listener != null) {
                    listener.onRestrictionClick(position, isChecked);
                }
            }
        });
    }

    private void initializeTimeTextViews(){
        Date startTimeDate = pickedTime.getStartTime();
        Date endTimeDate = pickedTime.getEndTime();
        String  weekday = pickedTime.getWeekday();
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        if(startTimeDate != null){
            String start = format.format(startTimeDate);
            startTime.setText(start);
        }else {
            startTime.setText("");
        }
        if(endTimeDate != null){
            String end = format.format(startTimeDate);
            endTime.setText(end);
        } else {
            endTime.setText("");
        }
            dateTime.setText(weekday);
    }

    private void buildTimePicker(){
        //initialize
        resetPicker  = itemView.findViewById(R.id.resetTimePicker);
        timePickerStart = itemView.findViewById(R.id.timePickerStart);
        timePickerEnd = itemView.findViewById(R.id.timePickerEnd);
        timePickerDate = itemView.findViewById(R.id.timePickerDate);

        startTime = itemView.findViewById(R.id.startTime);
        endTime = itemView.findViewById(R.id.endTime);
        dateTime = itemView.findViewById(R.id.dateTime);

        initializeTimeTextViews();

        //onClicks and TimePickers
        resetPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onResetTimePickerClick();
                    pickedTime.reset();
                    initializeTimeTextViews();
                }

            }
        });
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
                            listener.onTimeStartChanged(time);
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
                            listener.onTimeEndChanged(time);
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
                        listener.onTimeDateChanged(time);
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

        if (isCheckedDelivery){
            deliveryCheckbox.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
        }else {
            deliveryCheckbox.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
        }
        if (isCheckedOpen){
            openedCheckbox.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
        }else {
            openedCheckbox.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
        }

        isDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCheckedDelivery = !isCheckedDelivery;
                if (isCheckedDelivery){
                    deliveryCheckbox.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
                }else {
                    deliveryCheckbox.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
                }
                listener.onDeliveryClick(isCheckedDelivery);
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
                listener.onOpenedClick(isCheckedOpen);
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
        buildRecyclerViews();
        buildTimePicker();
        onClicks();
    }


    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause");
        onResume();


    }
    @Override
    public void onResume() {
        super.onResume();

        if(!itemView.isShown()) {
            BottomSheetBehavior.from((View) itemView.getParent()).setHideable(true);
            BottomSheetBehavior.from((View) itemView.getParent()).setState(BottomSheetBehavior.STATE_HIDDEN);
        }else {
            BottomSheetBehavior.from((View) itemView.getParent()).setHideable(false);
            BottomSheetBehavior.from((View) itemView.getParent()).setState(BottomSheetBehavior.STATE_EXPANDED);
        }

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Dester");
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
        void onRestrictionClick(int position, boolean isChecked);
        void onTimeStartChanged(String time);
        void onTimeEndChanged(String time);
        void onTimeDateChanged(String time);
        void onDeliveryClick(boolean isDelivery);
        void onOpenedClick(boolean isOpen);
        void onResetTimePickerClick();
    }
}
