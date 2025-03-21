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
    private String chatId;            // Unique chat ID
    private String chatName;          // Chat name/title (e.g. "Group #1" / "Private with John")
    private boolean isGroup;          // Is this a group chat or a private 1-to-1?
    private List<String> participants;// userIds of participants

    private String lastMessage;       // Content of the last message
    private long lastMessageTime;     // Timestamp of the last message

    // Empty constructor for Firebase
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

    // Getters + Setters
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

    /**
     * Format lastMessageTime for display (e.g. "03/20 15:42").
     */
    @Exclude
    public String getFormattedLastMessageTime() {
        if (lastMessageTime == 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());
        return sdf.format(new Date(lastMessageTime));
    }

}
