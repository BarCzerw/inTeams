package com.sda.inTeams.model.Team;

import com.sda.inTeams.model.Indexable;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.User.User;
import lombok.*;

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
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User teamOwner;

    @ManyToMany(fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> members;

    @OneToMany(mappedBy = "projectOwner", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Project> projects;

}
