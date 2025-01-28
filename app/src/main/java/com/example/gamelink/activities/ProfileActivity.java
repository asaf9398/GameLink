package com.example.gamelink.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamelink.R;
import com.example.gamelink.models.User;

public class ProfileActivity extends AppCompatActivity {

    private TextView nameTextView, ageTextView, countryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        nameTextView = findViewById(R.id.name_text_view);
        ageTextView = findViewById(R.id.age_text_view);
        countryTextView = findViewById(R.id.country_text_view);

        // לדוגמה: הצגת נתוני משתמש
        User user = new User("user123", "John Doe", 25, "USA", null);
        nameTextView.setText(user.getName());
        ageTextView.setText(String.valueOf(user.getAge()));
        countryTextView.setText(user.getCountry());
    }
}

