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
import android.widget.SeekBar;
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

public class LocationBottomSheet extends BottomSheetDialogFragment {

    private View itemView;
    private int radius;
    private LocationSettingsListener listener;
    private SeekBar seekBar;
    private TextView radiusTextView;

    public LocationBottomSheet(int radius){
        this.radius = radius;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public void onStart() {
        super.onStart();
        buildSettings();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemView = inflater.inflate(R.layout.location_bottom_sheet, container, false);
        return itemView;
    }

    private void buildSettings(){
        seekBar = itemView.findViewById(R.id.seekBar);
        radiusTextView = itemView.findViewById(R.id.radius);

        if(radius == 0){
            radiusTextView.setText(R.string.without_radius);
        } else {
            radiusTextView.setText(radius + "km");
        }

        seekBar.setProgress(radiusToPercent(radius));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = percentToRadius(progress);
                if(radius == 0){
                    radiusTextView.setText(R.string.without_radius);
                } else {
                    radiusTextView.setText(radius + "km");
                }
                if(listener != null){
                    listener.onLocationChange(radius);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private int percentToRadius(int progress){
        // progress is in the interval [0,100] -> max = 500, min = 0, f(x)=x^2/20
        return (int) ((float) Math.pow(progress, 2) / 20f);
    }

    private int radiusToPercent(int radius){
        if(radius >= 0){
            return (int) (Math.sqrt(radius) * Math.sqrt(20));
        }
        return 0;
    }

    /**
     * Sets the listener
     * @param listener an instance of the Interface below, to pass values
     */
    public void setLocationSettingsListener(LocationSettingsListener listener) {
        this.listener = listener;
    }

    /**
     * This interface is for passing values to the MainActivity
     */
    public interface LocationSettingsListener{
        void onLocationChange(int radius);
    }
}
