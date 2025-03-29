package com.example.gamelink.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.adapters.ChatAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.firebase.FirebaseStorageManager;
import com.example.gamelink.models.Chat;
import com.example.gamelink.models.Message;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private static final String ARG_CHAT_ID = "arg_chat_id";

    private RecyclerView chatRecyclerView;
    private TextInputEditText messageEditText;
    private ImageButton sendButton, attachButton, infoButton;
    private TextView chatTitle;
    private ChatAdapter chatAdapter;
    private List<Message> messages;
    private FirebaseDatabaseManager databaseManager;
    private FirebaseStorageManager storageManager;

    private String chatId;
    private String currentUserId;

    public static ChatFragment newInstance(String chatId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHAT_ID, chatId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatId = getArguments().getString(ARG_CHAT_ID, "unknown_chat_id");
        }
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            currentUserId = "exampleUserId";
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        infoButton       = view.findViewById(R.id.chat_info_button);
        chatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        messageEditText  = view.findViewById(R.id.chat_message_edit_text);
        sendButton       = view.findViewById(R.id.chat_send_button);
        attachButton     = view.findViewById(R.id.chat_attach_button);
        chatTitle        = view.findViewById(R.id.chat_title);

        databaseManager = new FirebaseDatabaseManager();
        storageManager  = new FirebaseStorageManager();

        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages, currentUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);

        loadChatTitleWithParticipants();
        loadMessages();

        sendButton.setOnClickListener(v -> sendMessage());
        attachButton.setOnClickListener(v -> pickFileFromStorage());
        infoButton.setOnClickListener(v -> Toast.makeText(getContext(), "Show chat info", Toast.LENGTH_SHORT).show());

        return view;
    }

    private void loadChatTitleWithParticipants() {
        databaseManager.getChatParticipantNicknames(chatId, new FirebaseDatabaseManager.DataCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> nicknames) {
                if (getActivity() != null && chatTitle != null) {
                    String title = "Chat with: " + String.join(", ", nicknames);
                    chatTitle.setText(title);
                }
            }

            @Override
            public void onFailure(Exception e) {
                chatTitle.setText("Chat");
                Toast.makeText(getContext(), "Failed to load participants", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMessages() {
        if (TextUtils.isEmpty(chatId)) {
            Toast.makeText(getContext(), "Invalid chat ID!", Toast.LENGTH_SHORT).show();
            return;
        }
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

    private void sendMessage() {
        String text = messageEditText.getText().toString().trim();
        if (!text.isEmpty()) {
            long now = System.currentTimeMillis();
            Message newMsg = new Message(currentUserId, text, now);
            databaseManager.addMessageObject(chatId, newMsg);

            messages.add(newMsg);
            chatAdapter.notifyItemInserted(messages.size() - 1);
            chatRecyclerView.scrollToPosition(messages.size() - 1);
            messageEditText.setText("");
        }
    }

    private void pickFileFromStorage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri fileUri = result.getData().getData();
                    if (fileUri != null) {
                        uploadFileToChat(fileUri);
                    }
                }
            });

    private void uploadFileToChat(Uri fileUri) {
        String fileName = System.currentTimeMillis() + "_file";
        storageManager.uploadChatFile(chatId, fileName, fileUri, new FirebaseStorageManager.UploadCallback() {
            @Override
            public void onSuccess(String downloadUrl) {
                Toast.makeText(getContext(), "File uploaded: " + downloadUrl, Toast.LENGTH_SHORT).show();
                long now = System.currentTimeMillis();
                Message fileMsg = new Message(currentUserId, "[File] " + downloadUrl, now);
                databaseManager.addMessageObject(chatId, fileMsg);

                messages.add(fileMsg);
                chatAdapter.notifyItemInserted(messages.size() - 1);
                chatRecyclerView.scrollToPosition(messages.size() - 1);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
