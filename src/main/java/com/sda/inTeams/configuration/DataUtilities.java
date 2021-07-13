package com.sda.inTeams.configuration;

import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataUtilities {

    private static final Random random = new Random();

    private static String getRandomTaskDescription() {
        return "Przykładowy opis zadania " + random.nextInt(1000000);
    }

    private static TaskStatus getRandomTaskStatus() {
        List<TaskStatus> availableStatuses = new ArrayList<>(List.of(TaskStatus.values()));
        return availableStatuses.get(random.nextInt(availableStatuses.size() - 1));
    }

    private static Task getRandomDetachedTask() {
        return Task.builder().description(DataUtilities.getRandomTaskDescription()).status(DataUtilities.getRandomTaskStatus()).build();
    }

    public static List<Task> getRandomTaskList(int size) {
        List<Task> returnList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            returnList.add(getRandomDetachedTask());
        }
        return returnList;
    }

}
