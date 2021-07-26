package com.sda.inTeams.controller;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.TaskRepository;
import com.sda.inTeams.repository.UserRepository;
import com.sda.inTeams.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @GetMapping()
    public String getTaskComments(Model model, @RequestParam(name = "taskId") long taskId) {
        try {
            model.addAttribute("commentList", commentService.getAllByTask(taskId));
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
            model.addAttribute("commentList", commentService.getAll());
        }
        return "comment-list";
    }

    @GetMapping("/add")
    public String addCommentForm(Model model, long taskId, long userId, Principal principal) {
        try {
            if (principal instanceof UsernamePasswordAuthenticationToken) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
                if (usernamePasswordAuthenticationToken.getPrincipal() instanceof User) {
                    User user = (User) usernamePasswordAuthenticationToken.getPrincipal();
                    taskRepository.findById(taskId).orElseThrow(() -> new InvalidOperation("Task not found!"));
                    userRepository.findById(userId).orElseThrow(() -> new InvalidOperation("User not found!"));
                    model.addAttribute("newComment", new Comment());
                    model.addAttribute("ownerId", taskId);
                    model.addAttribute("creatorId", user.getId());
                    return "comment-add-form";
                }
            }
        } catch (InvalidOperation invalidOperation) {
            invalidOperation.printStackTrace();
        }
        return "redirect:/";
    }

    @PostMapping("/add")
    public String addComment(Comment comment, long taskId, long userId) {
        try {
            comment.setCreator(userRepository.findById(userId).orElseThrow(() -> new InvalidOperation("User not found!")));
            comment.setTask(taskRepository.findById(taskId).orElseThrow(() -> new InvalidOperation("Task not found!")));
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
            model.addAttribute("creatorId", commentToEdit.getCreator().getId());
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
