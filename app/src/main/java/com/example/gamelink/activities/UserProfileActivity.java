package com.example.gamelink.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamelink.R;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.Game;
import com.example.gamelink.models.User;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    private MaterialTextView nicknameText, ageText, countryText, favGamesText;
    private String userId;
    private FirebaseDatabaseManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        nicknameText = findViewById(R.id.user_profile_nickname);
        ageText = findViewById(R.id.user_profile_age);
        countryText = findViewById(R.id.user_profile_country);
        favGamesText = findViewById(R.id.user_profile_fav_games);

        db = new FirebaseDatabaseManager();
        userId = getIntent().getStringExtra("userId");

        if (userId != null) {
            // שליפת פרטי המשתמש
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
                    Toast.makeText(UserProfileActivity.this, "שגיאה בטעינת המשתמש", Toast.LENGTH_SHORT).show();
                }
            });

            // שליפת המשחקים המועדפים
            db.getUserFavoriteGamesAsObjects(userId, new FirebaseDatabaseManager.DataCallback<List<Game>>() {
                @Override
                public void onSuccess(List<Game> games) {
                    if (games != null && !games.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (Game game : games) {
                            sb.append(game.getGameName()).append(", ");
                        }
                        // הסרת פסיק אחרון
                        sb.setLength(sb.length() - 2);
                        favGamesText.setText(sb.toString());
                    } else {
                        favGamesText.setText("No favorite games");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(UserProfileActivity.this, "שגיאה בטעינת המשחקים", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // כפתור חזרה
        ImageButton backButton = findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }
    }
}
