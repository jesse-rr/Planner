package com.proj.commands;

import com.proj.models.HistoryEntry;
import com.proj.models.Project;
import com.proj.models.Section;
import com.proj.models.Task;
import com.proj.util.Command;
import com.proj.util.PlannerManager;

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
        boolean success = undoAction(lastAction);
        
        if (success) {
            System.out.println("Undid: " + lastAction.getAction() + " on " + lastAction.getTargetType());
            manager.getHistory().remove(lastAction);
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