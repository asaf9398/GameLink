package com.example.gamelink.firebase;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.gamelink.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDatabaseManager {

    private static final String TAG = "FirebaseDatabaseManager";
    private final DatabaseReference usersRef;
    private final DatabaseReference gamesRef;
    private final DatabaseReference messagesRef;

    public FirebaseDatabaseManager(Context context) {
        FirebaseApp.initializeApp(context); // ✅ ודא ש-Firebase מאותחל
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        gamesRef = database.getReference("games");
        messagesRef = database.getReference("messages");
    }

    // Add a new user
    public void addUser(User user) {
        usersRef.child(user.getUserId()).setValue(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add user", e));
    }

    public void addRating(String userId, int rating, String feedback) {
        DatabaseReference ratingsRef = FirebaseDatabase.getInstance().getReference("ratings").child(userId);
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("rating", rating);
        ratingData.put("feedback", feedback);
        ratingsRef.setValue(ratingData);
    }


    // Get all users
    public void getAllUsers(DataCallback<List<User>> callback) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        users.add(user);
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

    // Add a game
    public void addGame(String gameId, String gameName) {
        gamesRef.child(gameId).setValue(gameName)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Game added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add game", e));
    }

    // Add a message to chat
    public void addMessage(String chatId, String messageId, String messageContent) {
        messagesRef.child(chatId).child(messageId).setValue(messageContent)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Message added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add message", e));
    }

    // Get messages from a chat
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

    // Callback interface for data retrieval
    public interface DataCallback<T> {
        void onSuccess(T data);

        void onFailure(Exception e);
    }
}
