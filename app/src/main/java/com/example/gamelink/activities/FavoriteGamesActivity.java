package com.example.gamelink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamelink.R;
import com.example.gamelink.adapters.FavoriteGamesAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.Game;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FavoriteGamesActivity extends AppCompatActivity {

    private FirebaseDatabaseManager databaseManager;
    private ListView gamesListView;
    private FavoriteGamesAdapter adapter;
    private List<Game> favoriteGames;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_games);

        databaseManager = new FirebaseDatabaseManager();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        EditText gameNameInput = findViewById(R.id.game_name_input);
        Button addGameButton    = findViewById(R.id.add_game_button);
        gamesListView           = findViewById(R.id.games_list);
        Button backToMainButton = findViewById(R.id.back_to_main_button);

        favoriteGames = new ArrayList<>();
        // כאן adapter מקבל רשימת אובייקטי Game
        adapter = new FavoriteGamesAdapter(this, favoriteGames, userId, databaseManager);
        gamesListView.setAdapter(adapter);

        loadFavoriteGames();

        // כפתור חזרה למסך הראשי
        backToMainButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        addGameButton.setOnClickListener(v -> {
            String gameName = gameNameInput.getText().toString().trim();
            if (!gameName.isEmpty()) {
                String gameId = java.util.UUID.randomUUID().toString();
                Game game = new Game(gameId, gameName);
                // הוספת אובייקט Game כמועדף
                databaseManager.addFavoriteGameObject(userId, game);

                favoriteGames.add(game);
                adapter.notifyDataSetChanged();
                gameNameInput.setText("");
                Toast.makeText(this, "משחק נוסף למועדפים!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "נא להזין שם משחק", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFavoriteGames() {
        databaseManager.getUserFavoriteGamesAsObjects(userId, new FirebaseDatabaseManager.DataCallback<List<Game>>() {
            @Override
            public void onSuccess(List<Game> data) {
                favoriteGames.clear();
                favoriteGames.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(FavoriteGamesActivity.this, "שגיאה בטעינת המשחקים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


