package com.sda.inTeams.service;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TaskService implements DatabaseManageable<Task> {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final CommentRepository commentRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

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
        /*for (Comment comment : taskToDelete.getComments()) {
            comment.setTask(null);
            comment = commentRepository.save(comment);
            commentRepository.delete(comment);
        }
        taskToDelete.getComments().clear();*/
        taskToDelete = saveToDatabase(taskToDelete);
        taskRepository.delete(taskToDelete);
    }

    public void setUserResponsible(long taskId, long userId) throws InvalidOperation {
        Task task = getByIdOrThrow(taskId);
        User user = userRepository.findById(userId).orElseThrow();

        task.setUserResponsible(user);
        List<Task> allByUserResponsible = taskRepository.findAllByUserResponsible(user);
        allByUserResponsible.add(task);
        user.setTaskResponsibleFor(new HashSet<>(allByUserResponsible));

        saveToDatabase(task);
    }

    public void addCommentToTask(long taskId, long commentId) throws InvalidOperation {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new InvalidOperation("Task id:" + taskId + " not found!"));
        Comment commentToAdd = commentRepository.findById(taskId).orElseThrow(() -> new InvalidOperation("Comment id:" + commentId + " not found!"));
        task.getComments().add(commentToAdd);
        saveToDatabase(task);
        commentToAdd.setTask(task);
        commentRepository.save(commentToAdd);
    }

    public void removeCommentFromTask(long taskId, long commentId) throws InvalidOperation {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new InvalidOperation("Task id:" + taskId + " not found!"));
        Comment commentToDelete = commentRepository.findById(taskId).orElseThrow(() -> new InvalidOperation("Comment id:" + commentId + " not found!"));
        if (task.getComments().contains(commentToDelete)) {
            commentRepository.delete(commentToDelete);
            saveToDatabase(task);
        } else {
            throw new InvalidOperation("Cannot remove comment id:" + commentId + " from task id:" + taskId + " - Comment is not assigned to this Task!");
        }
    }

    public void changeTaskStatus(long taskId, TaskStatus status) throws InvalidOperation {
        Task task = getByIdOrThrow(taskId);
        task.setStatus(status);
        saveToDatabase(task);
    }

    public Task saveToDatabase(Task task) {
        return taskRepository.save(task);
    }

    public List<User> getAllMembersByTask(Task task) {
        Project project = projectRepository.findByTasksContaining(task).orElseThrow();
        Team team = teamRepository.findByProjectsContaining(project).orElseThrow();
        return new ArrayList<>(team.getMembers());
    }

    public List<Task> getAllTasksByUserResponsibleFor(User user) {
        return taskRepository.findAllByUserResponsible(user);
    }
}
