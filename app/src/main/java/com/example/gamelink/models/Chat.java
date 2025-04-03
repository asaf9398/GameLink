package com.example.gamelink.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.google.firebase.database.Exclude;


/**
 * Represents a Chat/Conversation room.
 */
public class Chat {
    private String chatId;
    private String chatName;
    private boolean isGroup;
    private List<String> participants;

    private String lastMessage;
    private long lastMessageTime;

    public Chat() { }

    public Chat(String chatId, String chatName, boolean isGroup,
                List<String> participants, String lastMessage, long lastMessageTime) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.isGroup = isGroup;
        this.participants = participants;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatName() {
        return chatName;
    }
    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public boolean isGroup() {
        return isGroup;
    }
    public void setGroup(boolean group) {
        isGroup = group;
    }

    public List<String> getParticipants() {
        return participants;
    }
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getLastMessage() {
        return lastMessage;
    }
    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }
    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    @Exclude
    public String getFormattedLastMessageTime() {
        if (lastMessageTime == 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(lastMessageTime));
    }

}
