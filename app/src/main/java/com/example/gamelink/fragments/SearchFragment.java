package com.example.gamelink.fragments;

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
import com.example.gamelink.adapters.UserAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private TextInputLayout gameTextInputLayout, countryTextInputLayout;
    private MaterialAutoCompleteTextView gameAutoCompleteTextView, countryAutoCompleteTextView;
    private MaterialButton searchButton, advancedFilterButton;
    private RecyclerView searchRecyclerView; // <--- RecyclerView במקום MaterialRecyclerView

    private UserAdapter userAdapter;
    private List<User> searchResults;
    private FirebaseDatabaseManager databaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // קישור לרכיבי ה־XML
        gameTextInputLayout = view.findViewById(R.id.search_game_text_input_layout);
        countryTextInputLayout = view.findViewById(R.id.search_country_text_input_layout);

        gameAutoCompleteTextView = view.findViewById(R.id.search_game_autocomplete);
        countryAutoCompleteTextView = view.findViewById(R.id.search_country_autocomplete);

        searchButton = view.findViewById(R.id.search_button);
        advancedFilterButton = view.findViewById(R.id.search_advanced_filter_button);

        // שים לב: השתמש ב־androidx.recyclerview.widget.RecyclerView במקום MaterialRecyclerView
        searchRecyclerView = view.findViewById(R.id.search_recycler_view);

        // אתחול אובייקטים
        databaseManager = new FirebaseDatabaseManager();
        searchResults = new ArrayList<>();
        userAdapter = new UserAdapter(searchResults);

        // הגדרת ריסייקלר:
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setAdapter(userAdapter);

        // הגדרת נתונים ל־AutoComplete
        setupAutoCompleteData();

        // מאזין לכפתור חיפוש
        searchButton.setOnClickListener(v -> {
            String game = gameAutoCompleteTextView.getText().toString().trim();
            String country = countryAutoCompleteTextView.getText().toString().trim();
            performSearch(game, country);
        });

        // מאזין לכפתור פילטרים
        advancedFilterButton.setOnClickListener(v -> {
            // כאן אפשר לפתוח bottom sheet / מסך נוסף לסינון מתקדם
            Toast.makeText(getContext(), "Open advanced filters (not implemented)", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void setupAutoCompleteData() {
        // דוגמה לטעינת רשימת משחקים מוכרים ורשימת ארצות
        String[] popularGames = {"Chess", "Fortnite", "Overwatch", "League of Legends"};
        String[] countries = {"Israel", "USA", "Canada", "France", "Germany"};

        // משתמשים ב־MaterialAutoCompleteTextView כדי להציג אפשרויות:
        gameAutoCompleteTextView.setSimpleItems(popularGames);
        countryAutoCompleteTextView.setSimpleItems(countries);
    }

    private void performSearch(String game, String country) {
        databaseManager.getAllUsers(new FirebaseDatabaseManager.DataCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                searchResults.clear();
                for (User user : users) {
                    if (user.getFavoriteGames() == null) continue;
                    if (user.getFavoriteGames().contains(game) &&
                            user.getCountry() != null &&
                            user.getCountry().equalsIgnoreCase(country)) {

                        searchResults.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
