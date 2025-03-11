package com.example.gamelink.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.gamelink.R;
import com.example.gamelink.fragments.ChatFragment;
import com.example.gamelink.fragments.FeedbackFragment;
import com.example.gamelink.fragments.NotificationsFragment;
import com.example.gamelink.fragments.ProfileFragment;
import com.example.gamelink.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private Button btnLogout; // כפתור התנתקות

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        // אם המשתמש לא מחובר, להפנות אותו למסך ההתחברות
        if (firebaseAuth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        // ניווט תחתון בין מסכים
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        loadFragment(new SearchFragment()); // ברירת מחדל - מסך החיפוש

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_chat) {
                selectedFragment = new ChatFragment();
            } else if (itemId == R.id.nav_feedback) {
                selectedFragment = new FeedbackFragment();
            } else if (itemId == R.id.nav_notifications) {
                selectedFragment = new NotificationsFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            }

            return loadFragment(selectedFragment);
        });

        // כפתור התנתקות
        //btnLogout = findViewById(R.id.btn_logout);
        //btnLogout.setOnClickListener(view -> logoutUser());
    }

    // פונקציה לטעינת פרגמנט
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    // פונקציה להתנתקות מהמשתמש והעברה למסך ההתחברות
    private void logoutUser() {
        firebaseAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // מנקה את ה-Back Stack
        startActivity(intent);
        finish();
    }
}
