package com.sda.inTeams.configuration.entitiesGenerator;

import com.sda.inTeams.configuration.StringUtilities;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class TaskGenerator {

    private static Random random = new Random();

    public static List<Task> generateTasks(int size) {
        List<Task> tasks = new ArrayList<>();
        IntStream.range(0, size).forEach(ind -> tasks.add(generateSingleTask()));
        return tasks;
    }

    public static Task generateSingleTask() {
        return Task.builder()
                .description(StringUtilities.getRandomTextString())
                .status(generateTaskStatus())
                .build();
    }

    private static TaskStatus generateTaskStatus() {
        List<TaskStatus> taskStatuses = new ArrayList<>(List.of(TaskStatus.values()));
        return taskStatuses.get(random.nextInt(taskStatuses.size()));
    }
}
