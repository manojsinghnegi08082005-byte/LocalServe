package com.localserve.user;

public class ChatMessage {
    public int senderId;
    public String message;

    public ChatMessage(int senderId, String message) {
        this.senderId = senderId;
        this.message = message;
    }
}
