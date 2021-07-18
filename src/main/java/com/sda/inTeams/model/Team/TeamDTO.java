package com.sda.inTeams.model.Team;

import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.User.User;

import java.util.Set;

public class TeamDTO {
    long id;
    private String name;
    private User teamOwner;
    private Set<User> members;
    private Set<Project> projects;
}
