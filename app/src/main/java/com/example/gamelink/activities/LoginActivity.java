package com.example.gamelink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.example.gamelink.activities.R;
import com.example.gamelink.activities.firebase.FirebaseAuthManager;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1001;
    private FirebaseAuthManager firebaseAuthManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuthManager = new FirebaseAuthManager();
        firebaseAuthManager.initializeGoogleSignIn(this, getString(R.string.default_web_client_id));

        findViewById(R.id.google_sign_in_button).setOnClickListener(v -> {
            firebaseAuthManager.signInWithGoogle(LoginActivity.this, RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            firebaseAuthManager.handleGoogleSignInResult(task, this);
        }
    }
}
