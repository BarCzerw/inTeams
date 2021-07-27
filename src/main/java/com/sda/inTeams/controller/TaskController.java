package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;
import com.sda.inTeams.service.AuthorizationService;
import com.sda.inTeams.service.CommentService;
import com.sda.inTeams.service.ProjectService;
import com.sda.inTeams.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final CommentService commentService;
    private final AuthorizationService authorizationService;

    @GetMapping("/{id}")
    public String getTask(Model model, Principal principal, @PathVariable(name = "id") long taskId) {
        try {
            Task task = taskService.getByIdOrThrow(taskId);
            if (authorizationService.isUserMemberOfTask(principal, task)) {
                model.addAttribute("taskDetails", task);
                model.addAttribute("taskComments", commentService.getAllByTask(taskId));
                model.addAttribute("newComment", new Comment());
                model.addAttribute("ownerId", taskId);
                model.addAttribute("creatorId", authorizationService.getUserCredentials(principal).get().getId());
                model.addAttribute("isAdmin", authorizationService.isUserAdmin(principal));
                return "task-details";
            } else {
                //unauthorized access
            }
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
        }
        return "redirect:/";
    }

    @GetMapping("/add")
    public String addTaskForm(Model model, @RequestParam(name = "projectId") long projectId) {
        try {
            Project taskOwner = projectService.getByIdOrThrow(projectId);
            model.addAttribute("newTask", new Task());
            model.addAttribute("ownerId", projectId);
            model.addAttribute("statuses", new ArrayList<>(List.of(TaskStatus.values())));
            return "task-add-form";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/project/all";
        }
    }

    @PostMapping("/add")
    public String addTask(Task task, long ownerId) {
        try {
            //task.setStatus(task.getStatus());
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
            model.addAttribute("statuses", new ArrayList<>(List.of(TaskStatus.values())));
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
