package com.proj.commands;

import com.proj.util.Command;
import com.proj.util.PlannerManager;

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
        manager.createProject(args[0]);
        System.out.println("Created project: " + args[0]);
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
