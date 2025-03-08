package com.example.gamelink.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamelink.R;
import com.example.gamelink.firebase.FirebaseDatabaseManager;

public class FeedbackActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText feedbackEditText;
    private Button submitButton;

    private FirebaseDatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Initialize UI components
        ratingBar = findViewById(R.id.rating_bar);
        feedbackEditText = findViewById(R.id.feedback_edit_text);
        submitButton = findViewById(R.id.submit_button);

        // Initialize Firebase Database Manager
        databaseManager = new FirebaseDatabaseManager(this);

        // Handle submit button click
        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String feedback = feedbackEditText.getText().toString().trim();

            if (rating > 0 && !feedback.isEmpty()) {
                databaseManager.addRating("example_user_id", (int) rating, feedback);
                Toast.makeText(this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Please provide a rating and feedback", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

