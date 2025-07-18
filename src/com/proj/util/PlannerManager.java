package com.proj.util;

import com.proj.models.HistoryEntry;
import com.proj.models.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlannerManager {
    private List<HistoryEntry> history = new ArrayList<>();
    private Map<String, Project> projects;
    private Project currentProject;

    public PlannerManager() {
        this.projects = new HashMap<>();
    }
    
    public void createProject(String name) {
        Project project = new Project(name);
        projects.put(name.toLowerCase(), project);
        currentProject = project;
    }

    public void addHistoryEntry(HistoryEntry entry) {
        history.add(entry);
    }

    public List<HistoryEntry> getHistory() {
        return new ArrayList<>(history); // copy
    }

    public List<HistoryEntry> getHistory(String filterType, String filterName) {
        List<HistoryEntry> filtered = new ArrayList<>();
        for (HistoryEntry entry : history) {
            if (entry.getTargetType().equalsIgnoreCase(filterType) &&
                    (filterName == null || entry.getTargetName().equalsIgnoreCase(filterName))) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    public boolean undoLastAction() {
        if (history.isEmpty()) {
            return false;
        }
        HistoryEntry lastAction = history.remove(history.size() - 1);
        // Implement undo logic based on lastAction
        // This would need to reverse the change recorded in the history
        return true;
    }

    public Map<String, Project> getProjects() {
        return projects;
    }
    
    public Project getProject(String name) {
        return projects.get(name.toLowerCase());
    }
    
    public Project getCurrentProject() {
        return currentProject;
    }
    
    public void setCurrentProject(String name) {
        currentProject = getProject(name);
    }
}