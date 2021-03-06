package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Project.ProjectStatus;
import com.sda.inTeams.service.AuthorizationService;
import com.sda.inTeams.service.ProjectService;
import com.sda.inTeams.service.TaskService;
import com.sda.inTeams.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final TeamService teamService;
    private final TaskService taskService;
    private final AuthorizationService authorizationService;

    @GetMapping("/{id}")
    public String getProject(Model model, Principal principal, @PathVariable(name = "id") long projectId) {
        try {
            Project project = projectService.getByIdOrThrow(projectId);
            if (authorizationService.isUserEligibleToSeeProjectDetails(principal, project)) {
                model.addAttribute("projectDetails", projectService.getByIdOrThrow(projectId));
                model.addAttribute("projectTasks", taskService.getAllTasksOfTeam(projectId));
                return "project-details";
            } else {
                //unauthorized access
            }
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("/add")
    public String addProjectForm(Model model, @RequestParam(name = "teamId") long teamId) {
        model.addAttribute("newProject", new Project());
        model.addAttribute("teamId", teamId);
        model.addAttribute("statuses", new ArrayList<>(List.of(ProjectStatus.values())));
        return "project-add-form";
    }

    @PostMapping("/add")
    public String addProject(Project project, long ownerId) {
        try {
            project.setProjectOwner(teamService.getByIdOrThrow(ownerId));
            Project addedProject = projectService.add(project);
            return "redirect:/project/" + addedProject.getId();
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/team/all";
        }
    }

    @GetMapping("/edit/{id}")
    public String editProject(Model model, @PathVariable(name = "id") long projectId) {
        try {
            Project projectToEdit = projectService.getByIdOrThrow(projectId);
            model.addAttribute("newProject", projectToEdit);
            model.addAttribute("teamId", projectToEdit.getProjectOwner().getId());
            model.addAttribute("statuses", new ArrayList<>(List.of(ProjectStatus.values())));
            return "project-add-form";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/project/all";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable(name = "id") long projectId) {
        try {
            Project projectToDelete = projectService.getByIdOrThrow(projectId);
            long redirectTeamId = projectToDelete.getProjectOwner().getId();
            projectService.delete(projectId);
            return "redirect:/team/" + redirectTeamId;
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/project/all";
        }
    }

}
