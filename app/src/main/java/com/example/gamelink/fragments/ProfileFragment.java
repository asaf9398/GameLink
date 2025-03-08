package com.example.gamelink.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gamelink.R;
import com.example.gamelink.activities.FavoriteGamesActivity;
import com.example.gamelink.activities.LoginActivity;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView nameTextView, emailTextView;
    private Button logoutButton, favoriteGamesButton;
    private FirebaseAuth mAuth;
    private FirebaseDatabaseManager databaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameTextView = view.findViewById(R.id.name_text_view);
        emailTextView = view.findViewById(R.id.email_text_view);
        logoutButton = view.findViewById(R.id.logout_button);
        favoriteGamesButton = view.findViewById(R.id.favorite_games_button);

        mAuth = FirebaseAuth.getInstance();
        databaseManager = new FirebaseDatabaseManager();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            emailTextView.setText(user.getEmail());

            // קבלת נתוני משתמש
            databaseManager.getAllUsers(new FirebaseDatabaseManager.DataCallback<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    for (User u : users) {
                        if (u.getUserId().equals(user.getUid())) {
                            nameTextView.setText(u.getName());
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

        // התנתקות
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        // מעבר למסך המשחקים המועדפים
        favoriteGamesButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FavoriteGamesActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
