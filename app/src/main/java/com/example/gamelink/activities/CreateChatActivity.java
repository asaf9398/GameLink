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

    private String currentUserNickname = null; // במקום currentUserId

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);

        chatNameEditText = findViewById(R.id.chat_name_edit_text);
        participantsEditText = findViewById(R.id.participants_edit_text);
        createChatButton = findViewById(R.id.create_chat_button);

        databaseManager = new FirebaseDatabaseManager();

        // 1) נטען את ה־nickname של המשתמש הנוכחי
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseManager.getNicknameByUserId(uid, new FirebaseDatabaseManager.DataCallback<String>() {
            @Override
            public void onSuccess(String nickname) {
                currentUserNickname = nickname;
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(CreateChatActivity.this, "שגיאה בטעינת הכינוי", Toast.LENGTH_SHORT).show();
                finish(); // אפשרות: לא מאפשר יצירת צ'אט אם אין כינוי
            }
        });

        createChatButton.setOnClickListener(v -> {
            String chatName = chatNameEditText.getText().toString().trim();
            if (TextUtils.isEmpty(chatName)) {
                Toast.makeText(this, "נא להזין שם לצ'אט.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (currentUserNickname == null) {
                Toast.makeText(this, "טעינת הכינוי עדיין נמשכת...", Toast.LENGTH_SHORT).show();
                return;
            }

            String chatId = UUID.randomUUID().toString();
            List<String> participants = new ArrayList<>();
            participants.add(currentUserNickname); // הכינוי של המשתמש הנוכחי

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
                    Toast.makeText(CreateChatActivity.this, "צ'אט נוצר בהצלחה", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(CreateChatActivity.this, "נכשל ביצירת הצ'אט: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
