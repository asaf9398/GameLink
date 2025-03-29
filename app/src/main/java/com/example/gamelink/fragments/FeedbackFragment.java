package com.example.gamelink.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gamelink.R;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class FeedbackFragment extends Fragment {

    private RatingBar ratingBar;
    private TextInputLayout feedbackTextLayout;
    private TextInputEditText feedbackEditText;
    private RadioGroup categoryRadioGroup;
    private MaterialCheckBox anonCheckBox;
    private MaterialButton submitButton;

    private FirebaseDatabaseManager databaseManager;

    private String targetUserId = "example_user_id";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        ratingBar         = view.findViewById(R.id.feedback_rating_bar);
        feedbackTextLayout = view.findViewById(R.id.feedback_text_layout);
        feedbackEditText  = view.findViewById(R.id.feedback_edit_text);
        categoryRadioGroup= view.findViewById(R.id.feedback_category_radio_group);
        anonCheckBox      = view.findViewById(R.id.feedback_anon_checkbox);
        submitButton      = view.findViewById(R.id.feedback_submit_button);

        databaseManager = new FirebaseDatabaseManager();

        submitButton.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String feedback = feedbackEditText.getText().toString().trim();
            int selectedCategoryId = categoryRadioGroup.getCheckedRadioButtonId();

            if (rating <= 0 || feedback.isEmpty() || selectedCategoryId == -1) {
                Toast.makeText(getContext(), "Please fill all fields & rating", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isAnonymous = anonCheckBox.isChecked();

            String category = "";
            RadioButton selectedRb = view.findViewById(selectedCategoryId);
            if (selectedRb != null) {
                category = selectedRb.getText().toString();
            }

            databaseManager.addRating(targetUserId, (int) rating,
                    category + ": " + feedback + (isAnonymous ? " (Anonymous)" : ""));

            Toast.makeText(getContext(), "Feedback submitted successfully", Toast.LENGTH_SHORT).show();

            ratingBar.setRating(0f);
            feedbackEditText.setText("");
            categoryRadioGroup.clearCheck();
            anonCheckBox.setChecked(false);
        });

        return view;
    }
}
