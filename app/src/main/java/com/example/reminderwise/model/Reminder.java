package com.example.reminderwise.model;

import java.io.Serializable;
import java.util.Objects;

public class Reminder implements Serializable {
    private String name;
    private String description;
    private String dueDateTime;
    private String repetition;

    public Reminder(String name, String description, String dueDateTime, String repetition) {
        this.name = name;
        this.description = description;
        this.dueDateTime = dueDateTime;
        this.repetition = repetition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(String dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public String getRepetition() {
        return repetition;
    }

    public void setRepetition(String repetition) {
        this.repetition = repetition;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Reminder reminder = (Reminder) obj;
        return Objects.equals(name, reminder.name) &&
                Objects.equals(description, reminder.description) &&
                Objects.equals(dueDateTime, reminder.dueDateTime) &&
                Objects.equals(repetition, reminder.repetition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, dueDateTime, repetition);
    }
}