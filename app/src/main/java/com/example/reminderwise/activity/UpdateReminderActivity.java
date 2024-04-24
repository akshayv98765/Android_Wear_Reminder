package com.example.reminderwise.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UpdateReminderActivity extends AppCompatActivity {

    private EditText editTextReminderName;
    private EditText editTextReminderDescription;
    private EditText editTextDueDateTime;
    private Spinner spinnerRepetition;
    private Button buttonUpdate;
    private Button buttonDelete;

    private Reminder reminder;
    private Calendar calendar;

    private List<Reminder> reminderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_reminder);

        // Initialize views
        editTextReminderName = findViewById(R.id.editTextReminderName);
        editTextReminderDescription = findViewById(R.id.editTextReminderDescription);
        editTextDueDateTime = findViewById(R.id.editTextDueDateTime);
        spinnerRepetition = findViewById(R.id.spinnerRepetition);
        buttonUpdate = findViewById(R.id.buttonUpdateReminder);
        buttonDelete = findViewById(R.id.buttonDeleteReminder);

        // Initialize Calendar instance
        calendar = Calendar.getInstance();

        // Initialize reminder list
        reminderList = loadReminders();

        // Retrieve the Reminder object from intent
        reminder = (Reminder) getIntent().getSerializableExtra("reminder");

        if (reminder != null) {
            // Populate views with reminder details
            editTextReminderName.setText(reminder.getName());
            editTextReminderDescription.setText(reminder.getDescription());
            editTextDueDateTime.setText(reminder.getDueDateTime());
            // Set the selected item of spinnerRepetition based on reminder's repetition
            // For example:
            // spinnerRepetition.setSelection(getIndex(spinnerRepetition, reminder.getRepetition()));
        }

        // Set click listener for the Update button
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateReminder();
            }
        });

        // Set click listener for the Delete button
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteReminder();
            }
        });

        // Set click listener for the Due Date & Time field
        editTextDueDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });
    }

    // Method to show Date and Time picker dialog
    private void showDateTimePicker() {
        // Initialize the calendar with the existing date and time
        int year, month, day, hour, minute;
        if (reminder != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
            try {
                Date dueDateTime = sdf.parse(reminder.getDueDateTime());
                calendar.setTime(dueDateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Get the existing date and time
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

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

    private void updateDueDateTimeEditText() {
        // Format date and time
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault());
        String formattedDateTime = sdf.format(calendar.getTime());

        // Set formatted date and time to EditText field
        editTextDueDateTime.setText(formattedDateTime);
    }


    // Method to update the reminder details
    private void updateReminder() {
        // Retrieve updated values from views
        String updatedReminderName = editTextReminderName.getText().toString().trim();
        String updatedReminderDescription = editTextReminderDescription.getText().toString().trim();
        String updatedDueDateTime = editTextDueDateTime.getText().toString().trim();
        String updatedRepetition = spinnerRepetition.getSelectedItem().toString();

        // Create an updated reminder object
        Reminder updatedReminder = new Reminder(updatedReminderName, updatedReminderDescription, updatedDueDateTime, updatedRepetition);

        // Find the index of the existing reminder in the list
        int index = -1;
        for (int i = 0; i < reminderList.size(); i++) {
            if (reminderList.get(i).equals(reminder)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            // Replace the old reminder with the updated one
            reminderList.set(index, updatedReminder);

            // Save reminders
            saveReminders();

            // Show success message
            Toast.makeText(this, "Reminder updated successfully", Toast.LENGTH_SHORT).show();

            // Finish activity
            finish();

            // Schedule Notification
            scheduleNotification(reminder);
        } else {
            Toast.makeText(this, "Error: Unable to update reminder", Toast.LENGTH_SHORT).show();
        }
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


    // Method to delete the reminder
    private void deleteReminder() {
        // Find the index of the existing reminder in the list
        int index = -1;
        for (int i = 0; i < reminderList.size(); i++) {
            if (reminderList.get(i).equals(reminder)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            // Remove the reminder from the list
            reminderList.remove(index);

            // Save reminders
            saveReminders();

            // Show success message
            Toast.makeText(this, "Reminder deleted successfully", Toast.LENGTH_SHORT).show();

            // Finish activity
            finish();
        } else {
            Toast.makeText(this, "Error: Unable to delete reminder", Toast.LENGTH_SHORT).show();
        }
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