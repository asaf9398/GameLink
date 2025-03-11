package com.example.gamelink.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.adapters.ChatAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.Message;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView chatRecyclerView;
    private TextInputEditText messageEditText;
    private ImageButton sendButton, attachButton;
    private View topBarInfoButton;  // כפתור אייקון אינפו/פרופיל בחלק העליון (אופציונלי)

    private ChatAdapter chatAdapter;
    private List<Message> messages;
    private FirebaseDatabaseManager databaseManager;

    // מזהים לצורך הדוגמה. בפועל, יש לשלוף מ-FirebaseAuth או מה-Bundle:
    private String currentUserId = "exampleUserId";
    private String chatId = "example_chat_id";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // אזור העליון - טייטל "Chat" וכפתור מידע/פרופיל
        topBarInfoButton = view.findViewById(R.id.chat_info_button);

        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        messageEditText  = view.findViewById(R.id.chat_message_edit_text);
        sendButton       = view.findViewById(R.id.chat_send_button);
        attachButton     = view.findViewById(R.id.chat_attach_button);

        databaseManager  = new FirebaseDatabaseManager();

        messages = new ArrayList<>();

        // לדוגמה אם יש משתמש מחובר:
        // currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        chatAdapter = new ChatAdapter(messages, currentUserId);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        loadMessages();

        sendButton.setOnClickListener(v -> {
            String text = messageEditText.getText().toString().trim();
            if (!text.isEmpty()) {
                // יוצרים אובייקט הודעה חדש
                Message newMsg = new Message(currentUserId, text, System.currentTimeMillis());
                // שומרים ב-Firebase
                databaseManager.addMessageObject(chatId, newMsg);

                // עדכון רשימה מקומית
                messages.add(newMsg);
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messages.size() - 1);
                messageEditText.setText("");
            }
        });

        attachButton.setOnClickListener(v -> {
            // כאן אפשר לממש פתיחת גלריה / מצלמה / צרוף קובץ
            Toast.makeText(getContext(), "Attach feature not implemented yet", Toast.LENGTH_SHORT).show();
        });

        topBarInfoButton.setOnClickListener(v -> {
            // לחיצה על כפתור מידע/פרופיל
            Toast.makeText(getContext(), "Open chat info / profile screen", Toast.LENGTH_SHORT).show();
            // אפשר לפתוח כאן Activity/Fragment נוסף עם פרטי המשתמש / חדר
        });

        return view;
    }

    private void loadMessages() {
        databaseManager.getMessageObjects(chatId, new FirebaseDatabaseManager.DataCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> data) {
                messages.clear();
                messages.addAll(data);
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
