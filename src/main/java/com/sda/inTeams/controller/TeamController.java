package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.service.ProjectService;
import com.sda.inTeams.service.TeamService;
import com.sda.inTeams.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Service
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;
    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping("/all")
    public String getAllTeams(Model model) {
        model.addAttribute("teamList", teamService.getAll());
        return "team-list";
    }

    @GetMapping("/{id}")
    public String getTeam(Model model, @PathVariable(name = "id") long teamId) {
        try {
            model.addAttribute("teamDetails", teamService.getByIdOrThrow(teamId));
            model.addAttribute("teamProjects", projectService.getAllProjectsOfTeam(teamId));
            model.addAttribute("teamMembers", userService.getAllMembersOfTeam(teamId));
            return "team-details";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "team-list";
        }
    }

    @GetMapping("/add")
    public String addTeamForm(Model model, long userId) {
        try {
            User projectOwner = userService.getByIdOrThrow(userId);
            model.addAttribute("newTeam", new Team());
            model.addAttribute("ownerId", userId);
            return "team-add-form";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/team/all";
        }
    }

    @PostMapping("/add")
    public String addTeam(Team team, long userId) {
        try {
            Team addedTeam = teamService.createWithOwner(team, userId);
            return "redirect:/team/" + addedTeam.getId();
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/team/all";
        }
    }

    @GetMapping("/edit/{id}")
    public String editTeam(Model model, @PathVariable(name = "id") long teamId) {
        try {
            Team teamToEdit = teamService.getByIdOrThrow(teamId);
            model.addAttribute("newTeam", teamToEdit);
            model.addAttribute("ownerId", teamToEdit.getTeamOwner().getId());
            return "team-add-form";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/team/all";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable(name = "id") long teamId) {
        try {
            Team teamToDelete = teamService.getByIdOrThrow(teamId);
            teamService.delete(teamId);
            return "redirect:/team/all";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/team/all";
        }
    }

    @GetMapping("/removeUser")
    public String removeUserFromTeam(long teamId, long userId) {
        try {
            teamService.removeUserFromTeam(teamId, userId);
            return "redirect:/team/" + teamId;
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/team/all";
        }
    }
}
