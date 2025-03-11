package com.example.gamelink.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.gamelink.models.Game;
import com.example.gamelink.models.Message;
import com.example.gamelink.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  מנהל פעולות מול Firebase Realtime Database
 *  כולל הוספת / קריאת משתמשים, משחקים, הודעות, וכו’.
 */
public class FirebaseDatabaseManager {

    private static final String TAG = "FirebaseDatabaseManager";
    private final DatabaseReference usersRef;
    private final DatabaseReference gamesRef;
    private final DatabaseReference messagesRef;

    public FirebaseDatabaseManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef    = database.getReference("users");
        gamesRef    = database.getReference("games");
        messagesRef = database.getReference("messages");
    }

    // ********************************************************************
    // ***********************  User  *************************************
    // ********************************************************************

    // הוספת משתמש חדש
    public void addUser(User user) {
        usersRef.child(user.getUserId()).setValue(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add user", e));
    }

    // הוספת דירוג למשתמש
    public void addRating(String userId, int rating, String feedback) {
        DatabaseReference ratingsRef = usersRef.child(userId).child("ratings").push();
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("rating", rating);
        ratingData.put("feedback", feedback);
        ratingsRef.setValue(ratingData);
    }

    // קבלת כל המשתמשים
    public void getAllUsers(DataCallback<List<User>> callback) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.exists()) {
                        try {
                            String userId  = userSnapshot.child("userId").getValue(String.class);
                            String name    = userSnapshot.child("name").getValue(String.class);
                            int age        = userSnapshot.child("age").getValue(Integer.class) == null
                                    ? 0
                                    : userSnapshot.child("age").getValue(Integer.class);
                            String country = userSnapshot.child("country").getValue(String.class);

                            List<String> favoriteGames = new ArrayList<>();
                            if (userSnapshot.child("favoriteGames").exists()) {
                                Object gamesObject = userSnapshot.child("favoriteGames").getValue();
                                if (gamesObject instanceof List) {
                                    favoriteGames = (List<String>) gamesObject;
                                } else if (gamesObject instanceof Map) {
                                    Map<String, Object> gamesMap = (Map<String, Object>) gamesObject;
                                    favoriteGames.addAll(gamesMap.keySet());
                                }
                            }

                            User user = new User(userId, name, age, country, favoriteGames);
                            users.add(user);
                        } catch (Exception e) {
                            Log.e("FirebaseError", "Error parsing user data", e);
                        }
                    }
                }
                callback.onSuccess(users);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch users", error.toException());
                callback.onFailure(error.toException());
            }
        });
    }

    // ********************************************************************
    // ***********************  Messages (Chat)  **************************
    // ********************************************************************

    // הוספת אובייקט Message לצ’אט
    public void addMessageObject(String chatId, Message message) {
        String msgId = messagesRef.child(chatId).push().getKey();
        if (msgId != null) {
            messagesRef.child(chatId)
                    .child(msgId)
                    .setValue(message)
                    .addOnSuccessListener(aVoid ->
                            Log.d(TAG, "Message added successfully as object"))
                    .addOnFailureListener(e ->
                            Log.e(TAG, "Failed to add message object", e));
        }
    }

    // טעינת הודעות כ-List<Message>
    public void getMessageObjects(String chatId, DataCallback<List<Message>> callback) {
        messagesRef.child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Message> messages = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Message msg = ds.getValue(Message.class);
                    if (msg != null) {
                        messages.add(msg);
                    }
                }
                callback.onSuccess(messages);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    // ********************************************************************
    // ***********************  Global Games  *****************************
    // ********************************************************************

    /**
     * הוספת אובייקט משחק לנתיב "games/gameId"
     * @param game אובייקט המשחק
     * @param callback ממשק שיודיע על הצלחה או כישלון (OperationCallback)
     */
    public void addGameObject(Game game, OperationCallback callback) {
        gamesRef.child(game.getGameId())
                .setValue(game)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Global game added");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add global game", e);
                    callback.onFailure(e);
                });
    }

    /**
     * משיכת כל המשחקים הגלובליים
     */
    public void getAllGlobalGames(DataCallback<List<Game>> callback) {
        gamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Game> result = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Game g = ds.getValue(Game.class);
                    if(g != null) {
                        result.add(g);
                    }
                }
                callback.onSuccess(result);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    // ********************************************************************
    // ***********************  Favorite Games (User-Specific)  ***********
    // ********************************************************************

    // הוספת משחק מלא לרשימת המועדפים של משתמש (אובייקט Game)
    public void addFavoriteGameObject(String userId, Game game) {
        usersRef.child(userId)
                .child("favoriteGames")
                .child(game.getGameId())
                .setValue(game)
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "Game object added to favorites"))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Failed to add favorite game object", e));
    }

    // טעינת משחקים מועדפים של משתמש כאובייקטי Game
    public void getUserFavoriteGamesAsObjects(String userId, DataCallback<List<Game>> callback) {
        usersRef.child(userId).child("favoriteGames")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Game> games = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Game g = ds.getValue(Game.class);
                            if (g != null) {
                                games.add(g);
                            }
                        }
                        callback.onSuccess(games);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.toException());
                    }
                });
    }

    // מחיקת משחק מועדף לפי ה-gameId
    public void removeFavoriteGameObject(String userId, String gameId) {
        usersRef.child(userId)
                .child("favoriteGames")
                .child(gameId)
                .removeValue()
                .addOnSuccessListener(aVoid ->
                        Log.d(TAG, "Game object removed from favorites"))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Failed to remove favorite game object", e));
    }

    // ********************************************************************
    // ***********************  Deprecated or Examples *********************
    // ********************************************************************
    // לדוגמה (אם עדיין משתמשים בהם):
    public void addGame(String gameId, String gameName) {
        gamesRef.child(gameId).setValue(gameName)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Game added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add game", e));
    }

    // ********************************************************************
    // ************************   Internal Classes  ************************
    // ********************************************************************

    public static class GameEntry {
        private String gameId;
        private String gameName;

        public GameEntry() {}

        public GameEntry(String gameId, String gameName) {
            this.gameId = gameId;
            this.gameName = gameName;
        }

        public String getGameId() { return gameId; }
        public String getGameName() { return gameName; }
    }

    // ממשק בסיסי להחזרת נתונים
    public interface DataCallback<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }

    // **הוספת OperationCallback** לתמיכה בהצלחת או כישלון פעולה
    public interface OperationCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
