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
import java.util.List;
import java.util.Random;

import de.uni_marburg.sp21.data_structure.Company;
import de.uni_marburg.sp21.data_structure.ShopType;
import de.uni_marburg.sp21.filter.BottomSheetFilter;
import de.uni_marburg.sp21.filter.CheckItem;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> implements Filterable {

    private List<Company> companies;
    private List<Company> companyFullList;


    public CompanyAdapter(List<Company> companies) {

        this.companies = companies;
        companyFullList = new ArrayList<>(companies);
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
        List<ShopType> shopTypes = currentCompany.getTypes();
        ShopType randomTypeFromList = shopTypes.get(new Random().nextInt(shopTypes.size()));
        holder.image.setImageResource(randomTypeFromList.toDrawableID());
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            System.out.println("Constraint:" + constraint);
            List<Company> filterdList = new ArrayList<>();
            if(constraint == null || constraint.length() == 0){
                System.out.println("DOne");
                filterdList.addAll(companyFullList);
            }else {
                String filterParameter = constraint.toString().toLowerCase().trim();
                for (Company company : companyFullList){
                    if (company.getName().toLowerCase().contains(filterParameter) && filterCompany(company)) {

                        filterdList.add(company);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterdList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            companies.clear();
            companies.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    public boolean filterCompany(Company company){
        boolean result = false;
        for (CheckItem checkItem: BottomSheetFilter.getTYPES()){
           result =  company.getTypes().contains(ShopType.valueOF(checkItem.getText()));

        }

        return result;
    }

    public List<CheckItem> allCheckedItems(){
         List<CheckItem> allCheckedItems = new ArrayList();
        for (CheckItem item: BottomSheetFilter.getTYPES()) {
            allCheckedItems.add(item);
        }
        for (CheckItem item : BottomSheetFilter.getCATEGORIES()){
            allCheckedItems.add(item);
        }
        for (CheckItem item: BottomSheetFilter.getORGANISATIONS()) {
            allCheckedItems.add(item);
        }
        return allCheckedItems;
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
