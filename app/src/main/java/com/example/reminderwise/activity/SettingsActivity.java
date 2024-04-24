package com.example.reminderwise.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reminderwise.R;

public class SettingsActivity extends AppCompatActivity {

    private Switch themeSwitch;
    private RatingBar appRatingBar;
    private Button submitRatingButton;
    private TextView appInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        themeSwitch = findViewById(R.id.themeSwitch);
        appRatingBar = findViewById(R.id.appRatingBar);
        submitRatingButton = findViewById(R.id.submitRatingButton);
        appInfoTextView = findViewById(R.id.appInfoTextView);

        // Set onClick listener for submit rating button
        submitRatingButton.setOnClickListener(v -> submitRating());

        // Set onRatingBarChangeListener for app rating bar
        appRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (rating > 0) {
                submitRatingButton.setEnabled(true);
            } else {
                submitRatingButton.setEnabled(false);
            }
        });

        // Update app info text
        updateAppInfoText();
    }

    // Method to handle submitting the rating
    private void submitRating() {
        float rating = appRatingBar.getRating();
        // You can handle submitting the rating here, for example, sending it to a server
        Toast.makeText(this, "Rating submitted: " + rating, Toast.LENGTH_SHORT).show();
    }

    // Method to update the app info text
    private void updateAppInfoText() {
        // Get app name, version, etc. dynamically
        String appInfo = "App Name: Reminder Wise (v1.0.0)\nDevelopers:\nJoykeneth gamit\nNavpreet Navpreet";
        appInfoTextView.setText(appInfo);
    }
}
