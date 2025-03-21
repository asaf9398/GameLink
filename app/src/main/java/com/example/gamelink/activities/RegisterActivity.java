package com.example.gamelink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamelink.R;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private EditText nicknameEditText;  // *** ADDED
    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // אתחול Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // *** ADDED - מצביעים גם על nicknameEditText
        nicknameEditText = findViewById(R.id.nickname);
        emailEditText    = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        registerButton   = findViewById(R.id.register_button);
        progressBar      = findViewById(R.id.progress_bar);

        // כפתור הרשמה
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        // *** ADDED
        String nickname = nicknameEditText.getText().toString().trim();
        String email    = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (nickname.isEmpty() || email.isEmpty() || password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "נא למלא את כל השדות. סיסמה חייבת להיות לפחות 6 תווים!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser fUser = mAuth.getCurrentUser();
                        if (fUser != null) {
                            // *** יוצרים אובייקט User חדש ושומרים ב-Firebase
                            String uid = fUser.getUid();
                            // ניתן לשים ערכי ברירת מחדל ל-name/age וכו’ או לקבל טופס גדול יותר
                            User newUser = new User(
                                    uid,
                                    /*name=*/"",  // עד שלא מילא
                                    nickname,
                                    /*age=*/0,
                                    /*country=*/"",
                                    /*favoriteGames=*/new ArrayList<>()
                            );

                            // שמירה ל-Firebase Realtime Database
                            FirebaseDatabaseManager dbManager = new FirebaseDatabaseManager();
                            dbManager.addUser(newUser);

                            Toast.makeText(this, "נרשמת בהצלחה!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    } else {
                        Log.e("RegisterError", "הרשמה נכשלה", task.getException());
                        Toast.makeText(this, "הרשמה נכשלה! נסה שוב.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

