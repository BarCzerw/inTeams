package com.sda.inTeams.service;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Project.ProjectStatus;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.repository.ProjectRepository;
import com.sda.inTeams.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamService teamService;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getAllProjectsOfTeam(long teamId) throws InvalidOperation {
        Team team = teamService.getTeamByIdOrError(teamId);
        return projectRepository.findAllByProjectOwner(team);
    }

    public Optional<Project> getProjectById(long projectId) {
        return projectRepository.findById(projectId);
    }

    public Project getProjectByIdOrError(long projectId) throws InvalidOperation {
        return getProjectById(projectId).orElseThrow(
                () -> new InvalidOperation("Project id:" + projectId + " not found!"));
    }

    public Project changeStatus(long projectId, ProjectStatus status) throws InvalidOperation {
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new InvalidOperation("Project id:" + projectId + " not found!"));
        project.setStatus(status);
        return saveProjectToDatabase(project);
    }

    private Project saveProjectToDatabase(Project project) {
        return projectRepository.save(project);
    }
}
