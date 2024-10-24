package com.example.chatapplication;

import java.util.Map;

public class MsgSend extends Message {
    private Long time;
    private String photoProfileUrl;

    public MsgSend() {
    }

    public MsgSend(Long time) {
        this.time = time;
    }
    // Constructor sin la imagen
    public MsgSend(String msg, String username, String pictureMsg, String typeMsg, Long time) {
        super(msg, username, pictureMsg, typeMsg);
        this.time = time;
    }
    // Constructor con la imagen
    public MsgSend(String msg, String pictureMsg, String username, String photoProfileUrl, String typeMsg, Long time) {
        super(msg, username, pictureMsg, typeMsg);
        this.time = time;
        this.photoProfileUrl = photoProfileUrl; // Asignar la URL de la foto de perfil
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
    public String getPhotoProfileUrl() {
        return photoProfileUrl;
    }

    public void setPhotoProfileUrl(String photoProfileUrl) {
        this.photoProfileUrl = photoProfileUrl;
    }
}
