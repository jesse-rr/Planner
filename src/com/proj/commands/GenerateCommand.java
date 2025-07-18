package com.proj.commands;

import com.proj.util.Command;
import com.proj.util.PlannerManager;

public class GenerateCommand implements Command {
    private PlannerManager manager;
    
    public GenerateCommand(PlannerManager manager) {
        this.manager = manager;
    }
    
    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: plan g s <section-name>");
            return;
        }
        
        if (args[0].equalsIgnoreCase("s")) {
            manager.getCurrentProject().addSection(args[1]);
            System.out.println("Added section: " + args[1]);
        }
    }

    @Override
    public String getName() {
        return "generate";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"g", "gen"};
    }

    @Override
    public String getDescription() {
        return "Generates new project elements (sections, tasks)";
    }

    @Override
    public String getUsage() {
        return "Usage:\n"
                + "  plan generate s <section-name> - Create new section\n"
                + "  plan generate t <section-name> <task> - Add task to section\n"
                + "Aliases: g, gen";
    }
}