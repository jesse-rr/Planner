package com.proj.commands;

import com.proj.models.Project;
import com.proj.models.Priority;
import com.proj.models.Section;
import com.proj.util.Command;
import com.proj.util.PlannerManager;

import java.util.Collection;

public class ListCommand implements Command {
    private PlannerManager manager;

    public ListCommand(PlannerManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        String filter = "all";
        Priority priorityFilter = null;

        // Parse arguments
        for (String arg : args) {
            if (arg.equals("finished") || arg.equals("active")) {
                filter = arg;
            } else if (arg.startsWith("priority=")) {
                String priorityStr = arg.substring(9);
                priorityFilter = Priority.fromString(priorityStr);
            }
        }

        Collection<Project> projects = manager.getProjects().values();

        System.out.println("Projects:");
        for (Project project : projects) {
            if (filter.equals("all") ||
                    (filter.equals("finished") && project.isCompleted()) ||
                    (filter.equals("active") && !project.isCompleted())) {

                // Skip if priority filter doesn't match
                if (priorityFilter != null &&
                        (project.getPriority() == null ||
                                !project.getPriority().equals(priorityFilter))) {
                    continue;
                }

                System.out.println("- " + project.getName() +
                        (project.isCompleted() ? " (finished)" : "") +
                        (project.getPriority() != Priority.UNDEFINED ?
                                " [Priority: " + project.getPriority() + "]" : ""));

                // List sections if priority filter is active
                if (priorityFilter != null) {
                    for (Section section : project.getSections()) {
                        if (section.getPriority() != null &&
                                section.getPriority().equals(priorityFilter)) {
                            System.out.println("  └─ " + section.getName() +
                                    " [" + section.getPriority() + "]");
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ls", "dir"};
    }

    @Override
    public String getDescription() {
        return "Lists all projects with optional filters (finished/active, priority)";
    }

    @Override
    public String getUsage() {
        return "Usage:\n" +
                "  plan list - list all projects\n" +
                "  plan list finished - list only finished projects\n" +
                "  plan list active - list only active projects\n" +
                "  plan list priority=<level> - filter by priority (low/medium/high/extreme)\n" +
                "  (combine filters: 'plan list active priority=high')\n" +
                "Aliases: ls, dir";
    }
}