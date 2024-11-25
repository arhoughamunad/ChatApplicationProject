package com.example.chatapplication;

import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private List<MsgRec> messageList;
    private RecyclerView rvMsg;
    private MessageAdapter messageAdapter;
    private EditText txtMsg;
    private Button btnSend;
    private ImageButton btnCam;
    private String chatRoomId;
    private CircleImageView profileImageView;
    private TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        dbHelper = new DatabaseHelper(this);
        rvMsg = findViewById(R.id.rvMsg);
        rvMsg.setLayoutManager(new LinearLayoutManager(this));
        txtMsg = findViewById(R.id.txtMsg);
        btnSend = findViewById(R.id.btnSend);
        btnCam = findViewById(R.id.btnCam);

        chatRoomId = getIntent().getStringExtra("chatRoomId");

        profileImageView = findViewById(R.id.profileImageView);
        userNameTextView = findViewById(R.id.usernameTxt);


        loadMessages();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = txtMsg.getText().toString().trim();
                if (!TextUtils.isEmpty(messageText)) {
                    sendMessage(messageText, null);
                    txtMsg.setText("");// Limpiar campo de texto
                } else {
                    Toast.makeText(ChatActivity.this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir la cámara
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                }
            }
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
                Log.d("ChatActivity", "URL de imagen de Perfil: " + profileImageUrl);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Manejar la imagen capturada
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Se convierte el Bitmap a URI y se envia
            String imageUrl = saveImageToInternalStorage(imageBitmap);
            sendMessage(null, imageUrl);
        }
    }

    // Método para guardar la imagen en el almacenamiento interno y obtener su URI
    private String saveImageToInternalStorage(Bitmap bitmap) {
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        File file = wrapper.getDir("Images", MODE_PRIVATE);
        file = new File(file, "profile.jpg");
        try {
            OutputStream stream = null;
            stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.parse(file.getAbsolutePath()).toString();
    }

    private void loadMessages() {
        messageList = dbHelper.getMessages(chatRoomId);
        messageAdapter = new MessageAdapter(this, messageList); // Pasar contexto y lista de mensajes
        rvMsg.setAdapter(messageAdapter);
    }

    private void sendMessage(String messageText, String imageUrl) {
        MsgSend message;
        User currentUser = dbHelper.getCurrentUser();
        String username = currentUser != null ? currentUser.getUsername() : "Unknown";

        if (imageUrl == null) {
            // Usar el constructor sin imagen
            message = new MsgSend(messageText, username, null, "text", System.currentTimeMillis());
        } else {
            // Usar el constructor con imagen
            message = new MsgSend(messageText, imageUrl, username, null, "image", System.currentTimeMillis());
        }
        dbHelper.addMessage(message, chatRoomId); // Pasar el ID de la sala de chat
        messageList.add(new MsgRec(messageText, username, imageUrl, "1", System.currentTimeMillis()));
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        rvMsg.scrollToPosition(messageList.size() - 1);
    }
}
