package com.example.gamelink.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.activities.FavoriteGamesActivity;
import com.example.gamelink.activities.LoginActivity;
import com.example.gamelink.adapters.UserAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileFragment extends Fragment {

    private ShapeableImageView profileImageView;
    private MaterialTextView nameTextView, emailTextView, ratingTextView;
    private MaterialButton logoutButton, favoriteGamesButton, editProfileButton;
    private RecyclerView friendsRecyclerView;

    private UserAdapter friendsAdapter;
    private List<User> friendsList;
    private Set<String> friendsIdSet;

    private FirebaseAuth mAuth;
    private FirebaseDatabaseManager databaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // קישור לרכיבים
        profileImageView     = view.findViewById(R.id.profile_image_view);
        nameTextView         = view.findViewById(R.id.profile_name_text_view);
        emailTextView        = view.findViewById(R.id.profile_email_text_view);
        ratingTextView       = view.findViewById(R.id.profile_rating_text_view);
        friendsRecyclerView  = view.findViewById(R.id.profile_friends_recycler);
        logoutButton         = view.findViewById(R.id.profile_logout_button);
        favoriteGamesButton  = view.findViewById(R.id.profile_favorite_games_button);
        editProfileButton    = view.findViewById(R.id.profile_edit_button);

        // אתחול
        mAuth = FirebaseAuth.getInstance();
        databaseManager = new FirebaseDatabaseManager();
        friendsList = new ArrayList<>();
        friendsIdSet = new HashSet<>();

        // אתחול אדפטר – בפרופיל לא מבצעים פעולה על חברים ולכן listener=null
        friendsAdapter = new UserAdapter(friendsList, friendsIdSet, null);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        friendsRecyclerView.setAdapter(friendsAdapter);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emailTextView.setText(user.getEmail());

            // טען פרטי משתמש
            databaseManager.getAllUsers(new FirebaseDatabaseManager.DataCallback<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    for (User u : users) {
                        if (u.getUserId() != null && u.getUserId().equals(user.getUid())) {
                            nameTextView.setText(u.getNickname() != null ? u.getNickname() : "No Name");
                            break;
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
                }
            });

            // טען רשימת חברים
            databaseManager.getUserFriends(user.getUid(), new FirebaseDatabaseManager.DataCallback<List<User>>() {
                @Override
                public void onSuccess(List<User> data) {
                    friendsList.clear();
                    friendsIdSet.clear();
                    for (User friend : data) {
                        if (friend.getUserId() != null) {
                            friendsList.add(friend);
                            friendsIdSet.add(friend.getUserId());
                        }
                    }
                    friendsAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "שגיאה בטעינת חברים", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // פעולות לחצנים
        editProfileButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit Profile (not implemented)", Toast.LENGTH_SHORT).show();
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            if (getActivity() != null) getActivity().finish();
        });

        favoriteGamesButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), FavoriteGamesActivity.class));
        });

        return view;
    }
}
