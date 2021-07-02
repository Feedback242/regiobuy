package de.uni_marburg.sp21;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.uni_marburg.sp21.company_data_structure.Category;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder>{

    private OnPostClickListener listener;
    private List<Post> posts;

    public ShoppingListAdapter(List<Post> posts){
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
        ShoppingListAdapter.ViewHolder viewHolder = new ShoppingListAdapter.ViewHolder(view, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post currentPost = posts.get(position);
        holder.name.setText(currentPost.getName());
        holder.isChecked = currentPost.isChecked();

        String cat = "";
        for (Category category : currentPost.getCategories()){
            cat += category.toString() + ", ";
        }
        if(!cat.isEmpty()){
            cat = cat.substring(0, cat.length()-2);
        } else {
            holder.search.setVisibility(View.GONE);
            cat = "-";
        }

        holder.categories.setText(cat);
        if(holder.isChecked){
            holder.check.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
            holder.name.setPaintFlags(holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.check.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
            holder.name.setPaintFlags(holder.name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView categories;
        ImageView check;
        ImageView search;
        boolean isChecked;

        public ViewHolder(@NonNull View itemView, OnPostClickListener listener) {
            super(itemView);
            isChecked = false;
            name = itemView.findViewById(R.id.postName);
            check = itemView.findViewById(R.id.postChecked);
            search = itemView.findViewById(R.id.postSearch);
            categories = itemView.findViewById(R.id.category);
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isChecked = !isChecked;
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION) {
                        posts.get(pos).setChecked(isChecked);
                        if (isChecked) {
                            check.setImageResource(R.drawable.ic_baseline_radio_button_checked_24);
                            name.setPaintFlags(name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        } else {
                            check.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24);
                            name.setPaintFlags(name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        }
                    }
                }
            });

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            listener.onSearchClick(pos);
                        }
                    }
                }
            });
        }
    }

    interface OnPostClickListener{
        void onSearchClick(int pos);
    }

    public void setListener(OnPostClickListener listener) {
        this.listener = listener;
    }
}
