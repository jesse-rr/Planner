package com.proj.commands;

import com.proj.models.HistoryEntry;
import com.proj.models.Project;
import com.proj.util.Command;
import com.proj.util.PlannerManager;
import com.proj.util.SaveManager;

public class CreateCommand implements Command {
    private PlannerManager manager;

    public CreateCommand(PlannerManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            System.out.println(getUsage());
            return;
        }

        String projectName = args[0];
        if (manager.getProject(projectName) != null) {
            System.out.println("Project already exists: " + projectName);
            return;
        }

        Project project = new Project(projectName);
        manager.getProjects().put(projectName.toLowerCase(), project);
        manager.setCurrentProject(projectName);

        manager.addHistoryEntry(new HistoryEntry(
                "PROJECT_CREATE",
                "project",
                projectName,
                "",
                project.toString()
        ));

        System.out.println("Created project: " + projectName);
        SaveManager.saveAll(manager);
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"c", "new"};
    }

    @Override
    public String getDescription() {
        return "Creates a new project";
    }

    @Override
    public String getUsage() {
        return "Usage: plan create <project-name>";
    }
}
