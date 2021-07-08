package com.sda.inTeams.model.Team;

import com.sda.inTeams.model.Indexable;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.User.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Team implements Indexable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    private String name;

    @ManyToOne()
    private User teamOwner;

    @ManyToMany()
    @JoinTable(
            name = "teams_users",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members;

    @OneToMany(mappedBy = "projectOwner")
    private Set<Project> projects;

}
