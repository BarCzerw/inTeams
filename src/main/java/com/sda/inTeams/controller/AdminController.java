package com.sda.inTeams.controller;

import com.sda.inTeams.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AuthorizationService authorizationService;
    private final TeamService teamService;
    private final UserService userService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final CommentService commentService;
    private final DatabaseService databaseService;

    @GetMapping()
    public String getAdminPanel(Model model, Principal principal) {
        if (authorizationService.isUserAdmin(principal)) {
            model.addAttribute("dbInfo", databaseService.getDatabaseInfo());
            return "admin-panel";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/teams")
    public String getAllTeams(Model model, Principal principal) {
        if (authorizationService.isUserAdmin(principal)) {
            model.addAttribute("teamList", teamService.getAll());
            return "admin-team-list";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/users")
    public String getAllUsers(Model model, Principal principal) {
        if (authorizationService.isUserAdmin(principal)) {
            model.addAttribute("userList", userService.getAll());
            return "admin-user-list";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/projects")
    public String getAllProjects(Model model, Principal principal) {
        if (authorizationService.isUserAdmin(principal)) {
            model.addAttribute("projectList", projectService.getAll());
            return "admin-project-list";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/tasks")
    public String getAllTasks(Model model, Principal principal) {
        if (authorizationService.isUserAdmin(principal)) {
            model.addAttribute("taskList", taskService.getAll());
            return "admin-task-list";
        } else {
            return "redirect:/";
        }
    }


    @GetMapping("/comments")
    public String getAllComments(Model model, Principal principal) {
        if (authorizationService.isUserAdmin(principal)) {
            model.addAttribute("commentList", commentService.getAll());
            return "admin-comment-list";
        } else {
            return "redirect:/";
        }
    }
}
