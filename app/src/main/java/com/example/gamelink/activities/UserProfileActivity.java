package com.example.gamelink.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamelink.R;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.User;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private MaterialTextView nicknameText, ageText, countryText;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nicknameText = findViewById(R.id.user_profile_nickname);
        ageText = findViewById(R.id.user_profile_age);
        countryText = findViewById(R.id.user_profile_country);

        userId = getIntent().getStringExtra("userId");

        if (userId != null) {
            FirebaseDatabaseManager db = new FirebaseDatabaseManager();
            db.getAllUsers(new FirebaseDatabaseManager.DataCallback<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    for (User u : users) {
                        if (userId.equals(u.getUserId())) {
                            nicknameText.setText(u.getNickname());
                            ageText.setText("Age: " + u.getAge());
                            countryText.setText("Country: " + u.getCountry());
                            break;
                        }
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(UserProfileActivity.this, "Failed to load user", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

