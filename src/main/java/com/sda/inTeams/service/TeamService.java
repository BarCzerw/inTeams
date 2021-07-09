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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamById(long teamId) {
        return teamRepository.findById(teamId);
    }

    public Optional<Team> getTeamByName(String name) {
        return teamRepository.findByName(name);
    }

    public Team addTeam(Team team) throws InvalidOperation {
        if (!Objects.isNull(team)) {
            return teamRepository.save(team);
        } else {
            throw new InvalidOperation("Cannot add team - Object is null!");
        }
    }

    public void removeTeam(long teamId) throws InvalidOperation {
        Team team = getTeamByIdOrError(teamId);
        List<User> teamMembers = userRepository.findAllByTeamsContaining(team);
        for (User member : teamMembers) {
            Set<Team> teamsContaining = teamRepository.findAllByMembersContaining(member);
            for (Team teamCon : teamsContaining) {
                teamCon.getMembers().remove(member);
                teamRepository.saveAll(teamsContaining);
            }
        }
        userRepository.saveAll(teamMembers);
        team.setTeamOwner(null);
        teamRepository.delete(team);
    }

    public void addUserToTeam(long teamId, long userId) throws InvalidOperation {
        Team team = getTeamByIdOrError(teamId);
        User user = getUserByIdOrError(userId);

        if (!team.getMembers().contains(user)) {
            team.getMembers().add(user);
            saveTeamToDatabase(team);
        } else {
            throw new InvalidOperation("Cannot add user id:" + user.getId() + " to team id:" + team.getId() + " - User is already a member");
        }
    }

    public void removeUserFromTeam(long teamId, long userId) throws InvalidOperation {
        Team team = getTeamByIdOrError(teamId);
        User user = getUserByIdOrError(userId);

        if (team.getMembers().contains(user) && !team.getTeamOwner().equals(user)) {
            team.getMembers().remove(user);
            saveTeamToDatabase(team);
        } else if (team.getTeamOwner().equals(user)) {
            throw new InvalidOperation("Cannot remove user id:" + userId + " from team id: " + teamId + " - User is an owner!");
        } else {
            throw new InvalidOperation("Cannot remove user id:" + userId + " from team id:" + teamId + " - User is not a member!");
        }
    }

    public void setOwnerOfTeam(long teamId, long userId) throws InvalidOperation {
        Team team = getTeamByIdOrError(teamId);
        User user = getUserByIdOrError(userId);

        if (!isUserOwnerOfTeam(team, user) && isUserMemberOfTeam(team,user)) {
            team.setTeamOwner(user);
            saveTeamToDatabase(team);
        } else if (!isUserMemberOfTeam(team,user)) {
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

    public Team getTeamByIdOrError(long teamId) throws InvalidOperation {
        return teamRepository.findById(teamId).orElseThrow(() -> new InvalidOperation("Team id:" + teamId + " not found!"));
    }

    public void addProjectToTeam(long teamId, long projectId) throws InvalidOperation {
        Team team = getTeamByIdOrError(teamId);
        Project project = projectRepository.findById(projectId).orElseThrow();
        if (!team.getProjects().contains(project)) {
            team.getProjects().add(project);
            saveTeamToDatabase(team);
        } else {
            throw new InvalidOperation(
                    "Cannot add project id:" + projectId + " to team id:" + teamId + " - Project already belongs to Team"
            );
        }
    }

    private void saveTeamToDatabase(Team team) {
        teamRepository.save(team);
    }

}
