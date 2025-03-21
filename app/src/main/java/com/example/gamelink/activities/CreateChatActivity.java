package com.example.gamelink.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamelink.R;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.Chat;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreateChatActivity extends AppCompatActivity {

    private EditText chatNameEditText;
    private EditText participantsEditText;
    private Button createChatButton;
    private FirebaseDatabaseManager databaseManager;

    // Retrieve current user ID dynamically from FirebaseAuth
    private String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);

        // Link UI elements
        chatNameEditText = findViewById(R.id.chat_name_edit_text);
        participantsEditText = findViewById(R.id.participants_edit_text);
        createChatButton = findViewById(R.id.create_chat_button);

        databaseManager = new FirebaseDatabaseManager();

        createChatButton.setOnClickListener(v -> {
            String chatName = chatNameEditText.getText().toString().trim();
            if (TextUtils.isEmpty(chatName)) {
                Toast.makeText(this, "Please enter a chat name.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate a unique chat ID
            String chatId = UUID.randomUUID().toString();

            // Start with current user as a participant
            List<String> participants = new ArrayList<>();
            participants.add(currentUserId);

            // Read additional participants from the EditText (expecting a comma-separated list)
            String extraParticipantsText = participantsEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(extraParticipantsText)) {
                // Split by comma and trim each entry
                String[] extraParticipants = extraParticipantsText.split(",");
                for (String participant : extraParticipants) {
                    String trimmedId = participant.trim();
                    if (!TextUtils.isEmpty(trimmedId) && !participants.contains(trimmedId)) {
                        participants.add(trimmedId);
                    }
                }
            }

            // Create a new Chat object; here, we assume it's a private chat if there are only 2 participants,
            // or a group chat if more than 2.
            boolean isGroup = participants.size() > 2;
            Chat chat = new Chat(chatId, chatName, isGroup, participants, "", System.currentTimeMillis());

            // Add the chat to Firebase
            databaseManager.addChatObject(chat, new FirebaseDatabaseManager.OperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(CreateChatActivity.this, "Chat created successfully.", Toast.LENGTH_SHORT).show();
                    finish(); // Return to the chat list or previous screen
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(CreateChatActivity.this, "Failed to create chat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
