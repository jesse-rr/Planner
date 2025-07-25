package com.proj.commands;

import com.proj.models.HistoryEntry;
import com.proj.models.Project;
import com.proj.models.Section;
import com.proj.models.Task;
import com.proj.util.Command;
import com.proj.util.PlannerManager;
import com.proj.util.SaveManager;

import java.util.Arrays;

public class GenerateCommand implements Command {
    private PlannerManager manager;
    
    public GenerateCommand(PlannerManager manager) {
        this.manager = manager;
    }
    
    @Override
    public void execute(String[] args) {
        if (args.length < 2) {
            System.out.println(getUsage());
            return;
        }

        Project currentProject = manager.getCurrentProject();
        if (currentProject == null) {
            System.out.println("No project selected");
            return;
        }

        String type = args[0].toLowerCase();
        String name = args[1];

        if (type.equals("s")) {
            if (currentProject.getSection(name) != null) {
                System.out.println("Section already exists: " + name);
                return;
            }

            Section section = new Section(name);
            currentProject.getSections().add(section);

            manager.addHistoryEntry(new HistoryEntry(
                    "SECTION_CREATE",
                    "section",
                    name,
                    "",
                    section.toString()
            ));

            System.out.println("Added section: " + name);
        } else if (type.equals("t") && args.length >= 3) {
            String sectionName = args[1];
            String taskDesc = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            Section section = currentProject.getSection(sectionName);

            if (section == null) {
                System.out.println("Section not found: " + sectionName);
                return;
            }

            Task task = new Task(taskDesc);
            section.getTasks().add(task);

            manager.addHistoryEntry(new HistoryEntry(
                    "TASK_CREATE",
                    "task",
                    sectionName + ":" + taskDesc,
                    "",
                    task.toString()
            ));

            System.out.println("Added task to section '" + sectionName + "': " + taskDesc);
        } else {
            System.out.println(getUsage());
            return;
        }

        SaveManager.saveAll(manager);
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