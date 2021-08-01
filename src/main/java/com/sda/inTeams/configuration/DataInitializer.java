package com.sda.inTeams.configuration;

import com.sda.inTeams.configuration.entitiesGenerator.*;
import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.AccountRole;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String[] AVAILABLE_ROLES = {ROLE_ADMIN, ROLE_USER};

    private final PasswordEncoder passwordEncoder;
    private final UserRepository accountRepository;
    private final AccountRoleRepository accountRoleRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        for (String availableRole : AVAILABLE_ROLES) {
            addRole(availableRole);
        }
        addUser("admin","admin",new String[]{ROLE_ADMIN});
        addUser("user","user", new String[]{ROLE_USER});
        generateInitialDatabase();
        generateComments();
    }

    private void generateInitialDatabase() {
        List<Team> teams = TeamGenerator.generateTeams(6);
        teamRepository.saveAll(teams);

        for (Team team : teams) {
            List<User> users = UserGenerator.generateUsers(8);
            connectTeamAndUsers(team, users);

            List<Project> projects = ProjectGenerator.generateProjects(4);
            connectTeamAndProjects(team, projects);

            for (Project project : projects) {
                List<Task> tasks = TaskGenerator.generateTasks(6);
                connectProjectAndTasks(project, tasks);
                for (Task task : tasks) {
                    List<Comment> comments = CommentGenerator.generateComments(3);
                    connectTaskAndComments(task, comments);
                    connectTaskAndUser(task,users);
                }
                //taskRepository.saveAll(tasks);
                //userRepository.saveAll(users);
            }
        }
        //teamRepository.saveAll(teams);
    }

    private void generateComments() {
        List<Team> teams = teamRepository.findAll();
        for (Team team : teams) {
            List<User> users = userRepository.findAllByTeamsContaining(team);

            List<Project> projects = projectRepository.findAllByProjectOwner(team);

            for (Project project : projects) {
                List<Task> tasks = taskRepository.findAllByProject(project);
                for (Task task : tasks) {
                    List<Comment> comments = commentRepository.findAllByTask(task);
                    for (Comment comment : comments) {
                        users = userRepository.findAllByTeamsContaining(team);
                        connectCommentAndUser(comment, users);
                    }
                    //commentRepository.saveAll(comments);
                    //userRepository.saveAll(users);
                }
            }
        }
    }

    private void connectTeamAndUsers(Team team, List<User> userList) {
        team.setMembers(new HashSet<>(userList));
        team.setTeamOwner(UserGenerator.pickRandomUserFromList(userList));
        teamRepository.save(team);
    }

    private void connectTeamAndProjects(Team team, List<Project> teamProjects) {
        //team.setProjects(new HashSet<>(teamProjects));
        for (Project teamProject : teamProjects) {
            teamProject.setProjectOwner(team);
        }
        projectRepository.saveAll(teamProjects);
    }

    private void connectProjectAndTasks(Project project, List<Task> taskList) {
        //project.setTasks(new HashSet<>(taskList));
        for (Task projectTask : taskList) {
            projectTask.setProject(project);
        }
        taskRepository.saveAll(taskList);
    }

    private void connectTaskAndComments(Task task, List<Comment> commentList) {
        //task.setComments(new HashSet<>(commentList));
        for (Comment comment : commentList) {
            comment.setTask(task);
        }
        commentRepository.saveAll(commentList);
    }

    private void connectCommentAndUser(Comment comment, List<User> teamUsers) {
        User user = UserGenerator.pickRandomUserFromList(teamUsers);
        comment.setCreator(user);
        List<Comment> userComments = commentRepository.findAllByCreator(user);
        userComments.add(comment);
        user.setCommentsCreated(new HashSet<>(userComments));
        commentRepository.save(comment);
    }

    private void connectTaskAndUser(Task task, List<User> teamUsers) {
        User user = UserGenerator.pickRandomUserFromList(teamUsers);
        task.setUserResponsible(user);
        //List<Task> userTasks = taskRepository.findAllByUserResponsible(user);
        //userTasks.add(task);
        //user.setTaskResponsibleFor(new HashSet<>(userTasks));
        taskRepository.save(task);
    }

    private void addUser(String username, String password, String[] roles) {
        Optional<User> optionalAccount = accountRepository.findByUsername(username);
        if (!optionalAccount.isPresent()) {
            User account = User.builder()
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .enabled(true)
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .build();

            Set<AccountRole> roleSet = new HashSet<>();
            for (String role : roles) {
                Optional<AccountRole> roleOptional = accountRoleRepository.findByName(role);
                if (roleOptional.isPresent()) {
                    roleSet.add(roleOptional.get());
                }
            }
            account.setRoles(roleSet);
            accountRepository.save(account);
        }
    }

    private void addRole(String availableRole) {
        Optional<AccountRole> optionalAccountRole = accountRoleRepository.findByName(availableRole);
        if (!optionalAccountRole.isPresent()) {
            AccountRole accountRole = AccountRole.builder()
                    .name(availableRole)
                    .build();

            accountRoleRepository.save(accountRole);
        }
    }

}
