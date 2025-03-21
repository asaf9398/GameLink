package com.example.gamelink.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.gamelink.models.Chat;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages operations with Firebase Realtime Database, including users, games, messages, and chats.
 */
public class FirebaseDatabaseManager {

    private static final String TAG = "FirebaseDatabaseManager";

    // References in the Realtime Database
    private final DatabaseReference usersRef;
    private final DatabaseReference gamesRef;
    private final DatabaseReference messagesRef;
    private final DatabaseReference chatsRef;

    public FirebaseDatabaseManager() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        usersRef    = database.getReference("users");
        gamesRef    = database.getReference("games");
        messagesRef = database.getReference("messages");
        chatsRef    = database.getReference("chats");
    }

    // ==========================================
    // ========== User Methods ==================
    // ==========================================

    /**
     * Adds a new user to "users/{userId}".
     */
    public void addUser(User user) {
        usersRef.child(user.getUserId()).setValue(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add user", e));
    }

    /**
     * Adds a rating/feedback to a user under "users/{userId}/ratings".
     */
    public void addRating(String userId, int rating, String feedback) {
        DatabaseReference ratingsRef = usersRef
                .child(userId)
                .child("ratings")
                .push();

        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("rating", rating);
        ratingData.put("feedback", feedback);

        ratingsRef.setValue(ratingData);
    }

    /**
     * Fetches all users from "users" once.
     */
    public void getAllUsers(DataCallback<List<User>> callback) {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.exists()) {
                        try {
                            // Extract user fields
                            String userId   = userSnapshot.child("userId").getValue(String.class);
                            //String name     = userSnapshot.child("name").getValue(String.class);
                            String nickname = userSnapshot.child("nickname").getValue(String.class);

                            Integer ageVal  = userSnapshot.child("age").getValue(Integer.class);
                            int age         = (ageVal == null) ? 0 : ageVal;

                            String country  = userSnapshot.child("country").getValue(String.class);

                            // Favorite games
                            List<String> favoriteGames = new ArrayList<>();
                            if (userSnapshot.child("favoriteGames").exists()) {
                                Object gamesObject = userSnapshot.child("favoriteGames").getValue();
                                if (gamesObject instanceof List) {
                                    // If stored as a raw list
                                    favoriteGames = (List<String>) gamesObject;
                                } else if (gamesObject instanceof Map) {
                                    // If stored as a map of {gameId: gameName} or similar
                                    Map<String, Object> gamesMap = (Map<String, Object>) gamesObject;
                                    favoriteGames.addAll(gamesMap.keySet());
                                }
                            }

                            // Create the user object
                            User user = new User(userId, nickname, age, country, favoriteGames);
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


    // ==========================================
    // ========== Messages (Chat) ===============
    // ==========================================

    /**
     * Adds a new Message object to "messages/{chatId}/{msgId}".
     */
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

    /**
     * Loads the list of Message objects from "messages/{chatId}" once.
     */
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


    // ==========================================
    // ========== Global Games ==================
    // ==========================================

    /**
     * Adds a new Game object to "games/{gameId}".
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
     * Retrieves all Game objects from "games".
     */
    public void getAllGlobalGames(DataCallback<List<Game>> callback) {
        gamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Game> result = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Game g = ds.getValue(Game.class);
                    if (g != null) {
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


    // ==========================================
    // ========== Favorite Games ================
    // ==========================================

    /**
     * Adds a Game to the user's favorite list: "users/{userId}/favoriteGames/{gameId}".
     */
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

    /**
     * Loads the user's favorite games as Game objects from "users/{userId}/favoriteGames".
     */
    public void getUserFavoriteGamesAsObjects(String userId, DataCallback<List<Game>> callback) {
        usersRef.child(userId)
                .child("favoriteGames")
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

    /**
     * Removes a game from a user's favorites: "users/{userId}/favoriteGames/{gameId}".
     */
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


    // ==========================================
    // ========== Chat Methods ==================
    // ==========================================

    /**
     * Adds a new Chat object to "chats/{chatId}".
     */
    public void addChatObject(Chat chat, OperationCallback callback) {
        chatsRef.child(chat.getChatId())
                .setValue(chat)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Chat added successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to add chat", e);
                    callback.onFailure(e);
                });
    }

    /**
     * Loads the list of Chats in which a user participates:
     * "chats" -> filter by chat.getParticipants().contains(userId)
     */
    public void getUserChats(String userId, DataCallback<List<Chat>> callback) {
        chatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Chat> chats = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    if (chat != null
                            && chat.getParticipants() != null
                            && chat.getParticipants().contains(userId)) {
                        chats.add(chat);
                    }
                }
                callback.onSuccess(chats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }


    // ==========================================
    // ========== Deprecated Example ============
    // ==========================================

    /**
     * An older example for adding a game by just ID/name string.
     * Not recommended if you store full Game objects.
     */
    public void addGame(String gameId, String gameName) {
        gamesRef.child(gameId)
                .setValue(gameName)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Game added successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to add game", e));
    }

    /**
     * Fetch nickname of a user by userId (UID).
     */
    public void getNicknameByUserId(String userId, DataCallback<String> callback) {
        usersRef.child(userId).child("nickname")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nickname = snapshot.getValue(String.class);
                        if (nickname != null) {
                            callback.onSuccess(nickname);
                        } else {
                            callback.onFailure(new Exception("Nickname not found"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.toException());
                    }
                });
    }

    public void getChatParticipantNicknames(String chatId, DataCallback<List<String>> callback) {
        chatsRef.child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat chat = snapshot.getValue(Chat.class);
                if (chat == null || chat.getParticipants() == null) {
                    callback.onFailure(new Exception("No chat or participants"));
                    return;
                }

                List<String> uids = chat.getParticipants();
                List<String> nicknames = new ArrayList<>();
                AtomicInteger count = new AtomicInteger(uids.size());

                for (String uid : uids) {
                    usersRef.child(uid).child("nickname").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot ds) {
                            String nickname = ds.getValue(String.class);
                            nicknames.add(nickname != null ? nickname : uid);
                            if (count.decrementAndGet() == 0) {
                                callback.onSuccess(nicknames);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            if (count.decrementAndGet() == 0) {
                                callback.onSuccess(nicknames); // return what we got
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }

    public void addFriend(String userId, String friendId, OperationCallback callback) {
        usersRef.child(userId).child("friends").child(friendId).setValue(true)
                .addOnSuccessListener(aVoid -> usersRef.child(friendId).child("friends").child(userId).setValue(true)
                        .addOnSuccessListener(aVoid2 -> callback.onSuccess())
                        .addOnFailureListener(callback::onFailure))
                .addOnFailureListener(callback::onFailure);
    }

    public void getUserFriends(String userId, DataCallback<List<User>> callback) {
        usersRef.child(userId).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> friendIds = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    friendIds.add(ds.getKey());
                }

                getAllUsers(new DataCallback<List<User>>() {
                    @Override
                    public void onSuccess(List<User> users) {
                        List<User> friendUsers = new ArrayList<>();
                        for (User u : users) {
                            if (friendIds.contains(u.getUserId())) {
                                friendUsers.add(u);
                            }
                        }
                        callback.onSuccess(friendUsers);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.toException());
            }
        });
    }




    // ==========================================
    // ========== Internal Classes ==============
    // ==========================================

    /**
     * Simple helper class if you want to store game references as {gameId, gameName} pairs.
     */
    public static class GameEntry {
        private String gameId;
        private String gameName;

        public GameEntry() { }

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

    /**
     * Generic data callback for async fetch operations.
     */
    public interface DataCallback<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }

    /**
     * A callback for operations (e.g. add/update) that return success/failure only.
     */
    public interface OperationCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}
