package com.proj;

import com.proj.commands.*;
import com.proj.util.Command;
import com.proj.util.PlannerManager;
import com.proj.util.SaveManager;

import java.util.*;

public class Main {
    private static PlannerManager manager = new PlannerManager();
    private static Map<String, Command> commands = new HashMap<>();

    public static void main(String[] args) {
        // Load saved data
        SaveManager.loadAll(manager);

        // Initialize commands
        initializeCommands();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Project Planner CLI - Type 'help' for commands");

        // Add shutdown hook to save on exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SaveManager.saveAll(manager);
            System.out.println("Data saved on exit");
        }));

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) break;

            processCommand(input);
        }

        // Manual save before exit
        SaveManager.saveAll(manager);
        scanner.close();
    }

    private static void initializeCommands() {
        Command[] allCommands = {
                new CreateCommand(manager),
                new GenerateCommand(manager),
                new ChangeCommand(manager),
                new ShowCommand(manager),
                new ListCommand(manager),
                new FindCommand(manager),
                new HistoryCommand(manager),
                new UndoCommand(manager),
                new PriorityCommand(manager),
                new DeleteCommand(manager),
                new HelpCommand(Arrays.asList( // Initialize HelpCommand with all commands
                        new CreateCommand(manager),
                        new GenerateCommand(manager),
                        new ChangeCommand(manager),
                        new ShowCommand(manager),
                        new ListCommand(manager),
                        new FindCommand(manager),
                        new HistoryCommand(manager),
                        new UndoCommand(manager),
                        new PriorityCommand(manager),
                        new DeleteCommand(manager)
                ))
        };

        for (Command cmd : allCommands) {
            commands.put(cmd.getName().toLowerCase(), cmd);
            for (String alias : cmd.getAliases()) {
                commands.put(alias.toLowerCase(), cmd);
            }
        }
    }

    private static void processCommand(String input) {
        if (input.isEmpty()) return;

        String[] parts = input.split(" ");
        String commandKey = parts[0].toLowerCase();
        String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

        Command cmd = commands.get(commandKey);
        if (cmd != null) {
            cmd.execute(commandArgs);
        } else {
            System.out.println("Unknown command: " + commandKey);
            System.out.println("Type 'help' for available commands");
        }
    }
}