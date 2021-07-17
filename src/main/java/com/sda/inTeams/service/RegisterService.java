package com.sda.inTeams.service;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.model.dto.RegisterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final TeamService teamService;
    private final UserService userService;

    public Team registerNewTeamWithUser(RegisterDTO registerDTO) throws InvalidOperation {
        User user = userService.createFromRegister(registerDTO);
        Team team = teamService.createFromRegister(registerDTO);

        team.setTeamOwner(user);
        List<User> teamMembers = userService.getUsersOfTeam(team.getId());
        teamMembers.add(user);
        //team.setMembers(new HashSet<>(List.of(user)));
        //user.getTeams().add(team);
        //user.getTeamsOwned().add(team);

        //userService.saveToDatabase(user);
        return teamService.saveToDatabase(team);
    }

}
