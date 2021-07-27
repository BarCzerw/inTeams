package com.sda.inTeams.model.User;


import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Indexable;
import com.sda.inTeams.model.Team.Team;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Indexable, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String username;
    private String nonHashedPassword;
    private String password;

    private String firstName;
    private String lastName;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<AccountRole> roles;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(accountRole -> new SimpleGrantedAuthority(accountRole.getName()))
                .collect(Collectors.toList());
    }

}
