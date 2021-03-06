package com.sda.inTeams.model.Team;

import com.sda.inTeams.model.Indexable;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.User.User;
import lombok.*;

import javax.persistence.*;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Team implements Indexable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private final String uniqueRegisterId = UUID.randomUUID().toString().replace("-","");

    private String name;

    @ManyToOne()
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User teamOwner;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> members;

    @OneToMany(mappedBy = "projectOwner", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Project> projects;

}
