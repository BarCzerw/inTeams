package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;
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

    @GetMapping("/all")
    public String getAllTasks(Model model) {
        model.addAttribute("taskList", taskService.getAll());
        return "task-list";
    }

    @GetMapping()
    public String getProjectTasks(Model model, @RequestParam(name = "teamId") long teamId) {
        try {
            model.addAttribute("taskList", taskService.getAllTasksOfTeam(teamId));
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
        }
        return "task-list";
    }

    @GetMapping("/add")
    public String addTaskForm(Model model) {
        model.addAttribute("newTask", new Task());
        return "task-add-form";
    }

    @PostMapping("/add")
    public String addTask(Task task) {
        //long projectId
        try {
            task.setStatus(TaskStatus.NOT_STARTED);
            //task.setProject();
            taskService.add(task);
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
        }
        return "redirect:/task/all";
    }

    @GetMapping("/edit/{id}")
    public String editTask(Model model, @PathVariable(name = "id") long taskId) {
        try {
            model.addAttribute("newTask", taskService.getByIdOrThrow(taskId));
            return "task-add-form";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/task/all";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable(name = "id") long taskId) {
        try {
            taskService.delete(taskId);
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
        }
        return "redirect:/task/all";
    }

}
