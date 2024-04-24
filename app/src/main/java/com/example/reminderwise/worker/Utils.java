package com.example.reminderwise.worker;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.reminderwise.model.Reminder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    // Method to load reminders from SharedPreferences
    public static List<Reminder> loadReminders(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ReminderPreferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("reminderList", null);
        Type type = new TypeToken<ArrayList<Reminder>>() {}.getType();
        if (json == null) {
            return new ArrayList<>();
        }
        return gson.fromJson(json, type);
    }

    // Method to save reminders using SharedPreferences
    public static void saveReminders(Context context, List<Reminder> reminderList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ReminderPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(reminderList);
        editor.putString("reminderList", json);
        editor.apply();
    }
}
