package com.proj.models;

import java.io.Serializable;

public class Task implements Serializable {
    private String description;
    private Priority priority;
    private boolean completed;

    public Task(String description) {
        this.description = description;
        this.priority = Priority.UNDEFINED;
        this.completed = false;
    }

    public void markComplete() { this.completed = true; }
    public void markIncomplete() { this.completed = false; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return (completed ? "[Y] " : "[X] ") + description;
    }
}