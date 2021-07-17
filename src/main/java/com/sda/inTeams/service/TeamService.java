package com.sda.inTeams.service;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.ProjectRepository;
import com.sda.inTeams.repository.TeamRepository;
import com.sda.inTeams.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService implements DatabaseManageable<Team> {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    public Optional<Team> getById(long teamId) {
        return teamRepository.findById(teamId);
    }

    public Optional<Team> getTeamByName(String name) {
        return teamRepository.findByName(name);
    }

    public Team add(Team team) throws InvalidOperation {
        if (!Objects.isNull(team)) {
            return saveToDatabase(team);
        } else {
            throw new InvalidOperation("Cannot add team - Object is null!");
        }
    }

    public Team addWithOwner(Team team, long ownerId) throws InvalidOperation {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new InvalidOperation(""));
        team.setTeamOwner(owner);
        team.getMembers().add(owner);
        owner.getTeamsOwned().add(team);
        owner.getTeams().add(team);
        userRepository.save(owner);
        return saveToDatabase(team);
    }

    public void delete(long teamId) throws InvalidOperation {
        Team team = getByIdOrThrow(teamId);
        List<User> userList = userRepository.findAllByTeamsContaining(team);
        for (User user : userList) {
            user.getTeams().remove(team);
        }
        team.setMembers(new HashSet<>());
        //team.setProjects(new HashSet<>());
        team.setTeamOwner(null);
        userRepository.saveAll(userList);
        saveToDatabase(team);
        teamRepository.delete(team);
    }

    public void addUserToTeam(long teamId, long userId) throws InvalidOperation {
        Team team = getByIdOrThrow(teamId);
        User user = getUserByIdOrError(userId);

        if (!team.getMembers().contains(user)) {
            team.getMembers().add(user);
            saveToDatabase(team);
        } else {
            throw new InvalidOperation("Cannot add user id:" + user.getId() + " to team id:" + team.getId() + " - User is already a member");
        }
    }

    public void removeUserFromTeam(long teamId, long userId) throws InvalidOperation {
        Team team = getByIdOrThrow(teamId);
        User user = getUserByIdOrError(userId);

        if (team.getMembers().contains(user) && !team.getTeamOwner().equals(user)) {
            team.getMembers().remove(user);
            saveToDatabase(team);
        } else if (team.getTeamOwner().equals(user)) {
            throw new InvalidOperation("Cannot remove user id:" + userId + " from team id: " + teamId + " - User is an owner!");
        } else {
            throw new InvalidOperation("Cannot remove user id:" + userId + " from team id:" + teamId + " - User is not a member!");
        }
    }

    public void setOwnerOfTeam(long teamId, long userId) throws InvalidOperation {
        Team team = getByIdOrThrow(teamId);
        User user = getUserByIdOrError(userId);

        if (!isUserOwnerOfTeam(team, user) && isUserMemberOfTeam(team, user)) {
            team.setTeamOwner(user);
            saveToDatabase(team);
        } else if (!isUserMemberOfTeam(team, user)) {
            throw new InvalidOperation("Cannot make user id:" + userId + " owner of team id:" + teamId + " - User is not a team member!");
        } else {
            throw new InvalidOperation("Cannot make user id:" + userId + " owner of team id:" + teamId + " - User is already an owner!");
        }
    }

    public boolean isUserMemberOfTeam(Team team, User user) {
        return team.getMembers().contains(user);
    }

    public boolean isUserOwnerOfTeam(Team team, User user) {
        Optional<User> userOptional = Optional.ofNullable(team.getTeamOwner());
        return userOptional.map(value -> value.equals(user)).orElse(false);
    }

    public User getUserByIdOrError(long userId) throws InvalidOperation {
        return userRepository.findById(userId).orElseThrow(
                () -> new InvalidOperation("User id:" + userId + " not found!"));
    }

    public Team getByIdOrThrow(long teamId) throws InvalidOperation {
        return teamRepository.findById(teamId).orElseThrow(() -> new InvalidOperation("Team id:" + teamId + " not found!"));
    }

    public void addProjectToTeam(long teamId, long projectId) throws InvalidOperation {
        Team team = getByIdOrThrow(teamId);
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new InvalidOperation("Project id:" + projectId + " not found!"));
        if (!team.getProjects().contains(project)) {
            team.getProjects().add(project);
            saveToDatabase(team);
            project.setProjectOwner(team);
            projectRepository.save(project);
        } else {
            throw new InvalidOperation(
                    "Cannot add project id:" + projectId + " to team id:" + teamId + " - Project already belongs to Team"
            );
        }
    }

    public void removeProjectFromTeam(long teamId, long projectId) throws InvalidOperation {
        Team team = getByIdOrThrow(teamId);
        Project project = projectRepository.findById(projectId).orElseThrow();

        if (!team.getProjects().remove(project)) {
            throw new InvalidOperation("Cannot remove project id:" + projectId + " from team id:" + teamId + " - Project does not belong to Team");
        } else {
            saveToDatabase(team);
            projectRepository.delete(project);
        }

    }

    public Team saveToDatabase(Team team) {
        return teamRepository.save(team);
    }
}
