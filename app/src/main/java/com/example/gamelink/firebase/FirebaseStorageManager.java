package com.example.gamelink.firebase;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirebaseStorageManager {

    private static final String TAG = "FirebaseStorageManager";
    private final StorageReference storageReference;

    public FirebaseStorageManager() {
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    public void uploadProfilePicture(String userId, Uri fileUri, UploadCallback callback) {
        StorageReference profileRef = storageReference.child("profile_pictures/" + userId + ".jpg");

        profileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d(TAG, "Profile picture uploaded successfully: " + uri.toString());
                        callback.onSuccess(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload profile picture", e);
                    callback.onFailure(e);
                });
    }

    public void uploadChatFile(String chatId, String fileName, Uri fileUri, UploadCallback callback) {
        StorageReference chatFileRef = storageReference.child("chat_files/" + chatId + "/" + fileName);

        chatFileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    chatFileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Log.d(TAG, "Chat file uploaded successfully: " + uri.toString());
                        callback.onSuccess(uri.toString());
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload chat file", e);
                    callback.onFailure(e);
                });
    }

    public interface UploadCallback {
        void onSuccess(String downloadUrl);

        void onFailure(Exception e);
    }
}
