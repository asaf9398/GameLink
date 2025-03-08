package com.example.gamelink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamelink.MainActivity;
import com.example.gamelink.R;
import com.example.gamelink.adapters.FavoriteGamesAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FavoriteGamesActivity extends AppCompatActivity {

    private FirebaseDatabaseManager databaseManager;
    private ListView gamesListView;
    private ArrayAdapter<String> adapter;
    private List<String> favoriteGames;
    private String userId;
    private Button backToMainButton; // 🔹 הוספת כפתור חזרה

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_games);

        databaseManager = new FirebaseDatabaseManager();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        EditText gameNameInput = findViewById(R.id.game_name_input);
        Button addGameButton = findViewById(R.id.add_game_button);
        gamesListView = findViewById(R.id.games_list);
        backToMainButton = findViewById(R.id.back_to_main_button);

        favoriteGames = new ArrayList<>();
        adapter = new FavoriteGamesAdapter(this, favoriteGames, userId, databaseManager);
        gamesListView.setAdapter(adapter);

        loadFavoriteGames();

        // כפתור חזרה למסך הראשי
        backToMainButton.setOnClickListener(v -> {
            Intent intent = new Intent(FavoriteGamesActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // סגירת המסך הנוכחי כדי למנוע חזרה לאחור
        });


        addGameButton.setOnClickListener(v -> {
            String gameName = gameNameInput.getText().toString().trim();
            if (!gameName.isEmpty()) {
                String gameId = UUID.randomUUID().toString();
                databaseManager.addFavoriteGame(userId, gameId, gameName);
                favoriteGames.add(gameName);
                adapter.notifyDataSetChanged();
                gameNameInput.setText("");
                Toast.makeText(this, "משחק נוסף למועדפים!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "נא להזין שם משחק", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadFavoriteGames() {
        databaseManager.getUserFavoriteGames(userId, new FirebaseDatabaseManager.DataCallback<List<FirebaseDatabaseManager.GameEntry>>() {
            @Override
            public void onSuccess(List<FirebaseDatabaseManager.GameEntry> data) {
                favoriteGames.clear();
                for (FirebaseDatabaseManager.GameEntry entry : data) {
                    favoriteGames.add(entry.getGameName()); // 🔹 הוספת רק את שם המשחק
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(FavoriteGamesActivity.this, "שגיאה בטעינת המשחקים", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 🔹 הסרת משחק מועדף
    private void removeGameFromFavorites(String gameToRemove, int position) {
        databaseManager.getUserFavoriteGames(userId, new FirebaseDatabaseManager.DataCallback<List<FirebaseDatabaseManager.GameEntry>>() {
            @Override
            public void onSuccess(List<FirebaseDatabaseManager.GameEntry> data) {
                for (FirebaseDatabaseManager.GameEntry entry : data) {
                    if (entry.getGameName().equals(gameToRemove)) {
                        databaseManager.removeFavoriteGame(userId, entry.getGameId());
                        favoriteGames.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(FavoriteGamesActivity.this, "משחק הוסר מהמועדפים", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(FavoriteGamesActivity.this, "שגיאה בהסרת המשחק", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

