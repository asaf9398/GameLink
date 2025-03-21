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

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    // נוסיף TextInputLayout/TextInputEditText עבור Nickname:
    private TextInputLayout nicknameTextInputLayout;
    private TextInputEditText nicknameEditText; // אפשר גם MaterialAutoCompleteTextView אך בדר"כ Nickname הוא חופשי

    private TextInputLayout gameTextInputLayout, countryTextInputLayout;
    private MaterialAutoCompleteTextView gameAutoCompleteTextView, countryAutoCompleteTextView;
    private MaterialButton searchButton, advancedFilterButton;
    private RecyclerView searchRecyclerView;

    private UserAdapter userAdapter;
    private List<User> searchResults;
    private FirebaseDatabaseManager databaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // 1) קישור לרכיבי ה־XML
        nicknameTextInputLayout   = view.findViewById(R.id.search_nickname_text_input_layout);
        nicknameEditText          = view.findViewById(R.id.search_nickname_edit_text);

        gameTextInputLayout       = view.findViewById(R.id.search_game_text_input_layout);
        gameAutoCompleteTextView  = view.findViewById(R.id.search_game_autocomplete);

        countryTextInputLayout    = view.findViewById(R.id.search_country_text_input_layout);
        countryAutoCompleteTextView = view.findViewById(R.id.search_country_autocomplete);

        searchButton              = view.findViewById(R.id.search_button);
        advancedFilterButton      = view.findViewById(R.id.search_advanced_filter_button);
        searchRecyclerView        = view.findViewById(R.id.search_recycler_view);

        // 2) אתחול אובייקטים
        databaseManager = new FirebaseDatabaseManager();
        searchResults   = new ArrayList<>();
        userAdapter     = new UserAdapter(searchResults);

        // 3) הגדרת LayoutManager לריסייקלר, וקישור ה־Adapter
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setAdapter(userAdapter);

        // 4) טוענים נתונים ראשוניים לאוטוקומפליט
        setupAutoCompleteData();

        // 5) מאזין לכפתור חיפוש
        searchButton.setOnClickListener(v -> {
            String nickname = nicknameEditText.getText().toString().trim(); // <--- ניק ניים
            String game     = gameAutoCompleteTextView.getText().toString().trim();
            String country  = countryAutoCompleteTextView.getText().toString().trim();

            performSearch(nickname, game, country);
        });

        // 6) מאזין לכפתור פילטרים מתקדמים (כאן רק Toast להדגמה)
        advancedFilterButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Open advanced filters (not implemented)", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    /**
     * לדוגמה: הגדרת רשימת משחקים ומדינות עבור ה-AutoComplete
     */
    private void setupAutoCompleteData() {
        // לדוגמה רשימות מוצעות
        String[] popularGames = { "Chess", "Fortnite", "Overwatch", "League of Legends" };
        String[] countries    = { "Israel", "USA", "Canada", "France", "Germany" };

        // הגדרת האפשרויות ב-AutoCompleteTextView
        gameAutoCompleteTextView.setSimpleItems(popularGames);
        countryAutoCompleteTextView.setSimpleItems(countries);
    }

    /**
     * פונקציית חיפוש לפי Nickname, Game, ו-Country.
     * אם אחד הפרמטרים ריק, נתעלם ממנו; כלומר נעשה חיפוש גמיש.
     */
    private void performSearch(String nickname, String game, String country) {
        databaseManager.getAllUsers(new FirebaseDatabaseManager.DataCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                searchResults.clear();

                for (User user : users) {
                    // דילוג אם אין FavoriteGames
                    if (user.getFavoriteGames() == null) {
                        user.setFavoriteGames(new ArrayList<>());
                    }

                    // תנאי ניק ניים (נניח, אם משאירים ריק - לא בודקים)
                    boolean matchNickname = true;
                    if (!nickname.isEmpty()) {
                        // כאן מניחים שיש שדה nickname ב-User, או שהוא משתמש ב-getName() בתור nickname
                        // לדוגמה נניח ש-user.getName() הוא ה-nickname
                        if (user.getName() == null || !user.getName().equalsIgnoreCase(nickname)) {
                            matchNickname = false;
                        }
                    }

                    // תנאי משחק
                    boolean matchGame = true;
                    if (!game.isEmpty()) {
                        if (!user.getFavoriteGames().contains(game)) {
                            matchGame = false;
                        }
                    }

                    // תנאי מדינה
                    boolean matchCountry = true;
                    if (!country.isEmpty()) {
                        if (user.getCountry() == null || !user.getCountry().equalsIgnoreCase(country)) {
                            matchCountry = false;
                        }
                    }

                    // אם כל התנאים רלוונטיים ומתקיימים
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
