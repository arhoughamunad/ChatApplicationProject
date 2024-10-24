package com.example.chatapplication;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private String Msg;
    private String username;
    private String pictureMsg;
    private String TypeMsg;

    public Message() {

    }

    public Message(String msg, String username, String pictureMsg, String typeMsg) {
        Msg = msg;
        this.username = username;
        this.pictureMsg = pictureMsg;
        this.TypeMsg = typeMsg;
    }


    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPictureMsg() {
        return pictureMsg;
    }

    public void setPictureMsg(String pictureMsg) {
        this.pictureMsg = pictureMsg;
    }

    public String getTypeMsg() {
        return TypeMsg;
    }

    public void setTypeMsg(String typeMsg) {
        TypeMsg = typeMsg;
    }

}
