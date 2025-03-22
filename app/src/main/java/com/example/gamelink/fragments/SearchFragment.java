package com.example.gamelink.fragments;

import android.content.Intent;
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
import com.example.gamelink.activities.UserProfileActivity;
import com.example.gamelink.adapters.UserAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFragment extends Fragment {

    private TextInputEditText nicknameEditText;
    private MaterialAutoCompleteTextView gameAutoCompleteTextView, countryAutoCompleteTextView;
    private MaterialButton searchButton, advancedFilterButton;
    private RecyclerView searchRecyclerView;

    private UserAdapter userAdapter;
    private List<User> searchResults;
    private Set<String> currentFriends;
    private FirebaseDatabaseManager databaseManager;
    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        nicknameEditText = view.findViewById(R.id.search_nickname_edit_text);
        gameAutoCompleteTextView = view.findViewById(R.id.search_game_autocomplete);
        countryAutoCompleteTextView = view.findViewById(R.id.search_country_autocomplete);
        searchButton = view.findViewById(R.id.search_button);
        advancedFilterButton = view.findViewById(R.id.search_advanced_filter_button);
        searchRecyclerView = view.findViewById(R.id.search_recycler_view);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseManager = new FirebaseDatabaseManager();
        searchResults = new ArrayList<>();
        currentFriends = new HashSet<>();

        userAdapter = new UserAdapter(searchResults, currentFriends, new UserAdapter.OnUserActionListener() {
            @Override
            public void onAddFriend(User user) {
                String userId = user.getUserId();
                databaseManager.addFriend(currentUserId, userId, new FirebaseDatabaseManager.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        currentFriends.add(userId);
                        userAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), user.getNickname() + " נוסף לרשימת החברים שלך!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "שגיאה בהוספת חבר", Toast.LENGTH_SHORT).show();
                    }
                });

                databaseManager.addFriend(userId, currentUserId, null);
            }

            @Override
            public void onRemoveFriend(User user) {
                String userId = user.getUserId();
                databaseManager.removeFriend(currentUserId, userId, new FirebaseDatabaseManager.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        currentFriends.remove(userId);
                        userAdapter.notifyDataSetChanged();
                        Toast.makeText(getContext(), user.getNickname() + " הוסר מרשימת החברים שלך", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "שגיאה בהסרת חבר", Toast.LENGTH_SHORT).show();
                    }
                });

                databaseManager.removeFriend(userId, currentUserId, null);
            }


            @Override
            public void onUserClicked(User user) {
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                intent.putExtra("userId", user.getUserId());
                startActivity(intent);
            }
        });

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setAdapter(userAdapter);

        setupAutoCompleteData();
        loadCurrentFriends();

        searchButton.setOnClickListener(v -> {
            String nickname = nicknameEditText.getText().toString().trim();
            String game = gameAutoCompleteTextView.getText().toString().trim();
            String country = countryAutoCompleteTextView.getText().toString().trim();
            performSearch(nickname, game, country);
        });

        advancedFilterButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Open advanced filters (not implemented)", Toast.LENGTH_SHORT).show());

        return view;
    }

    private void setupAutoCompleteData() {
        String[] popularGames = { "Chess", "Fortnite", "Overwatch", "League of Legends" };
        String[] countries = { "Israel", "USA", "Canada", "France", "Germany" };

        gameAutoCompleteTextView.setSimpleItems(popularGames);
        countryAutoCompleteTextView.setSimpleItems(countries);
    }

    private void loadCurrentFriends() {
        databaseManager.getUserFriendIds(currentUserId, new FirebaseDatabaseManager.DataCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                currentFriends.clear();
                currentFriends.addAll(data);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "נכשלה טעינת רשימת החברים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performSearch(String nickname, String game, String country) {
        databaseManager.getAllUsers(new FirebaseDatabaseManager.DataCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                searchResults.clear();

                for (User user : users) {
                    if (user.getUserId() == null || user.getUserId().equals(currentUserId)) continue;

                    boolean matchNickname = nickname.isEmpty() || (user.getNickname() != null &&
                            user.getNickname().toLowerCase().contains(nickname.toLowerCase()));

                    boolean matchGame = game.isEmpty() || (user.getFavoriteGames() != null &&
                            user.getFavoriteGames().contains(game));

                    boolean matchCountry = country.isEmpty() || (user.getCountry() != null &&
                            user.getCountry().equalsIgnoreCase(country));

                    if (matchNickname && matchGame && matchCountry) {
                        searchResults.add(user);
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "נכשלה טעינת המשתמשים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
