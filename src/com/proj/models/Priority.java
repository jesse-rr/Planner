package com.proj.models;

public enum Priority {
    UNDEFINED("Undefined"),
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    EXTREME("Extreme");

    private final String displayName;

    Priority(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Priority fromString(String text) {
        for (Priority priority : Priority.values()) {
            if (priority.displayName.equalsIgnoreCase(text) ||
                    priority.name().equalsIgnoreCase(text)) {
                return priority;
            }
        }
        return UNDEFINED;
    }
}