package com.sda.inTeams.model.Task;

import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Indexable;
import com.sda.inTeams.model.Project.Project;
import lombok.*;
import org.hibernate.annotations.Cascade;

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
    long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne
    private Project project;

    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Comment> comments;
}
