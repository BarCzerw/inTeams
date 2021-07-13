package com.sda.inTeams.service;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;
import com.sda.inTeams.repository.ProjectRepository;
import com.sda.inTeams.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getAllTasksOfTeam(long projectId) throws InvalidOperation {
        return taskRepository.findAllByProject(
                projectRepository.findById(projectId).orElseThrow(
                        () -> new InvalidOperation("Project id:" + projectId + " not found!")
                ));
    }

    public Optional<Task> getTaskById(long taskId) {
        return taskRepository.findById(taskId);
    }

    public Task getTaskByIdOrError(long taskId) throws InvalidOperation {
        return getTaskById(taskId).orElseThrow(() -> new InvalidOperation("Task id:" + taskId + " not found!"));
    }

    public Task addTask(Task task) throws InvalidOperation {
        if (!Objects.isNull(task)) {
            return saveTaskToDatabase(task);
        } else {
            throw new InvalidOperation("Cannot add task - Object is null!");
        }
    }

    public void deleteTask(long taskId) throws InvalidOperation {
        Task taskToDelete = getTaskByIdOrError(taskId);
        taskToDelete.setProject(null);
        taskToDelete = saveTaskToDatabase(taskToDelete);
        taskRepository.delete(taskToDelete);
    }

    public void changeTaskStatus(long taskId, TaskStatus status) throws InvalidOperation {
        Task task = getTaskByIdOrError(taskId);
        task.setStatus(status);
        saveTaskToDatabase(task);
    }

    private Task saveTaskToDatabase(Task task) {
        return taskRepository.save(task);
    }

}
