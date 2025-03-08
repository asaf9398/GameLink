package com.example.gamelink.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.adapters.ChatAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;

    private ChatAdapter chatAdapter;
    private List<String> messages;
    private FirebaseDatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize UI components
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        messageEditText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);

        // Initialize Firebase Database Manager
        databaseManager = new FirebaseDatabaseManager(this);

        // Initialize message list and adapter
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Load chat messages from Firebase
        String chatId = "example_chat_id"; // Example chat ID, replace with actual ID
        databaseManager.getMessages(chatId, new FirebaseDatabaseManager.DataCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                messages.addAll(data);
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });

        // Handle send button click
        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString().trim();
            if (!message.isEmpty()) {
                databaseManager.addMessage(chatId, String.valueOf(System.currentTimeMillis()), message);
                messages.add(message);
                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messages.size() - 1);
                messageEditText.setText("");
            }
        });
    }
}
