package de.uni_marburg.sp21;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.ShopType;
import de.uni_marburg.sp21.filter.BottomSheetFilter;
import de.uni_marburg.sp21.filter.CheckItem;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {

    private List<Company> companies;
    private OnItemClickListener listener;

    public CompanyAdapter(List<Company> companies) {
        this.companies = companies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Company currentCompany = companies.get(position);
        holder.name.setText(currentCompany.getName());
        holder.description.setText(currentCompany.getDescription());
        holder.location.setText(currentCompany.getAddress().getZip() + " " + currentCompany.getAddress().getCity());
        List<ShopType> shopTypes = currentCompany.getTypes();
        ShopType randomTypeFromList = shopTypes.get(new Random().nextInt(shopTypes.size()));
        holder.image.setImageResource(randomTypeFromList.toDrawableID());
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView image;
        TextView description;
        TextView location;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            description = itemView.findViewById(R.id.description);
            location = itemView.findViewById(R.id.location);
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCompanyClick(position);
                }
            }
        }

    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public interface OnItemClickListener{
        void onCompanyClick(int pos);
    }
}
