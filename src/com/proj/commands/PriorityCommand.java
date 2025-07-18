package com.proj.commands;

import com.proj.models.Priority;
import com.proj.models.Project;
import com.proj.models.Section;
import com.proj.models.Task;
import com.proj.util.Command;
import com.proj.util.PlannerManager;

public class PriorityCommand implements Command {
    private PlannerManager manager;

    public PriorityCommand(PlannerManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 3) {
            System.out.println(getUsage());
            return;
        }

        String target = args[0].toLowerCase();
        String identifier = args[1];
        Priority priority = Priority.fromString(args[2]);

        if (priority == Priority.UNDEFINED) {
            System.out.println("Invalid priority level. Use: low, medium, high, extreme");
            return;
        }

        switch (target) {
            case "project":
                setProjectPriority(identifier, priority);
                break;
            case "section":
                setSectionPriority(identifier, priority);
                break;
            case "task":
                setTaskPriority(args, priority);
                break;
            default:
                System.out.println("Unknown target: " + target);
                System.out.println(getUsage());
        }
    }

    private void setProjectPriority(String projectName, Priority priority) {
        Project project = manager.getProject(projectName);
        if (project == null) {
            System.out.println("Project not found: " + projectName);
            return;
        }

        project.setPriority(priority);
        System.out.println("Set priority for project '" + projectName + "' to: " + priority);
    }

    private void setSectionPriority(String sectionName, Priority priority) {
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

        section.setPriority(priority);
        System.out.println("Set priority for section '" + sectionName + "' to: " + priority);
    }

    private void setTaskPriority(String[] args, Priority priority) {
        if (args.length < 4) {
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
            task.setPriority(priority);
            System.out.println("Set priority for task " + (taskIndex + 1) + 
                             " in section '" + sectionName + "' to: " + priority);
        } catch (NumberFormatException e) {
            System.out.println("Task index must be a number");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Task not found at index: " + args[2]);
        }
    }

    @Override
    public String getName() {
        return "priority";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"prio", "rank"};
    }

    @Override
    public String getDescription() {
        return "Sets priority for projects, sections, or tasks";
    }

    @Override
    public String getUsage() {
        return "Usage:\n" +
             "  plan priority project <name> <level> - Set project priority\n" +
             "  plan priority section <name> <level> - Set section priority\n" +
             "  plan priority task <section> <index> <level> - Set task priority\n" +
             "  (priority levels: low, medium, high, extreme)\n" +
             "Aliases: prio, rank";
    }
}