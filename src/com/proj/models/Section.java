package com.proj.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Section implements Serializable {
    private String name;
    private boolean completed;
    private Priority priority;
    private List<Task> tasks;
    
    public Section(String name) {
        this.name = name;
        this.completed = false;
        this.priority = Priority.UNDEFINED;
        this.tasks = new ArrayList<>();
    }
    
    public void addTask(String description) {
        tasks.add(new Task(description));
    }

    public Task getTask(int index) {
        return tasks.get(index);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
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
        StringBuilder sb = new StringBuilder();
        sb.append("Section: ").append(name).append("\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }
        return sb.toString();
    }
}