package com.example.chatapplication;

public class MsgRec extends Message {
    private Long time;

    public MsgRec() {
    }

    public MsgRec(Long time) {
        this.time = time;
    }

    public MsgRec(String msg, String username, String pictureMsg, String typeMsg, Long time) {
        super(msg, username, pictureMsg, typeMsg);
        this.time = time;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
