package com.example.gamelink.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gamelink.R;
import com.example.gamelink.firebase.FirebaseDatabaseManager;

public class FeedbackFragment extends Fragment {

    private RatingBar ratingBar;
    private EditText feedbackEditText;
    private Button submitButton;

    private FirebaseDatabaseManager databaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        ratingBar = view.findViewById(R.id.rating_bar);
        feedbackEditText = view.findViewById(R.id.feedback_edit_text);
        submitButton = view.findViewById(R.id.submit_button);

        databaseManager = new FirebaseDatabaseManager(requireContext());

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String feedback = feedbackEditText.getText().toString().trim();

            if (rating > 0 && !feedback.isEmpty()) {
                databaseManager.addRating("example_user_id", (int) rating, feedback);
                Toast.makeText(getContext(), "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Please provide a rating and feedback", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
