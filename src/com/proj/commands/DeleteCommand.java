package com.proj.commands;

import com.proj.models.HistoryEntry;
import com.proj.models.Project;
import com.proj.models.Section;
import com.proj.models.Task;
import com.proj.util.Command;
import com.proj.util.PlannerManager;

public class DeleteCommand implements Command {
    private PlannerManager manager;

    public DeleteCommand(PlannerManager manager) {
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
                deleteProject(identifier);
                break;
            case "section":
                deleteSection(identifier);
                break;
            case "task":
                deleteTask(args);
                break;
            default:
                System.out.println("Unknown target: " + target);
                System.out.println(getUsage());
        }
    }

    private void deleteProject(String projectName) {
        Project project = manager.getProject(projectName);
        if (project == null) {
            System.out.println("Project not found: " + projectName);
            return;
        }

        manager.addHistoryEntry(new HistoryEntry(
                "PROJECT_DELETE",
                "project",
                project.getName(),
                project.toString(),
                ""
        ));

        manager.getProjects().remove(projectName.toLowerCase());
        System.out.println("Deleted project: " + projectName);
    }

    private void deleteSection(String sectionName) {
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

        manager.addHistoryEntry(new HistoryEntry(
                "SECTION_DELETE",
                "section",
                section.getName(),
                section.toString(),
                ""
        ));

        currentProject.getSections().remove(section);
        System.out.println("Deleted section: " + sectionName);
    }

    private void deleteTask(String[] args) {
        if (args.length < 3) {
            System.out.println("Missing section name or task index");
            return;
        }

        Project currentProject = manager.getCurrentProject();
        if (currentProject == null) {
            System.out.println("No project selected");
            return;
        }

        String sectionName = args[1];
        Section section = currentProject.getSection(sectionName);
        if (section == null) {
            System.out.println("Section not found: " + sectionName);
            return;
        }

        try {
            int taskIndex = Integer.parseInt(args[2]) - 1;
            Task task = section.getTask(taskIndex);

            manager.addHistoryEntry(new HistoryEntry(
                    "TASK_DELETE",
                    "task",
                    section.getName() + ":" + task.getDescription(),
                    task.toString(),
                    ""
            ));

            section.getTasks().remove(taskIndex);
            System.out.println("Deleted task " + (taskIndex + 1) + " from section '" + sectionName + "'");
        } catch (NumberFormatException e) {
            System.out.println("Task index must be a number");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Task not found at index: " + args[2]);
        }
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"d", "del", "rm"};
    }

    @Override
    public String getDescription() {
        return "Deletes projects, sections, or tasks";
    }

    @Override
    public String getUsage() {
        return "Usage:\n"
                + "  plan delete project <name> - Delete a project\n"
                + "  plan delete section <name> - Delete a section\n"
                + "  plan delete task <section> <index> - Delete a task\n"
                + "Aliases: d, del, rm";
    }
}