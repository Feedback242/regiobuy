package de.uni_marburg.sp21;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.uni_marburg.sp21.data_structure.Company;

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
}
