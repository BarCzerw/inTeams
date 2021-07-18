package com.sda.inTeams.model.User;


import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Indexable;
import com.sda.inTeams.model.Team.Team;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Indexable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    private String username;
    private String password;

    private String firstName;
    private String lastName;

    @OneToMany(mappedBy = "teamOwner", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Team> teamsOwned;

    @ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Team> teams;

    @OneToMany(mappedBy = "creator")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Comment> commentsCreated;

}
