package com.example.gamelink.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamelink.R;
import com.example.gamelink.firebase.FirebaseDatabaseManager;
import com.example.gamelink.models.User;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText, ageEditText, countryEditText;
    private Button registerButton;

    private FirebaseDatabaseManager databaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.name_edit_text);
        ageEditText = findViewById(R.id.age_edit_text);
        countryEditText = findViewById(R.id.country_edit_text);
        registerButton = findViewById(R.id.register_button);

        databaseManager = new FirebaseDatabaseManager(this);

        registerButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String ageText = ageEditText.getText().toString().trim();
            String country = countryEditText.getText().toString().trim();

            if (!name.isEmpty() && !ageText.isEmpty() && !country.isEmpty()) {
                int age = Integer.parseInt(ageText);
                User user = new User("example_user_id", name, age, country, new ArrayList<>());
                databaseManager.addUser(user);
                Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

