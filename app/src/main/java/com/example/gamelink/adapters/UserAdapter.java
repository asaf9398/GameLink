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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    public interface OnUserActionListener {
        void onAddFriend(User user);
        void onRemoveFriend(User user);
        void onUserClicked(User user);
        }

    private final List<User> users;
    private final Set<String> currentFriendsIds;
    private final OnUserActionListener listener;

    public UserAdapter(List<User> users, Set<String> currentFriendsIds, OnUserActionListener listener) {
        this.users = users;
        this.currentFriendsIds = currentFriendsIds != null ? currentFriendsIds : new HashSet<>();
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

        boolean isFriend = currentFriendsIds.contains(user.getUserId());

        holder.addFriendButton.setImageResource(
                isFriend ? android.R.drawable.ic_delete : android.R.drawable.ic_input_add
        );

        holder.addFriendButton.setOnClickListener(v -> {
            if (listener == null) return;

            if (isFriend) {
                listener.onRemoveFriend(user);
                currentFriendsIds.remove(user.getUserId());
            } else {
                listener.onAddFriend(user);
                currentFriendsIds.add(user.getUserId());
            }
            notifyItemChanged(position);
        });

        // ←←← זה מה שחסר לך
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClicked(user);
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
