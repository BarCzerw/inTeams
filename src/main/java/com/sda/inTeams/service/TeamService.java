package com.sda.inTeams.service;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamById(long teamId) {
        return teamRepository.findById(teamId);
    }

    public Team addTeam(Team team) throws InvalidOperation {
        if (!Objects.isNull(team)) {
            return teamRepository.save(team);
        } else {
            throw new InvalidOperation("Cannot add team - Object is null!");
        }
    }

    public void removeTeam(long teamId) throws InvalidOperation {
        Optional<Team> teamOptional = getTeamById(teamId);
        if (teamOptional.isPresent()) {
            teamRepository.delete(teamOptional.get());
        } else {
            throw new InvalidOperation("Cannot remove team id:" + teamId + " - Team not found!");
        }
    }

    public void addUserToTeam(Team team, User user) throws InvalidOperation {
        if (!team.getMembers().contains(user)) {
            team.getMembers().add(user);
            saveTeamToDatabase(team);
        } else {
            throw new InvalidOperation("Cannot add user id:" + user.getId() + " to team id:" + team.getId() + " - User is already a member");
        }
    }

    public void removeUserFromTeam(Team team, User user) throws InvalidOperation {
        if (team.getMembers().contains(user) && !team.getTeamOwner().equals(user)) {
            team.getMembers().remove(user);
            saveTeamToDatabase(team);
        } else if (team.getTeamOwner().equals(user)) {
            throw new InvalidOperation("Cannot remove user id:" + user.getId() + " from team id: " + team.getId() + " - User is an owner!");
        } else {
            throw new InvalidOperation("Cannot remove user id:" + user.getId() + " from team id:" + team.getId() + " - User is not a member!");
        }
    }

    public void setOwnerOfTeam(Team team, User user) throws InvalidOperation {
        if (isUserOwnerOfTeam(team, user)) {
            throw new InvalidOperation("Cannot make user id:" + user.getId() + " owner of team id:" + team.getId() + " - User is already an owner!");
        } else if (!isUserMemberOfTeam(team, user)) {
            throw new InvalidOperation("Cannot make user id:" + user.getId() + " owner of team id:" + team.getId() + " - User is no a team member!");
        } else {
            team.setTeamOwner(user);
        }
    }

    public boolean isUserMemberOfTeam(Team team, User user) {
        return team.getMembers().contains(user);
    }

    public boolean isUserOwnerOfTeam(Team team, User user) {
        return team.getTeamOwner().equals(user);
    }

    private void saveTeamToDatabase(Team team) {
        teamRepository.save(team);
    }

}
