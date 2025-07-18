package com.proj.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HistoryEntry implements Serializable {
    private String action;
    private String targetType;
    private String targetName;
    private String oldValue;
    private String newValue;
    private LocalDateTime timestamp;

    public HistoryEntry(String action, String targetType, String targetName, String oldValue, String newValue) {
        this.action = action;
        this.targetType = targetType;
        this.targetName = targetName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = LocalDateTime.now();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s '%s': %s -> %s",
                timestamp,
                action,
                targetType,
                targetName,
                oldValue,
                newValue);
    }
}
