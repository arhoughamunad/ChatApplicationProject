package com.example.chatapplication;

import android.widget.TextView;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.transition.Hold;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class HolderMessage extends RecyclerView.ViewHolder {
    private TextView username;
    private TextView Msg;
    private TextView Time;
    private CircleImageView pictureMsg;

    public HolderMessage(View itemView){
        super(itemView);
        username=(TextView) itemView.findViewById(R.id.usernameMsg);
        Msg=(TextView) itemView.findViewById(R.id.MsgMsg);
        Time=(TextView) itemView.findViewById(R.id.timeMsg);
        pictureMsg=(CircleImageView)  itemView.findViewById(R.id.pictureMsg);
    }

    public TextView getUsername() {
        return username;
    }

    public void setUsername(TextView username) {
        this.username = username;
    }

    public TextView getMsg() {
        return Msg;
    }

    public void setMsg(TextView msg) {
        Msg = msg;
    }

    public TextView getTime() {
        return Time;
    }

    public void setTime(TextView time) {
        Time = time;
    }

    public CircleImageView getPictureMsg() {
        return pictureMsg;
    }

    public void setPictureMsg(CircleImageView pictureMsg) {
        this.pictureMsg = pictureMsg;
    }
}
