package com.example.gamelink.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gamelink.R;
import com.example.gamelink.activities.CreateChatActivity;
import com.example.gamelink.adapters.ChatListAdapter;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.Chat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatListFragment extends Fragment {

    private RecyclerView chatsRecyclerView;
    private FloatingActionButton newChatFab;
    private ChatListAdapter adapter;
    private List<Chat> chatList;
    private FirebaseDatabaseManager databaseManager;

    private String currentUserNickname = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats_list, container, false);

        chatsRecyclerView = view.findViewById(R.id.chats_list_recycler);
        newChatFab = view.findViewById(R.id.new_chat_fab);

        databaseManager = new FirebaseDatabaseManager();
        chatList = new ArrayList<>();
        adapter = new ChatListAdapter(chatList, chat -> openChat(chat.getChatId()));

        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatsRecyclerView.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseManager.getNicknameByUserId(uid, new FirebaseDatabaseManager.DataCallback<String>() {
            @Override
            public void onSuccess(String nickname) {
                currentUserNickname = nickname;
                loadUserChats();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Error loading the nickname", Toast.LENGTH_SHORT).show();
            }
        });

        newChatFab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), CreateChatActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentUserNickname != null) {
            loadUserChats();
        }
    }

    private void loadUserChats() {
        databaseManager.getUserChats(currentUserNickname, new FirebaseDatabaseManager.DataCallback<List<Chat>>() {
            @Override
            public void onSuccess(List<Chat> data) {
                chatList.clear();
                chatList.addAll(data);
                if (chatList.isEmpty()) {
                    Toast.makeText(getContext(), "No active chats found.", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to load chats.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openChat(String chatId) {
        ChatFragment chatFragment = ChatFragment.newInstance(chatId);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, chatFragment)
                .addToBackStack(null)
                .commit();
    }
}
