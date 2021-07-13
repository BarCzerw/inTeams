package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;
import com.sda.inTeams.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/all")
    public String getAllComments(Model model) {
        model.addAttribute("commentList", commentService.getAll());
        return "comment-list";
    }

    @GetMapping()
    public String getTaskComments(Model model, @RequestParam(name = "taskId") long taskId) {
        try {
            model.addAttribute("commentList", commentService.getAllByTask(taskId));
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
        }
        return "comment-list";
    }

    @GetMapping("/add")
    public String addCommentForm(Model model) {
        model.addAttribute("newComment", new Comment());
        return "comment-add-form";
    }

    @PostMapping("/add")
    public String addComment(Comment comment) {
        //long taskId
        //long creatorId
        try {
            //comment.setCreator();
            //comment.setTask();
            commentService.add(comment);
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
        }
        return "redirect:/comment/all";
    }

    @GetMapping("/edit/{id}")
    public String editComment(Model model, @PathVariable(name = "id") long commentId) {
        try {
            model.addAttribute("newComment", commentService.getByIdOrThrow(commentId));
            return "comment-add-form";
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            return "redirect:/comment/all";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteComment(@PathVariable(name = "id") long commentId) {
        try {
            commentService.delete(commentId);
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
        }
        return "redirect:/comment/all";
    }

}
