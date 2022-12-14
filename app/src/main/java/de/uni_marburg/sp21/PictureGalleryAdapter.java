package de.uni_marburg.sp21;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PictureGalleryAdapter extends RecyclerView.Adapter<PictureGalleryAdapter.ViewHolder> {

    private List<String> imagePaths;
    private OnPhotoClickListerner listerner;

    public PictureGalleryAdapter(List<String> imagePaths){
        this.imagePaths = imagePaths;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.picture_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, listerner);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currentPath = imagePaths.get(position);
        DataBaseManager.setImageFromPath(currentPath, holder.picture);
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView picture;

        public ViewHolder(@NonNull View itemView, OnPhotoClickListerner listener) {
            super(itemView);
            picture = itemView.findViewById(R.id.picture);

            picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onPhotoClickListerner(position);
                        }
                    }
                }
            });
        }
    }

    public void setListerner(OnPhotoClickListerner listerner) {
        this.listerner = listerner;
    }

    public interface OnPhotoClickListerner{
        void onPhotoClickListerner(int pos);
    }
}
