package com.sda.inTeams.model.Task;

import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Project.Project;

import java.util.Set;

public class TaskDTO {
    long id;
    private String description;
    private TaskStatus status;
    private Project project;
    private Set<Comment> comments;
}
