package com.example.gamelink.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseNotificationManager {

    private static final String TAG = "FirebaseNotificationMgr";

    // Subscribe to a topic for receiving notifications
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

    // Unsubscribe from a topic
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

    // Handle received notification (can be used in the activity or service)
    public void onMessageReceived(String title, String body) {
        Log.d(TAG, "Notification received - Title: " + title + ", Body: " + body);
        // You can add custom logic here, like showing a local notification
    }
}
