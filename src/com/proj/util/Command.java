package com.proj.util;

public interface Command {
    void execute(String[] args);
    String getName();
    String[] getAliases();
    String getDescription();
    String getUsage();
}