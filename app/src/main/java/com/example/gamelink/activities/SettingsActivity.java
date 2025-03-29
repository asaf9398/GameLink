package com.example.gamelink.activities;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gamelink.R;
import com.example.gamelink.utils.DarkModeManager;

public class SettingsActivity extends AppCompatActivity {

    private Switch darkModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        darkModeSwitch = findViewById(R.id.dark_mode_switch);

        darkModeSwitch.setChecked(DarkModeManager.isDarkModeEnabled(this));

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            DarkModeManager.setDarkMode(this, isChecked);
            recreate();
        });
    }
}
