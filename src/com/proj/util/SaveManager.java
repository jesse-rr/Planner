package com.proj.util;

import com.proj.models.Project;
import com.proj.models.HistoryEntry;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SaveManager {
    private static final String CONFIG_DIR = ".project-planner";
    private static final String SAVE_FILE = "projects.dat";
    private static Path savePath;

    static {
        // Try to find existing save location
        Path rootPath = Paths.get(System.getProperty("user.home"), CONFIG_DIR);
        if (Files.exists(rootPath)) {
            savePath = rootPath.resolve(SAVE_FILE);
        } else {
            // Default to current directory if no config exists
            savePath = Paths.get(SAVE_FILE);
        }
    }

    public static void saveAll(PlannerManager manager) {
        Scanner scanner = new Scanner(System.in);

        // If no existing save location, prompt user
        if (!Files.exists(savePath.getParent())) {
            System.out.print("No save location found. Enter directory path (blank for current dir): ");
            String userPath = scanner.nextLine().trim();

            if (!userPath.isEmpty()) {
                savePath = Paths.get(userPath, CONFIG_DIR, SAVE_FILE);
            } else {
                savePath = Paths.get(CONFIG_DIR, SAVE_FILE);
            }

            try {
                Files.createDirectories(savePath.getParent());
            } catch (IOException e) {
                System.out.println("Error creating save directory: " + e.getMessage());
                return;
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(savePath.toFile()))) {

            oos.writeObject(manager.getProjects());
            oos.writeObject(manager.getHistory());
            System.out.println("Saved data to: " + savePath);

        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadAll(PlannerManager manager) {
        if (!Files.exists(savePath)) {
            System.out.println("No save file found at: " + savePath);
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(savePath.toFile()))) {

            Map<String, Project> projects = (Map<String, Project>) ois.readObject();
            List<HistoryEntry> history = (List<HistoryEntry>) ois.readObject();

            manager.getProjects().putAll(projects);
            manager.getHistory().addAll(history);
            System.out.println("Loaded data from: " + savePath);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }
}