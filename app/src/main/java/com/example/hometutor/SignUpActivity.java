package com.example.hometutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    private EditText email, password;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        email = findViewById(R.id.signup_email);
        password = findViewById(R.id.signup_password);
        Button signUp = findViewById(R.id.signUp_btn);

        signUp.setOnClickListener(v->checkInputValidity());
    }

    private void checkInputValidity() {
        String emailString = email.getText().toString().trim();
        String passwordString = password.getText().toString().trim();

        if (emailString.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.setError("Invalid Email!");
            email.requestFocus();
            return;
        }

        if (passwordString.isEmpty() || passwordString.length() < 6) {
            password.setError("6 digit password required");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        signUpNewUser(emailString, passwordString);
    }

    private void signUpNewUser(String emailString, String passwordString) {
        mAuth.createUserWithEmailAndPassword(emailString, passwordString)
                .addOnSuccessListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(this, task->{
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "SignUp Failed!!", Toast.LENGTH_SHORT).show();
                });
    }
}