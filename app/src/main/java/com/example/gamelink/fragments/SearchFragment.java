package com.example.gamelink.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.adapters.UserAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.User;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText gameEditText, countryEditText;
    private Button searchButton;
    private RecyclerView searchRecyclerView;

    private UserAdapter userAdapter;
    private List<User> searchResults;
    private FirebaseDatabaseManager databaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        gameEditText = view.findViewById(R.id.game_edit_text);
        countryEditText = view.findViewById(R.id.country_edit_text);
        searchButton = view.findViewById(R.id.search_button);
        searchRecyclerView = view.findViewById(R.id.search_recycler_view);

        databaseManager = new FirebaseDatabaseManager();

        searchResults = new ArrayList<>();
        userAdapter = new UserAdapter(searchResults);

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

        return view;
    }
}
