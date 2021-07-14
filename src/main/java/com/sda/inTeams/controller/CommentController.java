package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;
import com.sda.inTeams.service.CommentService;
import com.sda.inTeams.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final TaskService taskService;

    @GetMapping("/all")
    public String getAllComments(Model model) {
        model.addAttribute("commentList", commentService.getAll());
        return "comment-list";
    }

    @GetMapping()
    public String getTaskComments(Model model, @RequestParam(name = "tastaskkId") long taskId) {
        try {
            model.addAttribute("commentList", commentService.getAllByTask(taskId));
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            model.addAttribute("commentList", commentService.getAll());
        }
        return "comment-list";
    }

    @GetMapping("/add")
    public String addCommentForm(Model model, long taskId) {
        try {
            taskService.getByIdOrThrow(taskId);
            model.addAttribute("newComment", new Comment());
            model.addAttribute("ownerId", taskId);
            return "comment-add-form";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/task/all";
        }
    }

    @PostMapping("/add")
    public String addComment(Comment comment, long taskId) {
        //long creatorId
        try {
            //comment.setCreator();
            comment.setTask(taskService.getByIdOrThrow(taskId));
            commentService.add(comment);
            return "redirect:/task/" + taskId;
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/comment/all";
        }
    }

    @GetMapping("/edit/{id}")
    public String editComment(Model model, @PathVariable(name = "id") long commentId) {
        try {
            Comment commentToEdit = commentService.getByIdOrThrow(commentId);
            model.addAttribute("newComment", commentToEdit);
            model.addAttribute("ownerId", commentToEdit.getTask().getId());
            return "comment-add-form";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/comment/all";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteComment(@PathVariable(name = "id") long commentId) {
        try {
            Comment comment = commentService.getByIdOrThrow(commentId);
            long taskId = comment.getTask().getId();
            commentService.delete(commentId);
            return "redirect:/task/" + taskId;
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/task/all";
        }
    }

}
