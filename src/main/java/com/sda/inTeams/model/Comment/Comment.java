package com.sda.inTeams.model.Comment;

import com.sda.inTeams.model.Indexable;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.User.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Indexable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    private User creator;

    @Enumerated(EnumType.STRING)
    private CommentType type;

    @ManyToOne(fetch = FetchType.EAGER)
    private Task task;

}
