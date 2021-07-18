package com.sda.inTeams.model.Comment;

import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.User.User;

public class CommentDTO {
    long id;
    private String text;
    private User creator;
    private Task task;
}
