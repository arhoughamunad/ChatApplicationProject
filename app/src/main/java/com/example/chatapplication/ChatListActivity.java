package com.example.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private TextView usernameTxt;
    private RecyclerView chatListView;
    private ChatListAdapter adapter;
    private List<String> chatList;

    private FirebaseAuth auth;
    private DatabaseReference userRef;
    private DatabaseReference chatListReference;
    private FirebaseUser currentUser;

    private static final String TAG = "ChatListActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        // Inicializar vistas
        profileImageView = findViewById(R.id.profileImageView);
        usernameTxt = findViewById(R.id.usernameTxt);
        chatListView = findViewById(R.id.chatListView);

        // Configurar RecyclerView
        chatList = new ArrayList<>();
        adapter = new ChatListAdapter(this, chatList);
        chatListView.setLayoutManager(new LinearLayoutManager(this));
        chatListView.setAdapter(adapter);

        // Obtener instancia de FirebaseAuth
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Verificar si el usuario está autenticado
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(currentUser.getUid());
            chatListReference = FirebaseDatabase.getInstance().getReference("ChatRooms");

            // Cargar la información del usuario
            loadUserProfile();

            // Cargar las salas de chat
            loadChatRooms();

        } else {
            // Manejar el caso en que el usuario no esté autenticado
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish(); // Cerrar la actividad
        }

        // Al hacer clic en la imagen de perfil, navegar a ProfileActivity
        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(ChatListActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // Botón para crear un nuevo chat
        Button buttonCreateChat = findViewById(R.id.buttonCreateChat); // Asegúrate de tener este botón en tu layout
        buttonCreateChat.setOnClickListener(v -> {
            createNewChatRoom(); // Llama al método para crear un nuevo chat
        });
    }

    // Método para cargar el perfil del usuario desde Firebase
    private void loadUserProfile() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String profileImageUrl = dataSnapshot.child("profileImageUrl").getValue(String.class);

                    // Actualizar UI con los datos del usuario
                    usernameTxt.setText(username);
                    if (profileImageUrl != null) {
                        Glide.with(ChatListActivity.this).load(profileImageUrl).into(profileImageView);
                    } else {
                        profileImageView.setImageResource(R.mipmap.ic_launcher); // Imagen por defecto
                    }
                } else {
                    Toast.makeText(ChatListActivity.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatListActivity.this, "Error al cargar el perfil: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Método para cargar las salas de chat desde Firebase
    private void loadChatRooms() {
        chatListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String chatRoomName = snapshot.child("chatName").getValue(String.class); // Asegúrate de que este campo exista en Firebase
                    if (chatRoomName != null) {
                        chatList.add(chatRoomName);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatListActivity.this, "Error al cargar las salas de chat: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para crear una nueva sala de chat
    private void createNewChatRoom() {
        // Crea un diálogo para que el usuario ingrese el nombre de la sala
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Crear una nueva sala de chat");

        // Configura el campo de entrada para el nombre de la sala
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String chatName = input.getText().toString().trim(); // Obtiene el nombre de la sala

            if (chatName.isEmpty()) {
                Toast.makeText(ChatListActivity.this, "El nombre de la sala no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            String chatId = chatListReference.push().getKey(); // Generar un ID único para el chat
            List<String> participants = new ArrayList<>();
            participants.add(currentUser.getUid()); // Agregar el usuario actual como participante

            // Crea el objeto ChatRoom con el nombre de la sala
            ChatRoom chatRoom = new ChatRoom(chatId, participants, "", System.currentTimeMillis(), chatName);

            // Guardar el chat en Firebase
            chatListReference.child(chatId).setValue(chatRoom)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ChatListActivity.this, "Chat room created", Toast.LENGTH_SHORT).show();
                            loadChatRooms(); // Recargar las salas de chat para reflejar la nueva sala
                        } else {
                            Toast.makeText(ChatListActivity.this, "Failed to create chat room", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

}


