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

import com.example.gamelink.R;
import com.example.gamelink.activities.FavoriteGamesActivity;
import com.example.gamelink.activities.LoginActivity;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ProfileFragment extends Fragment {

    private ShapeableImageView profileImageView; // תמונת פרופיל עגולה
    private MaterialTextView nameTextView, emailTextView, ratingTextView;
    private MaterialButton logoutButton, favoriteGamesButton, editProfileButton;

    private FirebaseAuth mAuth;
    private FirebaseDatabaseManager databaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImageView = view.findViewById(R.id.profile_image_view);
        nameTextView     = view.findViewById(R.id.profile_name_text_view);
        emailTextView    = view.findViewById(R.id.profile_email_text_view);
        ratingTextView   = view.findViewById(R.id.profile_rating_text_view);

        logoutButton        = view.findViewById(R.id.profile_logout_button);
        favoriteGamesButton = view.findViewById(R.id.profile_favorite_games_button);
        editProfileButton   = view.findViewById(R.id.profile_edit_button);

        mAuth = FirebaseAuth.getInstance();
        databaseManager = new FirebaseDatabaseManager();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emailTextView.setText(user.getEmail());

            // טוענים נתוני משתמש מ-Firebase
            databaseManager.getAllUsers(new FirebaseDatabaseManager.DataCallback<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    for (User u : users) {
                        if (u.getUserId() != null && u.getUserId().equals(user.getUid())) {
                            nameTextView.setText(u.getName() != null ? u.getName() : "No Name");
                            // אפשר להציג כאן average rating (אם הייתם שומרים בגוף ה-User)
                            // למשל:
                            // ratingTextView.setText(String.valueOf(u.getAverageRating()));
                            break;
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getActivity(), "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // כפתור עריכת פרופיל (למשל מעבר ל-EditProfileActivity)
        editProfileButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Edit Profile (not implemented)", Toast.LENGTH_SHORT).show();
            // אפשר לפתוח Activity לעריכת הפרופיל
        });

        // התנתקות
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        // מעבר למסך המשחקים המועדפים
        favoriteGamesButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FavoriteGamesActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
