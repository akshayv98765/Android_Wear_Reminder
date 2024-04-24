package com.example.reminderwise.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminderwise.adapter.RemindersAdapter;
import com.example.reminderwise.model.Reminder;
import com.example.reminderwise.databinding.ActivityListRemindersBinding;
import com.example.reminderwise.worker.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for listing reminders.
 */
public class ListRemindersActivity extends AppCompatActivity {

    private ActivityListRemindersBinding binding; // View binding instance
    private RemindersAdapter reminderAdapter; // Adapter for RecyclerView
    private List<Reminder> reminderList; // List to store reminders

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListRemindersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize RecyclerView and layout manager
        RecyclerView recyclerViewReminders = binding.recyclerViewReminders;
        recyclerViewReminders.setLayoutManager(new LinearLayoutManager(this));

        // Load reminder list
        reminderList = Utils.loadReminders(this);

        // Initialize ReminderAdapter
        reminderAdapter = new RemindersAdapter(reminderList, this);

        // Set adapter to RecyclerView
        recyclerViewReminders.setAdapter(reminderAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh reminder list and adapter when activity is resumed
        reminderList.clear();
        reminderList.addAll(Utils.loadReminders(this));
        reminderAdapter.notifyDataSetChanged();
    }

    /**
     * Method to update reminder list and notify adapter.
     * @param updatedReminderList The updated reminder list.
     */
    public void updateReminderList(List<Reminder> updatedReminderList) {
        reminderList.clear();
        reminderList.addAll(updatedReminderList);
        reminderAdapter.notifyDataSetChanged();
        Utils.saveReminders(this, reminderList);
    }
}
