package com.example.gamelink.firebase;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseAuthManager {

    private static final String TAG = "FirebaseAuthManager";
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    public FirebaseAuthManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void initializeGoogleSignIn(Activity activity, String webClientId) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public void signInWithGoogle(Activity activity, int requestCode) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, requestCode);
    }

    public void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask, Activity activity) {
        try {
            GoogleSignInAccount account = completedTask.getResult(Exception.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken(), activity);
            }
        } catch (Exception e) {
            Log.w(TAG, "Google sign-in failed", e);
            Toast.makeText(activity, "Google sign-in failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken, Activity activity) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "signInWithGoogle:success - User: " + user.getEmail());
                    Toast.makeText(activity, "Welcome, " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG, "signInWithGoogle:failure", task.getException());
                Toast.makeText(activity, "Authentication Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signOut(Activity activity) {
        mAuth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(activity, task -> {
            Toast.makeText(activity, "Signed out successfully", Toast.LENGTH_SHORT).show();
        });
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
}
