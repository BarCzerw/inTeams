package com.sda.inTeams.service;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Team.Team;
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


}
