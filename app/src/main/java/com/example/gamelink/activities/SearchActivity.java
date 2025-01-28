package com.example.gamelink.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.adapters.UserAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.User;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText gameEditText, countryEditText;
    private Button searchButton;
    private RecyclerView searchRecyclerView;

    private UserAdapter userAdapter;
    private List<User> searchResults;
    private FirebaseDatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        gameEditText = findViewById(R.id.game_edit_text);
        countryEditText = findViewById(R.id.country_edit_text);
        searchButton = findViewById(R.id.search_button);
        searchRecyclerView = findViewById(R.id.search_recycler_view);

        databaseManager = new FirebaseDatabaseManager();

        searchResults = new ArrayList<>();
        userAdapter = new UserAdapter(searchResults);

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerView.setAdapter(userAdapter);

        searchButton.setOnClickListener(v -> {
            String game = gameEditText.getText().toString().trim();
            String country = countryEditText.getText().toString().trim();

            databaseManager.getAllUsers(new FirebaseDatabaseManager.DataCallback<List<User>>() {
                @Override
                public void onSuccess(List<User> users) {
                    searchResults.clear();
                    for (User user : users) {
                        if (user.getFavoriteGames().contains(game) &&
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
        });
    }
}
