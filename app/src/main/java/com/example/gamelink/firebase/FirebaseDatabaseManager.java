package com.example.gamelink.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.gamelink.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseManager {

    private static final String TAG = "FirebaseDatabaseManager";
    private final DatabaseReference usersRef;
    private final DatabaseReference gamesRef;
    private final DatabaseReference messagesRef;

    public FirebaseDatabaseManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        gamesRef = database.getReference("games");
        messagesRef = database.getReference("messages");
    }

    // 🔹 הוספת משתמש חדש
    public void addUser(User user) {
        usersRef.child(user.getUserId()).setValue(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add user", e));
    }

    // 🔹 הוספת דירוג למשתמש (מאפשר כמה דירוגים לכל משתמש)
    public void addRating(String userId, int rating, String feedback) {
        DatabaseReference ratingsRef = usersRef.child(userId).child("ratings").push();
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("rating", rating);
        ratingData.put("feedback", feedback);
        ratingsRef.setValue(ratingData);
    }

    // 🔹 קבלת כל המשתמשים
    public void getAllUsers(DataCallback<List<User>> callback) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.exists()) {
                        try {
                            String userId = userSnapshot.child("userId").getValue(String.class);
                            String name = userSnapshot.child("name").getValue(String.class);
                            int age = userSnapshot.child("age").getValue(Integer.class);
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

    // 🔹 הוספת משחק חדש למערכת
    public void addGame(String gameId, String gameName) {
        gamesRef.child(gameId).setValue(gameName)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Game added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add game", e));
    }

    // 🔹 הוספת הודעה לצ'אט (כעת עם מזהה ייחודי)
    public void addMessage(String chatId, String messageId, String messageContent) {
        messagesRef.child(chatId).child(messageId).setValue(messageContent)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Message added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add message", e));
    }


    // 🔹 קבלת הודעות מצ'אט
    public void getMessages(String chatId, DataCallback<List<String>> callback) {
        messagesRef.child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> messages = new ArrayList<>();
                for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                    String message = messageSnapshot.getValue(String.class);
                    if (message != null) {
                        messages.add(message);
                    }
                }
                callback.onSuccess(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch messages", error.toException());
                callback.onFailure(error.toException());
            }
        });
    }

    // 🔹 הוספת משחק מועדף (נוסף עם מזהה ייחודי)
    public void addFavoriteGame(String userId, String gameId, String gameName) {
        usersRef.child(userId).child("favoriteGames").child(gameId).setValue(gameName)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Game added to favorites"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add favorite game", e));
    }

    // 🔹 הסרת משחק מרשימת המועדפים לפי מזהה
    public void removeFavoriteGame(String userId, String gameName) {
        usersRef.child(userId).child("favoriteGames").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> favoriteGames = new ArrayList<>();
                    for (DataSnapshot gameSnapshot : snapshot.getChildren()) {
                        String game = gameSnapshot.getValue(String.class);
                        if (game != null && !game.equals(gameName)) {
                            favoriteGames.add(game);
                        }
                    }
                    // עדכון הרשימה החדשה בלי המשחק שנמחק
                    usersRef.child(userId).child("favoriteGames").setValue(favoriteGames)
                            .addOnSuccessListener(aVoid -> Log.d(TAG, "Game removed from favorites"))
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to remove favorite game", e));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch favorite games", error.toException());
            }
        });
    }


    // 🔹 קבלת משחקים מועדפים עם מזהה (מחלקת עזר GameEntry)
    public void getUserFavoriteGames(String userId, DataCallback<List<GameEntry>> callback) {
        usersRef.child(userId).child("favoriteGames").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<GameEntry> games = new ArrayList<>();

                if (snapshot.exists()) {
                    for (DataSnapshot gameSnapshot : snapshot.getChildren()) {
                        String gameId = gameSnapshot.getKey();  // המפתח הוא מזהה המשחק
                        String gameName = gameSnapshot.getValue(String.class);  // הערך הוא שם המשחק

                        if (gameId != null && gameName != null) {
                            games.add(new GameEntry(gameId, gameName));
                        }
                    }
                }
                callback.onSuccess(games);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to fetch favorite games", error.toException());
                callback.onFailure(error.toException());
            }
        });
    }




    // 🔹 מחלקת עזר להצגת משחק עם מזהה + שם
    public static class GameEntry {
        private String gameId;
        private String gameName;

        public GameEntry() {}

        public GameEntry(String gameId, String gameName) {
            this.gameId = gameId;
            this.gameName = gameName;
        }

        public String getGameId() {
            return gameId;
        }

        public String getGameName() {
            return gameName;
        }
    }

    // 🔹 ממשק להחזרת נתונים
    public interface DataCallback<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }
}
