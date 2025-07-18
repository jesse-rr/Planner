package com.proj.commands;

import com.proj.models.HistoryEntry;
import com.proj.models.Project;
import com.proj.models.Section;
import com.proj.models.Task;
import com.proj.util.Command;
import com.proj.util.PlannerManager;

import java.util.Arrays;

public class ChangeCommand implements Command {
    private PlannerManager manager;

    public ChangeCommand(PlannerManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println(getUsage());
            return;
        }

        String target = args[0].toLowerCase();
        String identifier = args[1];

        switch (target) {
            case "project":
                handleProjectChange(args, identifier);
                break;
            case "section":
                handleSectionChange(args, identifier);
                break;
            case "task":
                handleTaskChange(args, identifier);
                break;
            default:
                System.out.println("Unknown target: " + target);
                System.out.println(getUsage());
        }
    }

    private void handleProjectChange(String[] args, String projectName) {
        Project project = manager.getProject(projectName);
        if (project == null) {
            System.out.println("Project not found: " + projectName);
            return;
        }

        if (args.length >= 3 && args[2].equalsIgnoreCase("finished")) {
            // Change project status
            boolean newStatus = args.length >= 4 ? Boolean.parseBoolean(args[3]) : true;
            boolean oldStatus = project.isCompleted();

            manager.addHistoryEntry(new HistoryEntry(
                    "PROJECT_STATUS",
                    "project",
                    project.getName(),
                    String.valueOf(oldStatus),
                    String.valueOf(newStatus)
            ));

            project.setCompleted(newStatus);
            System.out.println("Marked project '" + projectName + "' as " +
                    (newStatus ? "finished" : "active"));
        }
        else if (args.length >= 3 && args[2].equalsIgnoreCase("objective")) {
            // Change project objective
            if (args.length < 4) {
                System.out.println("Missing new objective");
                return;
            }
            String newObjective = String.join(" ", Arrays.copyOfRange(args, 3, args.length));
            String oldObjective = project.getObjective();

            manager.addHistoryEntry(new HistoryEntry(
                    "PROJECT_OBJECTIVE",
                    "project",
                    project.getName(),
                    oldObjective,
                    newObjective
            ));

            project.setObjective(newObjective);
            System.out.println("Changed objective for project '" + projectName + "'");
        }
        else if (args.length >= 3) {
            // Rename project
            String newName = args[2];

            manager.addHistoryEntry(new HistoryEntry(
                    "PROJECT_RENAME",
                    "project",
                    project.getName(),
                    project.getName(),
                    newName
            ));

            manager.getProjects().remove(project.getName().toLowerCase());
            project.setName(newName);
            manager.getProjects().put(newName.toLowerCase(), project);
            System.out.println("Renamed project to: " + newName);
        } else {
            System.out.println(getUsage());
        }
    }

    private void handleSectionChange(String[] args, String sectionName) {
        Project currentProject = manager.getCurrentProject();
        if (currentProject == null) {
            System.out.println("No project selected");
            return;
        }

        Section section = currentProject.getSection(sectionName);
        if (section == null) {
            System.out.println("Section not found: " + sectionName);
            return;
        }

        if (args.length >= 3 && args[2].equalsIgnoreCase("finished")) {
            // Mark all tasks in section as completed
            boolean newStatus = args.length >= 4 ? Boolean.parseBoolean(args[3]) : true;
            int completedCount = 0;

            for (Task task : section.getTasks()) {
                boolean oldStatus = task.isCompleted();
                if (oldStatus != newStatus) {
                    manager.addHistoryEntry(new HistoryEntry(
                            "TASK_STATUS",
                            "task",
                            section.getName() + ":" + task.getDescription(),
                            String.valueOf(oldStatus),
                            String.valueOf(newStatus)
                    ));
                    task.setCompleted(newStatus);
                    completedCount++;
                }
            }

            System.out.println("Marked " + completedCount + " tasks in section '" +
                    sectionName + "' as " + (newStatus ? "completed" : "pending"));
        }
        else if (args.length >= 3) {
            // Rename section
            String newName = args[2];

            manager.addHistoryEntry(new HistoryEntry(
                    "SECTION_RENAME",
                    "section",
                    section.getName(),
                    section.getName(),
                    newName
            ));

            section.setName(newName);
            System.out.println("Renamed section to: " + newName);
        } else {
            System.out.println(getUsage());
        }
    }

    private void handleTaskChange(String[] args, String taskIdentifier) {
        Project currentProject = manager.getCurrentProject();
        if (currentProject == null) {
            System.out.println("No project selected");
            return;
        }

        if (args.length < 3) {
            System.out.println(getUsage());
            return;
        }

        String sectionName = args[1];
        Section section = currentProject.getSection(sectionName);
        if (section == null) {
            System.out.println("Section not found: " + sectionName);
            return;
        }

        try {
            int taskIndex = Integer.parseInt(taskIdentifier) - 1;
            Task task = section.getTask(taskIndex);

            if (args[2].equalsIgnoreCase("finished")) {
                // Change task completion status
                boolean newStatus = args.length >= 4 ? Boolean.parseBoolean(args[3]) : true;
                boolean oldStatus = task.isCompleted();

                manager.addHistoryEntry(new HistoryEntry(
                        "TASK_STATUS",
                        "task",
                        section.getName() + ":" + task.getDescription(),
                        String.valueOf(oldStatus),
                        String.valueOf(newStatus)
                ));

                task.setCompleted(newStatus);
                System.out.println("Marked task " + (taskIndex + 1) + " in section '" +
                        sectionName + "' as " + (newStatus ? "completed" : "pending"));
            }
            else if (args[2].equalsIgnoreCase("move")) {
                // Move task to different section
                if (args.length < 4) {
                    System.out.println("Missing target section");
                    return;
                }
                Section targetSection = currentProject.getSection(args[3]);
                if (targetSection == null) {
                    System.out.println("Target section not found: " + args[3]);
                    return;
                }

                manager.addHistoryEntry(new HistoryEntry(
                        "TASK_MOVE",
                        "task",
                        task.getDescription(),
                        section.getName(),
                        targetSection.getName()
                ));

                section.getTasks().remove(taskIndex);
                targetSection.addTask(task.getDescription());
                System.out.println("Moved task to section '" + targetSection.getName() + "'");
            }
            else {
                // Change task description
                String newDescription = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                String oldDescription = task.getDescription();

                manager.addHistoryEntry(new HistoryEntry(
                        "TASK_EDIT",
                        "task",
                        section.getName() + ":" + oldDescription,
                        oldDescription,
                        newDescription
                ));

                task.setDescription(newDescription);
                System.out.println("Changed task description to: " + newDescription);
            }
        } catch (NumberFormatException e) {
            System.out.println("Task index must be a number");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Task not found at index: " + taskIdentifier);
        }
    }

    @Override
    public String getName() {
        return "change";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ch", "modify", "update"};
    }

    @Override
    public String getDescription() {
        return "Modifies projects, sections, or tasks (rename, status, objective, etc.)";
    }

    @Override
    public String getUsage() {
        return "Usage:\n" +
                "  plan change project <name> <new-name> - Rename project\n" +
                "  plan change project <name> finished [true|false] - Mark project status\n" +
                "  plan change project <name> objective <new-objective> - Change project objective\n\n" +
                "  plan change section <name> <new-name> - Rename section\n" +
                "  plan change section <name> finished [true|false] - Mark all tasks in section\n\n" +
                "  plan change task <index> <section> <new-desc> - Edit task description\n" +
                "  plan change task <index> <section> finished [true|false] - Mark task status\n" +
                "  plan change task <index> <section> move <target-section> - Move task\n\n" +
                "Aliases: ch, modify, update";
    }
}