package com.sda.inTeams.service;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Project.ProjectStatus;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.repository.ProjectRepository;
import com.sda.inTeams.repository.TaskRepository;
import com.sda.inTeams.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getAllProjectsOfTeam(long teamId) throws InvalidOperation {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new InvalidOperation("Team id:" + teamId + " not found!"));
        return projectRepository.findAllByProjectOwner(team);
    }

    public Optional<Project> getProjectById(long projectId) {
        return projectRepository.findById(projectId);
    }

    public Project getProjectByIdOrError(long projectId) throws InvalidOperation {
        return getProjectById(projectId).orElseThrow(
                () -> new InvalidOperation("Project id:" + projectId + " not found!"));
    }

    public Project addProject(Project project) throws InvalidOperation {
        if (!Objects.isNull(project)) {
            return projectRepository.save(project);
        } else {
            throw new InvalidOperation("Cannot add project - Object is null!");
        }
    }

    public void deleteProject(long projectId) throws InvalidOperation {
        Project projectToDelete = projectRepository.findById(projectId).orElseThrow(() -> new InvalidOperation("Task id:" + projectId + " not found!"));
        projectToDelete.setProjectOwner(null);
        projectToDelete = saveProjectToDatabase(projectToDelete);
        projectRepository.delete(projectToDelete);
    }

    public Project changeStatus(long projectId, ProjectStatus status) throws InvalidOperation {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new InvalidOperation("Project id:" + projectId + " not found!"));
        project.setStatus(status);
        return saveProjectToDatabase(project);
    }

    public void addTaskToProject(long projectId, long taskId) throws InvalidOperation {
        Project project = getProjectByIdOrError(projectId);
        Task task = taskRepository.findById(taskId).orElseThrow( () -> new InvalidOperation("Task id:" + taskId + " not found!") );
        if (!project.getTasks().contains(task)) {
            project.getTasks().add(task);
            saveProjectToDatabase(project);
        } else {
            throw new InvalidOperation("Cannot add task id:" + taskId + " to project id:" + projectId + " - Task already assigned to this Project");
        }
    }

    public void removeTaskFromProject(long projectId, long taskId) throws InvalidOperation {
        Task taskToRemove = taskRepository.findById(taskId).orElseThrow(() -> new InvalidOperation("Task id:" + taskId + " not found!"));
        Project projectToRemoveFrom = projectRepository.findById(projectId).orElseThrow(() -> new InvalidOperation("Project id:" + projectId + " not found!"));

        if (projectToRemoveFrom.getTasks().contains(taskToRemove)) {
            projectToRemoveFrom.getTasks().remove(taskToRemove);
            saveProjectToDatabase(projectToRemoveFrom);
        } else {
            throw new InvalidOperation("Cannot remove task id:" + taskId + " from project id:" + projectId + " - Task is not assigned to this Project!");
        }

    }

    private Project saveProjectToDatabase(Project project) {
        return projectRepository.save(project);
    }
}
