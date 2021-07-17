package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Project.ProjectStatus;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.service.ProjectService;
import com.sda.inTeams.service.TaskService;
import com.sda.inTeams.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final TeamService teamService;
    private final TaskService taskService;

    @GetMapping("/all")
    public String getAllProjects(Model model) {
        model.addAttribute("projectList", projectService.getAll());
        return "project-list";
    }

    @GetMapping("/{id}")
    public String getProject(Model model, @PathVariable(name = "id") long projectId) {
        try {
            model.addAttribute("projectDetails", projectService.getByIdOrThrow(projectId));
            model.addAttribute("projectTasks", taskService.getAllTasksOfTeam(projectId));
            return "project-details";
        } catch (InvalidOperation invalidOperation) {
            return "redirect:/project/all";
        }
    }

    @GetMapping("/add")
    public String addProjectForm(Model model, @RequestParam(name="teamId") long teamId) {
        model.addAttribute("newProject", new Project());
        model.addAttribute("teamId", teamId);
        return "project-add-form";
    }

    @PostMapping("/add")
    public String addProject(Project project, long ownerId) {
        try {
            project.setProjectOwner(teamService.getByIdOrThrow(ownerId));
            project.setStatus(ProjectStatus.NOT_STARTED);
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
            model.addAttribute("newProject", projectService.getByIdOrThrow(projectId));
            model.addAttribute("teamId", new Team());
            return "project-add-form";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/project/all";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable(name = "id") long projectId) {
        try {
            projectService.delete(projectId);
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
        }
        return "redirect:/project/all";
    }

}
