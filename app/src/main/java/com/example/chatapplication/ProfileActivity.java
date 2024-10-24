package com.example.chatapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profileImageView; // Imagen de perfil del usuario
    private Button chatButton; // Botón para regresar al chat
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;
    private static final int PHOTO_PROFILE = 1;
    private EditText descriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profileImageView);
        chatButton = findViewById(R.id.ChatButton);
        descriptionEditText = findViewById(R.id.descriptionTextView);

        // Inicializar Firebase Storage y Database
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("imagenes_perfil");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(currentUser.getUid());

        // Click en la foto de perfil para seleccionar una imagen
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PHOTO_PROFILE); // Usar un código de solicitud para identificar la acción
            }
        });

        // Click en el botón para regresar al chat
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Cierra ProfileActivity y regresa a ChatActivity
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PROFILE && resultCode == RESULT_OK) {
            Uri u = data.getData();
            if (u != null) {
                final StorageReference fotoReferencia = storageReference.child(u.getLastPathSegment());

                // Subir la imagen
                fotoReferencia.putFile(u).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Obtener la URL de descarga desde el StorageReference
                        fotoReferencia.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Cargar la nueva imagen de perfil en la vista usando Glide
                                Glide.with(ProfileActivity.this).load(downloadUri).into(profileImageView);
                                // Guardar la URL de la imagen de perfil en la base de datos
                                userRef.child("profileImageUrl").setValue(downloadUri.toString());
                            }
                        });
                    }
                });
            }
        }
    }

}
