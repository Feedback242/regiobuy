package de.uni_marburg.sp21;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import de.uni_marburg.sp21.company_data_structure.Company;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {

    private List<Company> companies;
    private OnItemClickListener listener;
    private Context context;

    /**
     * Constructor
     * @param companies the Companies that should be displayed by the Adapter
     */
    public CompanyAdapter(List<Company> companies) {
        this.companies = companies;
        context = MyApplication.getAppContext();
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

        currentCompany.setImageToImageView(holder.image);

        if(currentCompany.isDeliveryService()){
            holder.delivery.setColorFilter(ContextCompat.getColor(context, R.color.green_delivery), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.delivery.setColorFilter(ContextCompat.getColor(context, R.color.red_delivery), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if(currentCompany.isOpen()){
            holder.open.setColorFilter(ContextCompat.getColor(context, R.color.green_delivery), android.graphics.PorterDuff.Mode.SRC_IN);
        } else {
            holder.open.setColorFilter(ContextCompat.getColor(context, R.color.red_delivery), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;
        ImageView delivery;
        ImageView open;
        TextView description;
        TextView location;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
            description = itemView.findViewById(R.id.description);
            location = itemView.findViewById(R.id.location);
            delivery = itemView.findViewById(R.id.delivery);
            open = itemView.findViewById(R.id.open);

           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (listener != null) {
                       int position = getAdapterPosition();
                       if (position != RecyclerView.NO_POSITION) {
                           listener.onCompanyClick(position);
                       }
                   }
               }
           });
        }

    }

    /**
     * Sets the Listener that passes Values, for onClicks and so on, to the Class where The Adapter gets Initiated
     * @param listener You have to Implement all Methods from the Listener Interface and they will pass the Values to your Class
     */
    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onCompanyClick(int pos);
    }
}
