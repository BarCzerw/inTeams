package com.sda.inTeams.model.User;

import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Team.Team;

import java.util.Set;

public class UserDTO {
    long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Set<Team> teamsOwned;
    private Set<Team> teams;
    private Set<Comment> commentsCreated;
}
