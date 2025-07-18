package com.proj.models;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    private String name;
    private String objective;
    private String date;
    private boolean completed;
    private Priority priority;
    private List<Section> sections;
    
    public Project(String name) {
        this.name = name;
        this.completed = false;
        this.priority = Priority.UNDEFINED;
        this.sections = new ArrayList<>();
    }
    
    public void addSection(String sectionName) {
        sections.add(new Section(sectionName));
    }
    
    public Section getSection(String sectionName) {
        for (Section section : sections) {
            if (section.getName().equalsIgnoreCase(sectionName)) {
                return section;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
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
        sb.append("Project: ").append(name).append("\n");
        sb.append("Objective: ").append(objective).append("\n\n");
        for (Section section : sections) {
            sb.append(section).append("\n");
        }
        return sb.toString();
    }
}