package com.example.gamelink.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gamelink.R;
import com.example.gamelink.models.User;

public class ProfileFragment extends Fragment {

    private TextView nameTextView, ageTextView, countryTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        nameTextView = view.findViewById(R.id.name_text_view);
        ageTextView = view.findViewById(R.id.age_text_view);
        countryTextView = view.findViewById(R.id.country_text_view);

        // לדוגמה: הצגת נתוני משתמש
        User user = new User("user123", "John Doe", 25, "USA", null);
        nameTextView.setText(user.getName());
        ageTextView.setText(String.valueOf(user.getAge()));
        countryTextView.setText(user.getCountry());

        return view;
    }
}

