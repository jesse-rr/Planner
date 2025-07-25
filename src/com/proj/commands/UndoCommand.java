package com.proj.commands;

import com.proj.models.HistoryEntry;
import com.proj.models.Project;
import com.proj.models.Section;
import com.proj.models.Task;
import com.proj.util.Command;
import com.proj.util.PlannerManager;
import com.proj.util.SaveManager;

public class UndoCommand implements Command {
    private PlannerManager manager;

    public UndoCommand(PlannerManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        if (manager.getHistory().isEmpty()) {
            System.out.println("Nothing to undo");
            return;
        }

        HistoryEntry lastAction = manager.getHistory().get(manager.getHistory().size() - 1);
        boolean success = false;

        try {
            switch (lastAction.getAction()) {
                case "PROJECT_CREATE":
                    Project project = manager.getProject(lastAction.getTargetName());
                    if (project != null) {
                        manager.getProjects().remove(project.getName().toLowerCase());
                        success = true;
                    }
                    break;

                case "PROJECT_DELETE":
                    // To undo a delete, we'd need to store the serialized project in oldValue
                    // This would require changes to HistoryEntry to store serialized objects
                    System.out.println("Undo for project deletion not yet implemented");
                    break;

                case "PROJECT_RENAME":
                    project = manager.getProject(lastAction.getNewValue());
                    if (project != null) {
                        manager.getProjects().remove(project.getName().toLowerCase());
                        project.setName(lastAction.getOldValue());
                        manager.getProjects().put(project.getName().toLowerCase(), project);
                        success = true;
                    }
                    break;

                case "PROJECT_STATUS":
                    project = manager.getProject(lastAction.getTargetName());
                    if (project != null) {
                        project.setCompleted(Boolean.parseBoolean(lastAction.getOldValue()));
                        success = true;
                    }
                    break;

                case "SECTION_CREATE":
                    project = manager.getCurrentProject();
                    if (project != null) {
                        Section section = project.getSection(lastAction.getTargetName());
                        if (section != null) {
                            project.getSections().remove(section);
                            success = true;
                        }
                    }
                    break;

                case "SECTION_DELETE":
                    // Similar to PROJECT_DELETE, would need serialized section in oldValue
                    System.out.println("Undo for section deletion not yet implemented");
                    break;

                case "SECTION_RENAME":
                    project = manager.getCurrentProject();
                    if (project != null) {
                        Section section = project.getSection(lastAction.getNewValue());
                        if (section != null) {
                            section.setName(lastAction.getOldValue());
                            success = true;
                        }
                    }
                    break;

                case "TASK_CREATE":
                    project = manager.getCurrentProject();
                    if (project != null) {
                        String[] parts = lastAction.getTargetName().split(":");
                        if (parts.length == 2) {
                            Section section = project.getSection(parts[0]);
                            if (section != null) {
                                Task taskToRemove = null;
                                for (Task task : section.getTasks()) {
                                    if (task.getDescription().equals(parts[1])) {
                                        taskToRemove = task;
                                        break;
                                    }
                                }
                                if (taskToRemove != null) {
                                    section.getTasks().remove(taskToRemove);
                                    success = true;
                                }
                            }
                        }
                    }
                    break;

                case "TASK_DELETE":
                    // Would need serialized task in oldValue
                    System.out.println("Undo for task deletion not yet implemented");
                    break;

                case "TASK_STATUS":
                    project = manager.getCurrentProject();
                    if (project != null) {
                        String[] parts = lastAction.getTargetName().split(":");
                        if (parts.length == 2) {
                            Section section = project.getSection(parts[0]);
                            if (section != null) {
                                for (Task task : section.getTasks()) {
                                    if (task.getDescription().equals(parts[1])) {
                                        task.setCompleted(Boolean.parseBoolean(lastAction.getOldValue()));
                                        success = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    break;

                case "TASK_EDIT":
                    project = manager.getCurrentProject();
                    if (project != null) {
                        String[] parts = lastAction.getTargetName().split(":");
                        if (parts.length == 2) {
                            Section section = project.getSection(parts[0]);
                            if (section != null) {
                                for (Task task : section.getTasks()) {
                                    if (task.getDescription().equals(lastAction.getNewValue())) {
                                        task.setDescription(lastAction.getOldValue());
                                        success = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    break;

                case "TASK_MOVE":
                    project = manager.getCurrentProject();
                    if (project != null) {
                        String[] parts = lastAction.getTargetName().split(":");
                        if (parts.length == 1) { // Assuming targetName is just task description
                            Section sourceSection = project.getSection(lastAction.getNewValue());
                            Section targetSection = project.getSection(lastAction.getOldValue());
                            if (sourceSection != null && targetSection != null) {
                                Task taskToMove = null;
                                for (Task task : sourceSection.getTasks()) {
                                    if (task.getDescription().equals(lastAction.getTargetName())) {
                                        taskToMove = task;
                                        break;
                                    }
                                }
                                if (taskToMove != null) {
                                    sourceSection.getTasks().remove(taskToMove);
                                    targetSection.getTasks().add(taskToMove);
                                    success = true;
                                }
                            }
                        }
                    }
                    break;

                default:
                    System.out.println("Cannot undo action type: " + lastAction.getAction());
            }
        } catch (Exception e) {
            System.out.println("Error during undo: " + e.getMessage());
            e.printStackTrace();
        }

        if (success) {
            System.out.println("Undid: " + lastAction.getAction() + " on " + lastAction.getTargetType());
            manager.getHistory().remove(lastAction);
            SaveManager.saveAll(manager);
        } else {
            System.out.println("Failed to undo last action");
        }
    }

    private boolean undoAction(HistoryEntry action) {
        try {
            switch (action.getAction()) {
                case "PROJECT_RENAME":
                    Project project = manager.getProject(action.getNewValue());
                    if (project != null) {
                        project.setName(action.getOldValue());
                        return true;
                    }
                    break;
                case "TASK_STATUS":
                    // Parse section:task from targetName
                    String[] parts = action.getTargetName().split(":");
                    if (parts.length == 2) {
                        Project p = manager.getCurrentProject();
                        if (p != null) {
                            Section section = p.getSection(parts[0]);
                            if (section != null) {
                                for (Task task : section.getTasks()) {
                                    if (task.getDescription().equals(parts[1])) {
                                        task.setCompleted(Boolean.parseBoolean(action.getOldValue()));
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    break;
                // Add more undo cases as needed
                default:
                    return false;
            }
        } catch (Exception e) {
            System.out.println("Error undoing action: " + e.getMessage());
        }
        return false;
    }

    @Override
    public String getName() {
        return "undo";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"u"};
    }

    @Override
    public String getDescription() {
        return "Undoes the last action";
    }

    @Override
    public String getUsage() {
        return "Usage: plan undo\nAliases: u";
    }
}