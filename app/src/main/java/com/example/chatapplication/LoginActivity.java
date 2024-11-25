package com.example.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    //Definir variables
    private EditText editTextEmail, editTextPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        Button buttonLogin = findViewById(R.id.loginButton);
        buttonLogin.setOnClickListener(v -> loginUser());

        Button buttonRegister = findViewById(R.id.registerButton);
        buttonRegister.setOnClickListener(v -> navigateToRegister());
    }

    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dbHelper.checkUser(email, password)) {
            User user = dbHelper.getUserByEmail(email);
            if (user != null) {
                dbHelper.saveCurrentUserId(String.valueOf(user.getId()));
                Intent intent = new Intent(LoginActivity.this, ChatListActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Login failed: User not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
