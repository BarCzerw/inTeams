package com.sda.inTeams.model.User;


import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Team.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    private String username;
    private String password;

    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "teamOwner")
    private Set<Team> teamsOwned;

    @ManyToMany(mappedBy = "members")
    private Set<Team> teams;

    @OneToMany(mappedBy = "creator")
    private Set<Comment> commentsCreated;

}
