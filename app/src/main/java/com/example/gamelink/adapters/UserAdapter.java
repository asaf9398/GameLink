package com.example.gamelink.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onAddFriend(User user);
    }

    private final List<User> users;
    private final OnUserActionListener listener;

    public UserAdapter(List<User> users, OnUserActionListener listener) {
        this.users = users;
        this.listener = listener;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.userNameTextView.setText(user.getNickname());
        holder.userCountryTextView.setText(user.getCountry());

        holder.addFriendButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddFriend(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTextView;
        TextView userCountryTextView;
        ImageButton addFriendButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.user_name_text_view);
            userCountryTextView = itemView.findViewById(R.id.user_country_text_view);
            addFriendButton = itemView.findViewById(R.id.add_friend_button);
        }
    }
}
