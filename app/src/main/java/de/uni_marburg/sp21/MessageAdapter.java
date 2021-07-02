package de.uni_marburg.sp21;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.uni_marburg.sp21.company_data_structure.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private final boolean showNames;
    private OnMessageClickListener listener;
    private List<Message> messages;
    public MessageAdapter(List<Message> imagePaths, boolean showNames){
        this.messages = imagePaths;
        this.showNames = showNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message currentMessage = messages.get(position);
        //setting the date to a format, that is more readable
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date date = inputFormat.parse(currentMessage.getDate());
            SimpleDateFormat outputFormat = new SimpleDateFormat("d MMM yyyy, HH:mm:ss");
            String string = outputFormat.format(date);
            holder.date.setText(string);
        } catch (ParseException e) {
            holder.date.setText(currentMessage.getDate());
        }
        holder.content.setText(currentMessage.getContent());
        if(showNames){
            holder.name.setText(currentMessage.getCompanyName() +":");
        } else {
            holder.name.setText("");
        }
        holder.nameString = currentMessage.getCompanyName();

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView content;
        TextView name;
        String nameString;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            content = itemView.findViewById(R.id.content);
            name = itemView.findViewById(R.id.nameMessages);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onClick(nameString);
                        }
                    }
                }
            });
        }
    }

    interface OnMessageClickListener{
        void onClick(String companyName);
    }

    public void setOnMessageClickListener(OnMessageClickListener listener) {
        this.listener = listener;
    }
}
