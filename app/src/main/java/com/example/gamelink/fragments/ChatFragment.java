package com.example.gamelink.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.adapters.ChatAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private ImageButton sendButton;

    private ChatAdapter chatAdapter;
    private List<String> messages;
    private FirebaseDatabaseManager databaseManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        messageEditText = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.send_button);

        databaseManager = new FirebaseDatabaseManager();

        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        String chatId = "example_chat_id"; // Example chat ID
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

        return view;
    }
}

