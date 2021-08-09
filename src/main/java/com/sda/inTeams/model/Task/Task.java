package com.sda.inTeams.model.Task;

import com.sda.inTeams.model.Comment.Comment;
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
public class Task implements Indexable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne
    private Project project;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private User userResponsible;

    @OneToMany(mappedBy = "task")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Comment> comments;
}
