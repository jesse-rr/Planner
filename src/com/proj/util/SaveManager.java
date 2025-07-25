package com.proj.util;

import com.proj.models.Project;
import com.proj.models.HistoryEntry;
import com.proj.models.Section;
import com.proj.models.Priority;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SaveManager {
    private static final String DATA_DIR = "data";
    private static final String PROJECTS_DIR = "projects";
    private static final String CONFIG_FILE = "config.txt";
    private static final String PROJECTS_FILE = "projects.dat";
    private static final String HISTORY_FILE = "history.dat";
    private static Path dataPath;
    private static Path projectsPath;

    static {
        dataPath = Paths.get(DATA_DIR);
        projectsPath = dataPath.resolve(PROJECTS_DIR);
        try {
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
            if (!Files.exists(projectsPath)) {
                Files.createDirectories(projectsPath);
            }
        } catch (IOException e) {
            System.err.println("Error creating data directories: " + e.getMessage());
            dataPath = Paths.get(".");
            projectsPath = dataPath;
        }
    }

    public static void saveAll(PlannerManager manager) {
        saveToFile(PROJECTS_FILE, manager.getProjects());
        saveProjectsToTextFiles(manager.getProjects());
        saveToFile(HISTORY_FILE, manager.getHistory());
        saveConfig(manager.getCurrentProject() != null ?
                manager.getCurrentProject().getName() : null);
        System.out.println("Data saved to: " + dataPath.toAbsolutePath());
    }

    private static void saveProjectsToTextFiles(Map<String, Project> projects) {
        for (Map.Entry<String, Project> entry : projects.entrySet()) {
            saveProjectToTextFile(entry.getValue());
        }
    }

    private static void saveProjectToTextFile(Project project) {
        Path projectFilePath = projectsPath.resolve(project.getName() + ".txt");
        try (PrintWriter writer = new PrintWriter(projectFilePath.toFile())) {
            writer.println("=== PROJECT DETAILS ===");
            writer.println("Name: " + project.getName());
            writer.println("Objective: " + project.getObjective());
            writer.println("Date: " + project.getDate());
            writer.println("Completed: " + project.isCompleted());
            writer.println("Priority: " + project.getPriority());

            if (!project.getSections().isEmpty()) {
                writer.println("\n=== SECTIONS ===");
                for (Section section : project.getSections()) {
                    writer.println("\nSection: " + section.getName());
                    // Add section details here if your Section class has more fields
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving project text file for " +
                    project.getName() + ": " + e.getMessage());
        }
    }

    private static void saveConfig(String currentProjectName) {
        Path configPath = dataPath.resolve(CONFIG_FILE);
        try (PrintWriter writer = new PrintWriter(configPath.toFile())) {
            if (currentProjectName != null) {
                writer.println("current_project=" + currentProjectName);
            }
        } catch (IOException e) {
            System.out.println("Error saving config: " + e.getMessage());
        }
    }

    private static <T> void saveToFile(String filename, T data) {
        Path filePath = dataPath.resolve(filename);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.out.println("Error saving " + filename + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadAll(PlannerManager manager) {
        Map<String, Project> projects = (Map<String, Project>) loadFromFile(PROJECTS_FILE);
        if (projects != null) {
            manager.getProjects().putAll(projects);
        }

        List<HistoryEntry> history = (List<HistoryEntry>) loadFromFile(HISTORY_FILE);
        if (history != null) {
            manager.getHistory().addAll(history);
        }

        String currentProject = loadConfig();
        if (currentProject != null && projects != null &&
                projects.containsKey(currentProject.toLowerCase())) {
            manager.setCurrentProject(currentProject);
        }

        System.out.println("Data loaded from: " + dataPath.toAbsolutePath());
    }

    private static String loadConfig() {
        Path configPath = dataPath.resolve(CONFIG_FILE);
        if (!Files.exists(configPath)) {
            return null;
        }

        try (Scanner scanner = new Scanner(configPath)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("current_project=")) {
                    return line.substring("current_project=".length());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading config: " + e.getMessage());
        }
        return null;
    }

    private static Object loadFromFile(String filename) {
        Path filePath = dataPath.resolve(filename);
        if (!Files.exists(filePath)) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filePath.toFile()))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading " + filename + ": " + e.getMessage());
            return null;
        }
    }
}