package de.uni_marburg.sp21;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.ShopType;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {

    private List<Company> companies;

    public CompanyAdapter(List<Company> companies) {
        this.companies = companies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Company currentCompany = companies.get(position);
        holder.name.setText(currentCompany.getName());
        holder.description.setText(currentCompany.getDescription());
        holder.location.setText(currentCompany.getAddress().getZip() + " " + currentCompany.getAddress().getCity());
        holder.image.setImageResource(getImage(currentCompany));
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            description = itemView.findViewById(R.id.description);
            location = itemView.findViewById(R.id.location);
        }
    }

    private int getImage(Company company){
        ArrayList<ShopType> types = company.getTypes();
        ShopType shopType;
        if(types != null) {
            int r = new Random().nextInt(types.size());
            shopType = types.get(r);
        } else {
            return R.drawable.shop;
        }

        switch (shopType){
            case RESTAURANT: return R.drawable.restaurant;
            case PRODUCER: return R.drawable.producer;
            case HOTEL: return R.drawable.hotel;
            case MART: return R.drawable.mart;
            default: return R.drawable.shop;
        }
    }
}
