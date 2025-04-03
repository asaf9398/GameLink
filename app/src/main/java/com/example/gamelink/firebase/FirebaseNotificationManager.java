package com.example.gamelink.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseNotificationManager {

    private static final String TAG = "FirebaseNotificationMgr";

    public void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Subscribed to topic: " + topic);
                    } else {
                        Log.e(TAG, "Failed to subscribe to topic: " + topic);
                    }
                });
    }

    public void unsubscribeFromTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Unsubscribed from topic: " + topic);
                    } else {
                        Log.e(TAG, "Failed to unsubscribe from topic: " + topic);
                    }
                });
    }

    public void onMessageReceived(String title, String body) {
        Log.d(TAG, "Notification received - Title: " + title + ", Body: " + body);
    }
}
