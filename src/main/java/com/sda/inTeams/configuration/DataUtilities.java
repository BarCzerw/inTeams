package com.sda.inTeams.configuration;

import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Project.ProjectStatus;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class DataUtilities {

    public static List<Project> getRandomProjectList(int size, Team team) {
        List<Project> projectList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            projectList.add(getRandomProject(team));
        }
        return projectList;
    }

    private static Project getRandomProject(Team team) {
        return Project.builder()
                .name(getRandomProjectName())
                .projectOwner(team)
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
        int count = random.nextInt(6) + 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(getRandomWord());
            if (i < count - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private static String getRandomWord() {
        String availableChars = "abcdefghijklmnopqrstuwxyz";
        StringBuilder newString = new StringBuilder();
        int size = random.nextInt(13) + 2;
        for (int j = 0; j < size; j++) {
            newString.append(availableChars.charAt(random.nextInt(availableChars.length())));
        }
        return newString.toString();
    }


    public static List<Team> getRandomTeamList(int size) {
        List<Team> teamList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            teamList.add(getRandomTeam());
        }
        return teamList;
    }

    private static Team getRandomTeam() {
        return Team.builder()
                .name(getRandomTeamName())
                .build();
    }

    private static String getRandomTeamName() {
        return "Team " + getRandomWord() + " " + getRandomNumberAsString();
    }

    private static String getRandomNumberAsString() {
        String randomNumberAsString = String.valueOf(random.nextInt(1000)+1);
        while (randomNumberAsString.length() < 4) {
            randomNumberAsString = "0" + randomNumberAsString;
        }
        return randomNumberAsString;
    }

    public static List<User> getRandomUserList(int size) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            users.add(getRandomUser());
        }
        return users;
    }

    private static User getRandomUser() {
        return User.builder()
                .firstName("Name" + getRandomNumberAsString())
                .lastName("Last" + getRandomNumberAsString())
                .build();
    }
}
