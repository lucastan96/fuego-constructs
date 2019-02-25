package com.fuego.mobile_ca1.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fuego.mobile_ca1.Classes.User;
import com.fuego.mobile_ca1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private Button btnSignin;
    private EditText emailField;
    private EditText passwordField;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);

        btnSignin = findViewById(R.id.btn_login);
        btnSignin.setOnClickListener(v -> signIn());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    private void signIn() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    DocumentReference ref = db.collection("users").document(auth.getUid());
                    ref.get().addOnSuccessListener(snapshot -> {
                        if (!snapshot.exists()) {
                            User user = new User();
                            ref.set(user);
                        }
                    });

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(LoginActivity.this, "Incorrect Details", Toast.LENGTH_SHORT).show());
    }
}
