package com.example.gamelink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.models.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ChatAdapter עם 2 סוגי בועות: INCOMING/OUTGOING
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private static final int VIEW_TYPE_INCOMING = 0;
    private static final int VIEW_TYPE_OUTGOING = 1;

    private final List<Message> messages;
    private final String currentUserId;

    public ChatAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * קובע סוג View לפי שולח ההודעה.
     */
    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        // אם ה-senderId שווה למשתמש הנוכחי => OUTGOING
        if (message.getSenderId().equals(currentUserId)) {
            return VIEW_TYPE_OUTGOING;
        } else {
            return VIEW_TYPE_INCOMING;
        }
    }

    /**
     * מנפח layout שונה לפי ה-viewType.
     */
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_OUTGOING) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_outgoing, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_incoming, parent, false);
        }
        return new ChatViewHolder(view);
    }

    /**
     * מילוי תוכן ההודעה.
     */
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    /**
     * ViewHolder אחד – מציג טקסט + זמן, ללא תלות בסוג. ה-layout שונה כבר ניפוח לפי viewType.
     */
    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView   = itemView.findViewById(R.id.message_text_view);
            timestampTextView = itemView.findViewById(R.id.message_time_text_view);
        }

        public void bind(Message message) {
            // תוכן ההודעה
            messageTextView.setText(message.getContent());

            // פורמט תאריך מלא: dd/MM/yyyy HH:mm
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateTimeString = sdf.format(new Date(message.getTimestamp()));
            timestampTextView.setText(dateTimeString);
        }
    }
}
