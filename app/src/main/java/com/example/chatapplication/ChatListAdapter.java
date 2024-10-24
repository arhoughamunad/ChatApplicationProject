package com.example.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    private Context context;
    private List<String> chatList;

    // Constructor
    public ChatListAdapter(Context context, List<String> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño del ítem para la lista de chats
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        // Obtener el nombre de la sala de chat y configurarlo en el TextView
        String chatRoomName = chatList.get(position);
        holder.chatRoomName.setText(chatRoomName);

        // Configurar el clic para navegar a ChatActivity con el nombre de la sala de chat
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("chatRoomName", chatRoomName);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    // ViewHolder para la lista de chats
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView chatRoomName;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatRoomName = itemView.findViewById(android.R.id.text1); // Usa el TextView predeterminado
        }
    }
}
