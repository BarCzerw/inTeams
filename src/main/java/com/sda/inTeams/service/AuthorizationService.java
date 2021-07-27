package com.sda.inTeams.service;

import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.AccountRole;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorizationService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final AccountRoleRepository accountRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> accountOptional = userRepository.findByUsername(username);
        if (accountOptional.isPresent()) {
            return accountOptional.get();
        }
        throw new  UsernameNotFoundException("Cannot find username:" + username);
    }

    public Optional<User> getUserCredentials(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) principal;
            if (usernamePasswordAuthenticationToken.getPrincipal() instanceof User) {
                return  Optional.of((User) usernamePasswordAuthenticationToken.getPrincipal());
            }
        }
        return Optional.empty();
    }

    public boolean isUserAdmin(Principal principal) {
        Optional<User> userOptional = getUserCredentials(principal);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            AccountRole accountRole = accountRoleRepository.findByName("ROLE_ADMIN").get();
            return user.getRoles().contains(accountRole);
        }
        return false;
    }

    public boolean isUserEligibleToDeleteComment(Principal principal, Comment comment) {
        return isUserCommentCreator(principal,comment) || isUserAdmin(principal);
    }

    public boolean isUserEligibleToEditComment(Principal principal, Comment comment) {
        return isUserCommentCreator(principal,comment) || isUserAdmin(principal);
    }

    private boolean isUserCommentCreator(Principal principal, Comment comment) {
        return getUserCredentials(principal).orElseThrow().equals(comment.getCreator());
    }

    public boolean isUserMemberOfTask(Principal principal, Task task) {
        User user = getUserCredentials(principal).orElseThrow();
        Project project = projectRepository.findByTasksContaining(task).orElseThrow();
        Team team = teamRepository.findByProjectsContaining(project).orElseThrow();
        return userRepository.findAllByTeamsContaining(team).contains(user);
    }
}
