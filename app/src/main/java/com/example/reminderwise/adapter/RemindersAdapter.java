package com.example.reminderwise.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminderwise.activity.UpdateReminderActivity;
import com.example.reminderwise.model.Reminder;
import com.example.reminderwise.R;

import java.util.List;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;
    private Context context;

    public RemindersAdapter(List<Reminder> reminderList, Context context) {
        this.reminderList = reminderList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.bind(reminder);
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public class ReminderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtReminder;
        private Reminder currentReminder;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtReminder = itemView.findViewById(R.id.txtReminder);
            itemView.setOnClickListener(this);
        }

        public void bind(Reminder reminder) {
            currentReminder = reminder;
            txtReminder.setText("Reminder Name: " + reminder.getName() +
                    " Reminder Description: " + reminder.getDescription() +
                    " Reminder Due: " + reminder.getDueDateTime() +
                    " Reminder Repetition: " + reminder.getRepetition());
        }

        @Override
        public void onClick(View v) {
            // Start UpdateReminderActivity when a reminder is clicked
            Intent intent = new Intent(context, UpdateReminderActivity.class);
            // Pass the clicked reminder object to UpdateReminderActivity
            intent.putExtra("reminder", currentReminder);
            context.startActivity(intent);
        }
    }
}