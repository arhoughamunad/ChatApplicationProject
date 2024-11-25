package com.example.chatapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView chatListView;
    private ChatListAdapter adapter;
    private List<ChatRoom> chatList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private TextView noChatsText; // Para mostrar el mensaje cuando no hay chats

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        // Inicialización de componentes
        dbHelper = new DatabaseHelper(this);
        chatListView = findViewById(R.id.chatListView);
        noChatsText = findViewById(R.id.noChatsText); // Este es el TextView que muestra el mensaje
        CircleImageView profileImageView = findViewById(R.id.profileImageView); // Imagen de perfil
        // Configuración del RecyclerView
        chatListView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatListAdapter(this, chatList);
        chatListView.setAdapter(adapter);
        // Botón para crear una nueva sala de chat
        Button createChatButton = findViewById(R.id.buttonCreateChat);
        createChatButton.setOnClickListener(v -> openCreateChatDialog());
        // Listener para la imagen de perfil
        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(ChatListActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        // Cargar las salas de chat desde la base de datos
        loadChatRooms();
        // Configurar el listener para los elementos de la lista
        adapter.setOnItemClickListener(chatRoom -> {
            Intent intent = new Intent(ChatListActivity.this, ChatActivity.class);
            intent.putExtra("chatRoomId", chatRoom.getChatId());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfileImage();
        loadUsername();
    }

    //Cargar la imagen del usuario desde la base de datos
    private void loadUserProfileImage() {
        CircleImageView profileImageView = findViewById(R.id.profileImageView);
        User currentUser = dbHelper.getCurrentUser();
        if (currentUser != null) {
            String profileImageUrl = currentUser.getProfileImageUrl();
            if (profileImageUrl != null) {
                profileImageView.setImageURI(Uri.parse(profileImageUrl));
            } else {
                profileImageView.setImageResource(R.mipmap.ic_launcher); // Imagen por defecto
            }
        }
    }

    //Cargar el nombre del usuario desde la base de datos
    private void loadUsername() {
        TextView usernameTxt = findViewById(R.id.usernameTxt);
        User currentUser = dbHelper.getCurrentUser();
        if (currentUser != null) {
            usernameTxt.setText(currentUser.getUsername());
        } else {
            usernameTxt.setText("Usuario desconocido"); // Texto por defecto
        }
    }
    private void loadChatRooms() {
        List<ChatRoom> chatRooms = dbHelper.getChatRooms();
        if (chatRooms.isEmpty()) {
            // Si no hay salas de chat, mostrar el mensaje de "No tienes chats"
            noChatsText.setVisibility(View.VISIBLE);
            chatListView.setVisibility(View.GONE); // Ocultar el RecyclerView si no hay chats
        } else {
            // Si hay salas de chat, mostrar la lista
            noChatsText.setVisibility(View.GONE);
            chatListView.setVisibility(View.VISIBLE);
            chatList.clear();
            chatList.addAll(chatRooms);
            adapter.notifyDataSetChanged();
        }
    }

    private void openCreateChatDialog() {
        // Aquí abres un diálogo donde el usuario puede ingresar un nombre para la nueva sala de chat
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Crear nueva sala de chat");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Crear", (dialog, which) -> {
            String chatName = input.getText().toString().trim();
            if (!chatName.isEmpty()) {
                // Crear la nueva sala de chat en la base de datos
                String chatId = UUID.randomUUID().toString(); // Generar un ID único para la sala
                ChatRoom newChatRoom = new ChatRoom(chatId, new ArrayList<>(), "", System.currentTimeMillis(), chatName);
                dbHelper.createChatRoom(newChatRoom); // Método que agrega la nueva sala a la base de datos
                // Actualizar la lista de chats
                loadChatRooms();
            } else {
                Toast.makeText(this, "Por favor ingrese un nombre para la sala", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
