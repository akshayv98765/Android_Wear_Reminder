package com.example.reminderwise.worker;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.reminderwise.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ReminderNotificationWorker extends Worker {

    private static final String CHANNEL_ID = "reminder_notification_channel";
    private Context context;

    public ReminderNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Extract reminder details from input data
            Data inputData = getInputData();
            String reminderName = inputData.getString("reminder_name");
            String reminderDescription = inputData.getString("reminder_description");
            String repetition = inputData.getString("reminder_repetition");

            // Trigger notification
            triggerNotification(reminderName, reminderDescription);

            // Schedule next notification based on recurrence
            scheduleNextNotification(inputData);

            return Result.success();
        } catch (Exception e) {
            Log.e("ReminderNotificationWorker", "Error processing reminder", e);
            return Result.failure();
        }
    }

    // Method to trigger a notification for a reminder
    @SuppressLint("MissingPermission")
    private void triggerNotification(String reminderName, String reminderDescription) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Reminder: " + reminderName)
                .setContentText("Description: " + reminderDescription)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Method to create a notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Reminder Notifications";
            String description = "Notifications for reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Method to schedule the next notification based on the reminder's recurrence
    private void scheduleNextNotification(Data inputData) {
        // Extract reminder details
        String repetition = inputData.getString("reminder_repetition");

        // Calculate the next due date/time based on the recurrence
        Calendar nextDueDateTime = calculateNextDueDateTime(repetition);

        // Calculate time difference between current time and next due date/time
        long delayInMillis = nextDueDateTime.getTimeInMillis() - System.currentTimeMillis();

        // Create a new work request for the next notification
        OneTimeWorkRequest nextNotificationWorkRequest =
                new OneTimeWorkRequest.Builder(ReminderNotificationWorker.class)
                        .setInputData(inputData)
                        .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                        .build();

        // Enqueue the work request
        WorkManager.getInstance(context).enqueue(nextNotificationWorkRequest);
    }

    // Method to calculate the next due date/time based on the reminder's recurrence
    private Calendar calculateNextDueDateTime(String repetition) {
        // Get the current date/time
        Calendar currentDateTime = Calendar.getInstance();

        // Calculate and return the next due date/time based on the recurrence
        switch (repetition) {
            case "Once":
                // For a one-time reminder, return the current date/time
                return currentDateTime;
            case "Hourly":
                // For hourly recurrence, add one hour to the current date/time
                currentDateTime.add(Calendar.HOUR, 1);
                break;
            case "Daily":
                // For daily recurrence, add one day to the current date/time
                currentDateTime.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case "Weekly":
                // For weekly recurrence, add one week to the current date/time
                currentDateTime.add(Calendar.WEEK_OF_YEAR, 1);
                break;
            case "Monthly":
                // For monthly recurrence, add one month to the current date/time
                currentDateTime.add(Calendar.MONTH, 1);
                break;
            case "Yearly":
                // For yearly recurrence, add one year to the current date/time
                currentDateTime.add(Calendar.YEAR, 1);
                break;
            default:
                break;
        }

        return currentDateTime;
    }
}