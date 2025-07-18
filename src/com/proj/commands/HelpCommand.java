package com.proj.commands;

import com.proj.util.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class HelpCommand implements Command {
    private Collection<Command> allCommands;
    
    public HelpCommand(Collection<Command> allCommands) {
        this.allCommands = allCommands;
    }
    
    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            // Show general help
            System.out.println("Available commands:");
            for (Command cmd : allCommands) {
                System.out.printf("  %-10s - %s\n", 
                    String.join(", ", getAllCommandForms(cmd)), 
                    cmd.getDescription());
            }
            System.out.println("\nUse 'help <command>' for detailed usage");
        } else {
            // Show specific command help
            String cmdName = args[0].toLowerCase();
            for (Command cmd : allCommands) {
                if (cmd.getName().equals(cmdName) || 
                    Arrays.asList(cmd.getAliases()).contains(cmdName)) {
                    System.out.println(cmd.getDescription());
                    System.out.println(cmd.getUsage());
                    return;
                }
            }
            System.out.println("Command not found: " + args[0]);
        }
    }
    
    private String[] getAllCommandForms(Command cmd) {
        List<String> forms = new ArrayList<>();
        forms.add(cmd.getName());
        forms.addAll(Arrays.asList(cmd.getAliases()));
        return forms.toArray(new String[0]);
    }
    
    @Override
    public String getName() { return "help"; }
    
    @Override
    public String[] getAliases() { return new String[]{"h", "man", "?"}; }
    
    @Override
    public String getDescription() { return "Shows help information"; }
    
    @Override
    public String getUsage() { 
        return "Usage:\n  help - list all commands\n  help <command> - show command details"; 
    }
}