package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.User.AccountRole;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.AccountRoleRepository;
import com.sda.inTeams.service.AuthorizationService;
import com.sda.inTeams.service.CommentService;
import com.sda.inTeams.service.TeamService;
import com.sda.inTeams.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final TeamService teamService;
    private final CommentService commentService;
    private final AccountRoleRepository accountRoleRepository;
    private final AuthorizationService authorizationService;

    @GetMapping("/{id}")
    public String getUser(Model model, Principal principal, @PathVariable(name = "id") long userId) {
        try {
            User user = authorizationService.getUserCredentials(principal).orElseThrow();
            if (authorizationService.isUserEligibleToSeeUserDetails(principal, user)) {
                model.addAttribute("userDetails", userService.getByIdOrThrow(userId));
                model.addAttribute("teamsAssignedTo", teamService.getTeamsContainingMember(userId));
                model.addAttribute("teamsOwnedBy", teamService.getTeamsOwnedBy(userId));
                model.addAttribute("commentsCreated", commentService.getAllUserComments(userId));
                return "user-details";
            } else {
                //unauthorized access
            }
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("/myaccount")
    public String getMyAccountPage(Model model, Principal principal) {
        try {
            User user = authorizationService.getUserCredentials(principal).orElseThrow();
            return getUser(model, principal, user.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editUser(Model model, @PathVariable(name = "id") long userId) {
        try {
            User userToEdit = userService.getByIdOrThrow(userId);
            model.addAttribute("newUser", userToEdit);
            AccountRole adminRole = accountRoleRepository.findByName("ROLE_ADMIN").orElseThrow();
            model.addAttribute("isAdmin", userToEdit.getRoles().contains(adminRole));
            return "user-edit-form";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/";
        }
    }

    @PostMapping("/save")
    public String saveEditedUser(Model model, User user, boolean isAdmin) {
        user.setRoles(new HashSet<>(
                List.of(
                        accountRoleRepository
                                .findByName(isAdmin ? "ROLE_ADMIN" : "ROLE_USER")
                                .orElseThrow())));
        user = userService.saveToDatabase(user);
        return "redirect:/user/" + user.getId();
    }

    @GetMapping("/delete/{id}")
    public String deleteTeam(@PathVariable(name = "id") long userId) {
        try {
            User userToDelete = userService.getByIdOrThrow(userId);
            userService.delete(userId);
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
        }
        return "redirect:/user/all";
    }
}
