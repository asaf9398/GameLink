package com.example.gamelink.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.getSenderId().equals(currentUserId) ? VIEW_TYPE_OUTGOING : VIEW_TYPE_INCOMING;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                viewType == VIEW_TYPE_OUTGOING ? R.layout.item_message_outgoing : R.layout.item_message_incoming,
                parent,
                false
        );
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampTextView;
        ImageView messageImageView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView   = itemView.findViewById(R.id.message_text_view);
            timestampTextView = itemView.findViewById(R.id.message_time_text_view);
            messageImageView  = itemView.findViewById(R.id.message_image_view);
        }

        public void bind(Message message) {
            // תאריך
            String dateTimeString = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(new Date(message.getTimestamp()));
            timestampTextView.setText(dateTimeString);

            // אם זה הודעת קובץ (תמונה)
            if (message.getContent().startsWith("[File] ")) {

                //Log.d("ChatAdapter", "Message content: " + message.getContent());

                messageTextView.setVisibility(View.GONE);
                messageImageView.setVisibility(View.VISIBLE);

                String imageUrl = message.getContent().replace("[File] ", "").trim();
                Glide.with(itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(messageImageView);
            } else {
                messageImageView.setVisibility(View.GONE);
                messageTextView.setVisibility(View.VISIBLE);
                messageTextView.setText(message.getContent());
            }
        }
    }
}
