package com.example.gamelink.activities;



import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.adapters.GameAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity שמנהל "מאגר משחקים גלובלי" תחת הענף "games" ב-Firebase,
 * מאפשר למשתמש להכניס משחק חדש, ורואה את רשימת המשחקים הקיימים.
 */
public class ManageGamesActivity extends AppCompatActivity {

    private EditText inputGlobalGameName;
    private Button btnAddGlobalGame;
    private RecyclerView globalGamesRecycler;

    private FirebaseDatabaseManager dbManager;
    private GameAdapter gameAdapter;
    private List<Game> allGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_games);

        // קישור ל-Views מה-XML
        inputGlobalGameName = findViewById(R.id.input_global_game_name);
        btnAddGlobalGame    = findViewById(R.id.btn_add_global_game);
        globalGamesRecycler = findViewById(R.id.global_games_recycler);

        // אתחול רשימת המשחקים
        allGames = new ArrayList<>();

        // אתחול Firebase Database Manager
        dbManager = new FirebaseDatabaseManager();

        // Adapter המציג רשימת Game (בהנחה ששינית את GameAdapter לתמוך ב-List<Game>)
        gameAdapter = new GameAdapter(allGames);
        globalGamesRecycler.setLayoutManager(new LinearLayoutManager(this));
        globalGamesRecycler.setAdapter(gameAdapter);

        // טוען את כל המשחקים הקיימים ב-"games"
        loadGlobalGames();

        // כפתור להוספת משחק חדש למאגר הגלובלי
        btnAddGlobalGame.setOnClickListener(v -> {
            String name = inputGlobalGameName.getText().toString().trim();
            if(!name.isEmpty()) {
                String id = java.util.UUID.randomUUID().toString();

                // מוסיפים את המשחק ל-Firebase
                dbManager.addGameObject(new Game(id, name), new FirebaseDatabaseManager.OperationCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(ManageGamesActivity.this, "Game added: " + name, Toast.LENGTH_SHORT).show();
                        inputGlobalGameName.setText("");
                        // לאחר הוספה, אפשר לרענן את הרשימה
                        loadGlobalGames();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(ManageGamesActivity.this, "Failed to add game: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please type a game name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * טוען את רשימת המשחקים הגלובליים מ-Firebase ומעדכן ב-RecyclerView.
     */
    private void loadGlobalGames() {
        dbManager.getAllGlobalGames(new FirebaseDatabaseManager.DataCallback<List<Game>>() {
            @Override
            public void onSuccess(List<Game> data) {
                allGames.clear();
                allGames.addAll(data);
                gameAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(ManageGamesActivity.this, "Failed to load games", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
