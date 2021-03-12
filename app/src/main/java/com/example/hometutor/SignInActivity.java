package com.example.hometutor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {
    private EditText email, password;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.signIn_email);
        password = findViewById(R.id.signIn_password);
        progressBar = findViewById(R.id.signIn_progressbar);

        Button signIn = findViewById(R.id.signin_btn);
        signIn.setOnClickListener(v -> {
            if (networkAvailable()) {
                checkInputValidity();
            } else {
                showToastNotNetworkAvailable();
            }
        });

        TextView newUser = findViewById(R.id.signin_newUser);
        newUser.setOnClickListener(v -> {
            if (networkAvailable()) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(intent);
            } else {
                showToastNotNetworkAvailable();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            moveToMainActivity();
        }
    }

    private void showToastNotNetworkAvailable() {
        Toast.makeText(this, "Network not available!!", Toast.LENGTH_SHORT).show();
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
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
        fireBaseAuthentication(emailString, passwordString);
    }

    private void fireBaseAuthentication(String emailString, String passwordString) {
        mAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnSuccessListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    moveToMainActivity();
                })
                .addOnFailureListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                });
    }


    private boolean networkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null || activeNetworkInfo.isConnected();
    }
}