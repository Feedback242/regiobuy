package de.uni_marburg.sp21.filter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import de.uni_marburg.sp21.R;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {

    private CheckItem[] checkItems;
    private OnItemClickListener listener;

    public FilterAdapter(CheckItem[] checkItems){
        this.checkItems = checkItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_checkbox, parent, false);
        FilterAdapter.ViewHolder viewHolder = new FilterAdapter.ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckItem currentCheckItem = checkItems[position];
        holder.text.setText(currentCheckItem.getText());
        holder.isChecked = currentCheckItem.isChecked();
        if (holder.isChecked){
            holder.checkbox.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
        }else {
            holder.checkbox.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
        }
    }

    @Override
    public int getItemCount() {
        return checkItems.length;
    }

    public interface OnItemClickListener{
        void onItemClick(int position, boolean isChecked);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView text;
        ImageView checkbox;
        boolean isChecked;


        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            text = itemView.findViewById(R.id.textCheck);
            checkbox = itemView.findViewById(R.id.imageCheck);
            isChecked = false;
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position, isChecked);
                        }
                    }
                }
            });
        }
    }
}
