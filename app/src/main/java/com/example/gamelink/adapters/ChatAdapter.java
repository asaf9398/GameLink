package com.example.gamelink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.models.Message;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<Message> messages;
    private final String currentUserId;

    public ChatAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message, message.getSenderId().equals(currentUserId));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timestampTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView   = itemView.findViewById(R.id.message_text_view);
            timestampTextView = itemView.findViewById(R.id.message_time_text_view); // הוסף בשורת ה-XML
        }

        void bind(Message message, boolean isMine) {
            messageTextView.setText(message.getContent());

            // הצגת זמן (לדוגמה פורמט HH:mm)
            long ts = message.getTimestamp();
            String timeString = new java.text.SimpleDateFormat("HH:mm")
                    .format(new java.util.Date(ts));
            timestampTextView.setText(timeString);

            // אפשר לשנות עיצוב אם isMine=true
            if(isMine) {
                // לדוגמה: textColor אחר, alignment וכו’.
            } else {
                // עיצוב אחר
            }
        }
    }
}


