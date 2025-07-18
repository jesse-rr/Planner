package com.proj.commands;

import com.proj.models.Project;
import com.proj.models.Section;
import com.proj.models.Task;
import com.proj.util.Command;
import com.proj.util.PlannerManager;

public class FindCommand implements Command {
    private PlannerManager manager;
    
    public FindCommand(PlannerManager manager) {
        this.manager = manager;
    }
    
    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println(getUsage());
            return;
        }
        
        String type = args[0].toLowerCase();
        String query = args[1].toLowerCase();
        
        switch (type) {
            case "project":
                findProjects(query);
                break;
            case "section":
                findSections(query);
                break;
            case "task":
                findTasks(query);
                break;
            default:
                System.out.println("Unknown type: " + type);
                System.out.println(getUsage());
        }
    }
    
    private void findProjects(String query) {
        System.out.println("Matching projects:");
        for (Project project : manager.getProjects().values()) {
            if (project.getName().toLowerCase().contains(query)) {
                System.out.println("- " + project.getName());
            }
        }
    }
    
    private void findSections(String query) {
        System.out.println("Matching sections:");
        for (Project project : manager.getProjects().values()) {
            for (Section section : project.getSections()) {
                if (section.getName().toLowerCase().contains(query)) {
                    System.out.println("- " + section.getName() + 
                        " (Project: " + project.getName() + ")");
                }
            }
        }
    }
    
    private void findTasks(String query) {
        System.out.println("Matching tasks:");
        for (Project project : manager.getProjects().values()) {
            for (Section section : project.getSections()) {
                for (Task task : section.getTasks()) {
                    if (task.getDescription().toLowerCase().contains(query)) {
                        System.out.println("- " + task + 
                            " (Section: " + section.getName() + 
                            ", Project: " + project.getName() + ")");
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return "find";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"f", "search"};
    }

    @Override
    public String getDescription() {
        return "Finds projects, sections or tasks by name";
    }

    @Override
    public String getUsage() {
        return "Usage:\n"
             + "  plan find project <name>\n"
             + "  plan find section <name>\n"
             + "  plan find task <description>\n"
             + "Aliases: f, search";
    }
}