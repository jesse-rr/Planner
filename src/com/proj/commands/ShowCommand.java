package com.proj.commands;

import com.proj.models.Section;
import com.proj.models.Task;
import com.proj.util.Command;
import com.proj.util.PlannerManager;

public class ShowCommand implements Command {
    private PlannerManager manager;
    
    public ShowCommand(PlannerManager manager) {
        this.manager = manager;
    }
    
    @Override
    public void execute(String[] args) {
        String filter = args.length > 0 ? args[0].toLowerCase() : "all";
        
        if (manager.getCurrentProject() == null) {
            System.out.println("No project selected");
            return;
        }
        
        System.out.println(manager.getCurrentProject());
        
        if (!filter.equals("all")) {
            System.out.println("\nFiltered by: " + filter);
            if (filter.equals("completed")) {
                printFiltered(true);
            } else if (filter.equals("pending")) {
                printFiltered(false);
            }
        }
    }
    
    private void printFiltered(boolean completed) {
        for (Section section : manager.getCurrentProject().getSections()) {
            System.out.println("Section: " + section.getName());
            for (Task task : section.getTasks()) {
                if (task.isCompleted() == completed) {
                    System.out.println("  " + task);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"sh", "display"};
    }

    @Override
    public String getDescription() {
        return "Displays current project details";
    }

    @Override
    public String getUsage() {
        return "Usage:\n"
             + "  plan show - show all project details\n"
             + "  plan show completed - show only completed tasks\n"
             + "  plan show pending - show only pending tasks\n"
             + "Aliases: sh, display";
    }
}