package com.sda.inTeams.model.Project;

import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Team.Team;

import java.util.Set;

public class ProjectDTO {
    long id;
    private String name;
    private Team projectOwner;
    private ProjectStatus status;
    private Set<Task> tasks;
}
