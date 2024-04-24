package com.example.reminderwise.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.reminderwise.R;

public class MainActivity extends AppCompatActivity {

    private Button buttonAddReminder; // Button to add a reminder
    private Button buttonListReminders; // Button to list reminders
    private Button buttonSettings; // Button for application settings

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        buttonAddReminder = findViewById(R.id.buttonAddReminder);
        buttonListReminders = findViewById(R.id.buttonListReminders);
        buttonSettings = findViewById(R.id.buttonSettings);

        // Set click listener for Add Reminder button
        buttonAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open AddReminderActivity
                Intent intent = new Intent(MainActivity.this, AddReminderActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for List Reminders button
        buttonListReminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open ListTasksActivity
                Intent intent = new Intent(MainActivity.this, ListRemindersActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for Settings button
        buttonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open ListTasksActivity
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }
}