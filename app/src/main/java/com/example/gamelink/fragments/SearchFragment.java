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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private TextInputLayout nicknameTextInputLayout;
    private TextInputEditText nicknameEditText;
    private TextInputLayout gameTextInputLayout, countryTextInputLayout;
    private MaterialAutoCompleteTextView gameAutoCompleteTextView, countryAutoCompleteTextView;
    private MaterialButton searchButton, advancedFilterButton;
    private RecyclerView searchRecyclerView;

    private UserAdapter userAdapter;
    private List<User> searchResults;
    private FirebaseDatabaseManager databaseManager;

    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        nicknameTextInputLayout     = view.findViewById(R.id.search_nickname_text_input_layout);
        nicknameEditText            = view.findViewById(R.id.search_nickname_edit_text);
        gameTextInputLayout         = view.findViewById(R.id.search_game_text_input_layout);
        gameAutoCompleteTextView    = view.findViewById(R.id.search_game_autocomplete);
        countryTextInputLayout      = view.findViewById(R.id.search_country_text_input_layout);
        countryAutoCompleteTextView = view.findViewById(R.id.search_country_autocomplete);
        searchButton                = view.findViewById(R.id.search_button);
        advancedFilterButton        = view.findViewById(R.id.search_advanced_filter_button);
        searchRecyclerView          = view.findViewById(R.id.search_recycler_view);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseManager = new FirebaseDatabaseManager();
        searchResults = new ArrayList<>();

        userAdapter = new UserAdapter(searchResults, user -> {
            // הוספת חבר לשני הצדדים עם callback
            databaseManager.addFriend(currentUserId, user.getUserId(), new FirebaseDatabaseManager.OperationCallback() {
                @Override
                public void onSuccess() {
                    // אין צורך להודיע פעמיים
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "שגיאה בהוספת חבר", Toast.LENGTH_SHORT).show();
                }
            });

            databaseManager.addFriend(user.getUserId(), currentUserId, new FirebaseDatabaseManager.OperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getContext(), user.getNickname() + " נוסף לרשימת החברים שלך!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "שגיאה בהוספת חבר", Toast.LENGTH_SHORT).show();
                }
            });
        });

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setAdapter(userAdapter);

        setupAutoCompleteData();

        searchButton.setOnClickListener(v -> {
            String nickname = nicknameEditText.getText().toString().trim();
            String game = gameAutoCompleteTextView.getText().toString().trim();
            String country = countryAutoCompleteTextView.getText().toString().trim();
            performSearch(nickname, game, country);
        });

        advancedFilterButton.setOnClickListener(v ->
                Toast.makeText(getContext(), "Open advanced filters (not implemented)", Toast.LENGTH_SHORT).show()
        );

        return view;
    }

    private void setupAutoCompleteData() {
        String[] popularGames = { "Chess", "Fortnite", "Overwatch", "League of Legends" };
        String[] countries = { "Israel", "USA", "Canada", "France", "Germany" };

        gameAutoCompleteTextView.setSimpleItems(popularGames);
        countryAutoCompleteTextView.setSimpleItems(countries);
    }

    private void performSearch(String nickname, String game, String country) {
        databaseManager.getAllUsers(new FirebaseDatabaseManager.DataCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                searchResults.clear();

                for (User user : users) {
                    if (user.getUserId() == null || user.getUserId().equals(currentUserId)) continue;

                    boolean matchNickname = nickname.isEmpty() ||
                            (user.getNickname() != null && user.getNickname().equalsIgnoreCase(nickname));

                    boolean matchGame = game.isEmpty() ||
                            (user.getFavoriteGames() != null && user.getFavoriteGames().contains(game));

                    boolean matchCountry = country.isEmpty() ||
                            (user.getCountry() != null && user.getCountry().equalsIgnoreCase(country));

                    if (matchNickname && matchGame && matchCountry) {
                        searchResults.add(user);
                    }
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "נכשלה טעינת המשתמשים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
