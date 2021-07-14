package com.sda.inTeams.configuration;

import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Project.ProjectStatus;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class DataUtilities {

    public static List<Project> getRandomProjectList(int size) {
        List<Project> projectList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            projectList.add(getRandomProject());
        }
        return projectList;
    }

    private static Project getRandomProject() {
        return Project.builder()
                .name(getRandomProjectName())
                .status(getRandomProjectStatus())
                .build();
    }

    private static ProjectStatus getRandomProjectStatus() {
        List<ProjectStatus> availableStatuses = new ArrayList<>(List.of(ProjectStatus.values()));
        return availableStatuses.get(random.nextInt(availableStatuses.size() - 1));
    }

    private static String getRandomProjectName() {
        return "Project " + getRandomTextString() + " " + (random.nextInt(50)+1);
    }

    public static List<Task> getRandomTaskList(int size, Project project) {
        List<Task> returnList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            returnList.add(getRandomTask(project));
        }
        return returnList;
    }

    public static List<Comment> getRandomCommentList(int size, Task task) {
        List<Comment> commentList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            commentList.add(getRandomComment(task));
        }
        return commentList;
    }

    private static final Random random = new Random();

    private static String getRandomTaskDescription() {
        return "Przykładowy opis zadania " + random.nextInt(1000000);
    }

    private static TaskStatus getRandomTaskStatus() {
        List<TaskStatus> availableStatuses = new ArrayList<>(List.of(TaskStatus.values()));
        return availableStatuses.get(random.nextInt(availableStatuses.size() - 1));
    }

    private static Task getRandomTask(Project project) {
        return Task.builder()
                .description(DataUtilities.getRandomTaskDescription())
                .status(DataUtilities.getRandomTaskStatus())
                .project(project)
                .build();
    }

    private static Comment getRandomComment(Task task) {
        return Comment.builder()
                .text(getRandomTextString())
                .task(task)
                .build();
    }

    private static String getRandomTextString() {
        String availableChars = "abcdefghijklmnopqrstuwxyz";
        int size;
        int count = random.nextInt(6) + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            StringBuilder newString = new StringBuilder();
            size = random.nextInt(13) + 2;
            for (int j = 0; j < size; j++) {
                newString.append(availableChars.charAt(random.nextInt(availableChars.length())));
            }
            sb.append(newString);
            if (i < count - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }


}
