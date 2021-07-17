package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;
import com.sda.inTeams.service.CommentService;
import com.sda.inTeams.service.ProjectService;
import com.sda.inTeams.service.TaskService;
import com.sda.inTeams.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;
    private final TeamService teamService;
    private final ProjectService projectService;
    private final CommentService commentService;

    @GetMapping("/all")
    public String getAllTasks(Model model) {
        model.addAttribute("taskList", taskService.getAll());
        return "task-list";
    }

    @GetMapping("/{id}")
    public String getTask(Model model, @PathVariable(name = "id") long taskId) {
        try {
            model.addAttribute("taskDetails", taskService.getByIdOrThrow(taskId));
            model.addAttribute("taskComments", commentService.getAllByTask(taskId));
            return "task-details";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "task-list";
        }
    }

    @GetMapping("/team")
    public String getProjectTasks(Model model, @RequestParam(name = "teamId") long teamId) {
        try {
            model.addAttribute("taskList", taskService.getAllTasksOfTeam(teamId));
            return "task-list";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return getAllTasks(model);
        }
    }

    @GetMapping("/add")
    public String addTaskForm(Model model, @RequestParam(name = "projectId") long projectId) {
        try {
            Project taskOwner = projectService.getByIdOrThrow(projectId);
            model.addAttribute("newTask", new Task());
            model.addAttribute("ownerId", projectId);
            return "task-add-form";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/project/all";
        }
    }

    @PostMapping("/add")
    public String addTask(Task task, long ownerId) {
        try {
            task.setStatus(TaskStatus.NOT_STARTED);
            task.setProject(projectService.getByIdOrThrow(ownerId));
            Task addedTask = taskService.add(task);
            return "redirect:/task/" + addedTask.getId();
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/task/all";
        }
    }

    @GetMapping("/edit/{id}")
    public String editTask(Model model, @PathVariable(name = "id") long taskId) {
        try {
            Task taskToEdit = taskService.getByIdOrThrow(taskId);
            model.addAttribute("newTask", taskToEdit);
            model.addAttribute("ownerId", taskToEdit.getProject().getId());
            return "task-add-form";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/task/all";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable(name = "id") long taskId) {
        try {
            Task taskToDelete = taskService.getByIdOrThrow(taskId);
            long projectId = taskToDelete.getProject().getId();
            taskService.delete(taskId);
            return "redirect:/project/" + projectId;
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/task/all";
        }
    }

}
