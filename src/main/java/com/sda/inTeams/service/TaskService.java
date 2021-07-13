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
public class TaskService implements DatabaseManageable<Task> {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public List<Task> getAllTasksOfTeam(long projectId) throws InvalidOperation {
        return taskRepository.findAllByProject(
                projectRepository.findById(projectId).orElseThrow(
                        () -> new InvalidOperation("Project id:" + projectId + " not found!")
                ));
    }

    public Optional<Task> getById(long taskId) {
        return taskRepository.findById(taskId);
    }

    public Task getByIdOrThrow(long taskId) throws InvalidOperation {
        return getById(taskId).orElseThrow(() -> new InvalidOperation("Task id:" + taskId + " not found!"));
    }

    public Task add(Task task) throws InvalidOperation {
        if (!Objects.isNull(task)) {
            return saveToDatabase(task);
        } else {
            throw new InvalidOperation("Cannot add task - Object is null!");
        }
    }

    public void delete(long taskId) throws InvalidOperation {
        Task taskToDelete = getByIdOrThrow(taskId);
        taskToDelete.setProject(null);
        taskToDelete = saveToDatabase(taskToDelete);
        taskRepository.delete(taskToDelete);
    }

    public void changeTaskStatus(long taskId, TaskStatus status) throws InvalidOperation {
        Task task = getByIdOrThrow(taskId);
        task.setStatus(status);
        saveToDatabase(task);
    }

    public Task saveToDatabase(Task task) {
        return taskRepository.save(task);
    }

}
