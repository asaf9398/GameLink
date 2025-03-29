package com.example.gamelink.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.gamelink.R;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class EditProfileActivity extends AppCompatActivity {

    private EditText nicknameInput, ageInput, countryInput;
    private ImageView profileImageView;
    private Button saveButton;
    private String userId;
    private Uri selectedImageUri;
    private FirebaseDatabaseManager databaseManager;

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    profileImageView.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nicknameInput = findViewById(R.id.edit_nickname);
        ageInput = findViewById(R.id.edit_age);
        countryInput = findViewById(R.id.edit_country);
        profileImageView = findViewById(R.id.edit_profile_image);
        saveButton = findViewById(R.id.save_button);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Error in user identification", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = currentUser.getUid();
        databaseManager = new FirebaseDatabaseManager();

        loadUserInfo();

        profileImageView.setOnClickListener(v -> openGallery());
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void loadUserInfo() {
        databaseManager.getAllUsers(new FirebaseDatabaseManager.DataCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                for (User u : users) {
                    if (u.getUserId() != null && u.getUserId().equals(userId)) {
                        nicknameInput.setText(u.getNickname());
                        ageInput.setText(String.valueOf(u.getAge()));
                        countryInput.setText(u.getCountry());

                        if (u.getProfileImageUrl() != null && !u.getProfileImageUrl().isEmpty()) {
                            Glide.with(EditProfileActivity.this)
                                    .load(u.getProfileImageUrl())
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .into(profileImageView);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditProfileActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
                Log.e("EditProfile", "Load user info failed", e);
            }
        });
    }

    private void saveChanges() {
        String newName = nicknameInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim();
        String newCountry = countryInput.getText().toString().trim();

        if (newName.isEmpty() || ageStr.isEmpty() || newCountry.isEmpty()) {
            Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int newAge;
        try {
            newAge = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid age", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("profile_images")
                    .child(userId + ".jpg");

            imageRef.putFile(selectedImageUri)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) throw task.getException();
                        return imageRef.getDownloadUrl();
                    })
                    .addOnSuccessListener(downloadUri ->
                            updateUserProfile(newName, newAge, newCountry, downloadUri.toString()))
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditProfileActivity.this, "Failed to upload the image", Toast.LENGTH_SHORT).show();
                        Log.e("EditProfile", "Image upload failed", e);
                    });
        } else {
            updateUserProfile(newName, newAge, newCountry, null);
        }
    }

    private void updateUserProfile(String name, int age, String country, @Nullable String imageUrl) {
        databaseManager.updateUserProfile(userId, name, age, country, imageUrl, new FirebaseDatabaseManager.OperationCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EditProfileActivity.this, "The profile has been updated!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditProfileActivity.this, "Failed to update.", Toast.LENGTH_SHORT).show();
                Log.e("EditProfile", "Profile update failed", e);
            }
        });
    }
}
