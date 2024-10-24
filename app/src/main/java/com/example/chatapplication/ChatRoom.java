package com.example.chatapplication;

import java.util.List;

public class ChatRoom {
    private String chatId;
    private List<String> participants; // Lista de IDs de los usuarios en el chat
    private String lastMessage; // El último mensaje enviado
    private long lastMessageTimestamp; // Marca de tiempo del último mensaje
    private String chatName; // Nombre de la sala de chat

    public ChatRoom() {
        // Constructor vacío necesario para Firebase
    }

    public ChatRoom(String chatId, List<String> participants, String lastMessage, long lastMessageTimestamp, String chatName) {
        this.chatId = chatId;
        this.participants = participants;
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.chatName = chatName; // Inicializa el nombre de la sala
    }

    // Getters y setters
    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
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

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}
