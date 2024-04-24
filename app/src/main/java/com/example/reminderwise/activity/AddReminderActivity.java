package com.example.reminderwise.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.reminderwise.R;
import com.example.reminderwise.model.Reminder;
import com.example.reminderwise.worker.ReminderNotificationWorker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AddReminderActivity extends AppCompatActivity {

    private EditText editTextReminderName;
    private EditText editTextReminderDescription;
    private EditText editTextDueDateTime;
    private Button buttonAddReminder;
    private Spinner spinnerRepetition;

    private Calendar calendar;
    private List<Reminder> reminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        // Initialize views
        editTextReminderName = findViewById(R.id.editTextReminderName);
        editTextReminderDescription = findViewById(R.id.editTextReminderDescription);
        editTextDueDateTime = findViewById(R.id.editTextDueDateTime);
        buttonAddReminder = findViewById(R.id.buttonAddReminder);
        spinnerRepetition = findViewById(R.id.spinnerRepetition);

        // Initialize Calendar instance
        calendar = Calendar.getInstance();

        // Initialize reminder list
        reminderList = loadReminders();

        // Set click listener for the Add Reminder button
        buttonAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get reminder name, description, and due date/time from EditText fields
                String reminderName = editTextReminderName.getText().toString().trim();
                String description = editTextReminderDescription.getText().toString().trim();
                String dueDateTime = editTextDueDateTime.getText().toString().trim();
                String repetition = spinnerRepetition.getSelectedItem().toString(); // Get selected repetition option

                // Validate reminder name, description, and due date/time
                if (reminderName.isEmpty() || dueDateTime.isEmpty() || repetition.isEmpty()) {
                    Toast.makeText(AddReminderActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Create new Reminder object
                    Reminder reminder = new Reminder(reminderName, description, dueDateTime, repetition);

                    // Add reminder to list
                    reminderList.add(reminder);

                    // Save reminders
                    saveReminders();

                    // Display success message
                    Toast.makeText(AddReminderActivity.this, "Reminder added successfully", Toast.LENGTH_SHORT).show();

                    // Clear EditText fields after adding reminder
                    editTextReminderName.setText("");
                    editTextReminderDescription.setText("");
                    editTextDueDateTime.setText("");
                    spinnerRepetition.setSelection(0); // Reset repetition spinner to default value

                    // Schedule notification using WorkManager if reminder is due within the next one hour
                    scheduleNotification(reminder);
                }
            }
        });

        // Set click listener for Due Date & Time field
        editTextDueDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show Date and Time picker dialog
                showDateTimePicker();
            }
        });
    }

    // Method to show Date and Time picker dialog
    private void showDateTimePicker() {
        // Get current date and time
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Show DatePicker dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update Calendar instance with selected date
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Show TimePicker dialog after Date is set
                        showTimePicker();
                    }
                }, year, month, day);

        // Show DatePicker dialog
        datePickerDialog.show();
    }

    // Method to show TimePicker dialog
    private void showTimePicker() {
        // Get current time
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Show TimePicker dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Update Calendar instance with selected time
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        // Update EditText field with selected date and time
                        updateDueDateTimeEditText();
                    }
                }, hour, minute, true); // Use 24-hour format

        // Show TimePicker dialog
        timePickerDialog.show();
    }

    // Method to update Due Date & Time EditText field with selected date and time
    private void updateDueDateTimeEditText() {
        // Format date and time
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        String formattedDateTime = sdf.format(calendar.getTime());

        // Set formatted date and time to EditText field
        editTextDueDateTime.setText(formattedDateTime);
    }

    // Method to save reminders using SharedPreferences
    private void saveReminders() {
        SharedPreferences sharedPreferences = getSharedPreferences("ReminderPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(reminderList);
        editor.putString("reminderList", json);
        editor.apply();
    }

    // Method to load reminders from SharedPreferences
    private List<Reminder> loadReminders() {
        SharedPreferences sharedPreferences = getSharedPreferences("ReminderPreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("reminderList", null);
        Type type = new TypeToken<ArrayList<Reminder>>() {}.getType();
        if (json == null) {
            return new ArrayList<>();
        }
        return gson.fromJson(json, type);
    }

    // Method to schedule notification for reminders due within the next one hour using WorkManager
    private void scheduleNotification(Reminder reminder) {
        // Get the due date and time of the reminder
        Calendar reminderDueDateTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        try {
            reminderDueDateTime.setTime(sdf.parse(reminder.getDueDateTime()));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Calculate the difference between current time and reminder due time
        long timeDifferenceInMillis = reminderDueDateTime.getTimeInMillis() - System.currentTimeMillis();

        // Schedule the notification if the reminder is due within the next one hour using WorkManager
        if (timeDifferenceInMillis > 0 && timeDifferenceInMillis <= TimeUnit.HOURS.toMillis(1)) {
            // Pass reminder details to the Worker
            Data inputData = new Data.Builder()
                    .putString("reminder_name", reminder.getName())
                    .putString("reminder_description", reminder.getDescription())
                    .build();

            // Create WorkRequest for ReminderNotificationWorker
            OneTimeWorkRequest notificationWorkRequest =
                    new OneTimeWorkRequest.Builder(ReminderNotificationWorker.class)
                            .setInputData(inputData)
                            .setInitialDelay(timeDifferenceInMillis, TimeUnit.MILLISECONDS)
                            .build();

            // Enqueue the WorkRequest
            WorkManager.getInstance(this).enqueue(notificationWorkRequest);
        }
    }
}