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

    private String currentUserNickname = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);

        chatNameEditText = findViewById(R.id.chat_name_edit_text);
        participantsEditText = findViewById(R.id.participants_edit_text);
        createChatButton = findViewById(R.id.create_chat_button);

        databaseManager = new FirebaseDatabaseManager();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseManager.getNicknameByUserId(uid, new FirebaseDatabaseManager.DataCallback<String>() {
            @Override
            public void onSuccess(String nickname) {
                currentUserNickname = nickname;
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(CreateChatActivity.this, "Error loading nickname ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        createChatButton.setOnClickListener(v -> {
            String chatName = chatNameEditText.getText().toString().trim();
            if (TextUtils.isEmpty(chatName)) {
                Toast.makeText(this, "Please enter a name for the chat", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentUserNickname == null) {
                Toast.makeText(this, "The nickname is still loading...", Toast.LENGTH_SHORT).show();
                return;
            }

            String chatId = UUID.randomUUID().toString();
            List<String> participants = new ArrayList<>();
            participants.add(currentUserNickname);

            String extraParticipantsText = participantsEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(extraParticipantsText)) {
                String[] extraParticipants = extraParticipantsText.split(",");
                for (String participant : extraParticipants) {
                    String trimmed = participant.trim();
                    if (!TextUtils.isEmpty(trimmed) && !participants.contains(trimmed)) {
                        participants.add(trimmed);
                    }
                }
            }

            boolean isGroup = participants.size() > 2;
            Chat chat = new Chat(chatId, chatName, isGroup, participants, "", System.currentTimeMillis());

            databaseManager.addChatObject(chat, new FirebaseDatabaseManager.OperationCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(CreateChatActivity.this, "Chat created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(CreateChatActivity.this, "Failed to create the chat: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
