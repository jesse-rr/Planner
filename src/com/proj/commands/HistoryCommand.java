package com.proj.commands;

import com.proj.models.HistoryEntry;
import com.proj.util.Command;
import com.proj.util.PlannerManager;

import java.util.List;

public class HistoryCommand implements Command {
    private PlannerManager manager;

    public HistoryCommand(PlannerManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            // Show all history
            printHistory(manager.getHistory());
        } else if (args.length == 1) {
            // Filter by type
            printHistory(manager.getHistory(args[0], null));
        } else if (args.length == 2) {
            // Filter by type and name
            printHistory(manager.getHistory(args[0], args[1]));
        } else {
            System.out.println(getUsage());
        }
    }

    private void printHistory(List<HistoryEntry> entries) {
        if (entries.isEmpty()) {
            System.out.println("No history entries found");
            return;
        }
        System.out.println("History:");
        for (HistoryEntry entry : entries) {
            System.out.println("  " + entry);
        }
    }

    @Override
    public String getName() {
        return "log";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"hist"};
    }

    @Override
    public String getDescription() {
        return "Shows history of changes";
    }

    @Override
    public String getUsage() {
        return "Usage:\n"
             + "  plan log - show all history\n"
             + "  plan log <type> - show history for type (project/section/task)\n"
             + "  plan log <type> <name> - show history for specific item\n"
             + "Aliases: hist";
    }
}