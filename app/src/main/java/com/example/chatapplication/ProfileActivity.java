package com.example.chatapplication;

import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private ImageView profileImageView;
    private TextView usernameTxt;
    private DatabaseHelper dbHelper;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profileImageView);
        usernameTxt = findViewById(R.id.usernameTextView);
        Button chatButton = findViewById(R.id.ChatButton);
        dbHelper = new DatabaseHelper(this);

        profileImageView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

        chatButton.setOnClickListener(v -> finish());

        loadUserProfile(); // Cargar el perfil del usuario
    }

    private void loadUserProfile() {
        currentUser = dbHelper.getCurrentUser();
        if (currentUser != null) {
            usernameTxt.setText(currentUser.getUsername());
            String profileImageUrl = currentUser.getProfileImageUrl();
            if (profileImageUrl != null) {
                profileImageView.setImageURI(Uri.parse(profileImageUrl));
            } else {
                profileImageView.setImageResource(R.mipmap.ic_launcher); // Imagen por defecto
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            Log.d("ProfileActivity", "Imagen Seleccionada URI: " + selectedImage);
            if (selectedImage != null) {
                try {
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    profileImageView.setImageBitmap(bitmap);
                    // Guardar la imagen en el almacenamiento interno y actualizar la base de datos
                    updateProfileImage(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al seleccionar la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Método para guardar la imagen en el almacenamiento interno y obtener su ruta
    private String saveImageToInternalStorage(Bitmap bitmap) {
        ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
        File file = wrapper.getDir("Images", MODE_PRIVATE);
        file = new File(file, "profile.jpg");
        try {
            OutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    private void updateProfileImage(Bitmap bitmap) {
        String imagePath = saveImageToInternalStorage(bitmap);
        Log.d("ProfileActivity", "Imagen guardada en: " + imagePath);
        User currentUser = dbHelper.getCurrentUser();
        if (currentUser != null) {
            dbHelper.updateUserProfileImage(String.valueOf(currentUser.getId()), imagePath);
            Log.d("ProfileActivity", "Imagen del perfil actualizada para: " + currentUser.getUsername());
        }
    }
}

