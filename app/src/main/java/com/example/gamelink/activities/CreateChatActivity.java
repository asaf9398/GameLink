package com.example.gamelink.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamelink.R;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.Chat;

import java.util.ArrayList;
import java.util.UUID;

public class CreateChatActivity extends AppCompatActivity {

    private EditText chatNameEditText;
    private Button createChatButton;
    private FirebaseDatabaseManager databaseManager;
    // In a real app, retrieve the current user ID from FirebaseAuth
    private String currentUserId = "exampleUserId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);

        chatNameEditText = findViewById(R.id.chat_name_edit_text);
        createChatButton = findViewById(R.id.create_chat_button);
        databaseManager = new FirebaseDatabaseManager();

        createChatButton.setOnClickListener(v -> {
            String chatName = chatNameEditText.getText().toString().trim();
            if (chatName.isEmpty()) {
                Toast.makeText(this, "Please enter a chat name.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Create a new Chat object
            String chatId = UUID.randomUUID().toString();
            ArrayList<String> participants = new ArrayList<>();
            participants.add(currentUserId);
            Chat chat = new Chat(chatId, chatName, false, participants, "", System.currentTimeMillis());
            databaseManager.addChatObject(chat, new FirebaseDatabaseManager.OperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(CreateChatActivity.this, "Chat created successfully.", Toast.LENGTH_SHORT).show();
                    finish(); // Return to the chat list
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(CreateChatActivity.this, "Failed to create chat.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
