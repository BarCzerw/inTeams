package com.sda.inTeams.configuration;

import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.CommentRepository;
import com.sda.inTeams.repository.ProjectRepository;
import com.sda.inTeams.repository.TaskRepository;
import com.sda.inTeams.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        Random random = new Random();

        List<User> users = DataUtilities.getRandomUserList(350);
        List<Team> teams = DataUtilities.getRandomTeamList(6);

        for (User user : users) {
            teams.get(random.nextInt(teams.size())).getMembers().add(user);
        }

        for (Team team : teams) {
            List<User> teamUsers = new ArrayList<>(team.getMembers());
            if (!teamUsers.isEmpty()) {
                team.setTeamOwner(teamUsers.get(0));
            }
        }

        for (Team team : teams) {
            List<Project> projectList = projectRepository.saveAll(DataUtilities.getRandomProjectList(5, team));
            for (Project project : projectList) {
                List<Task> taskList = taskRepository.saveAll(DataUtilities.getRandomTaskList(10, project));
                for (Task task : taskList) {
                    List<Comment> commentList = commentRepository.saveAll(DataUtilities.getRandomCommentList(15, task));
                    task.setComments(new HashSet<>(commentList));
                }
                project.setTasks(new HashSet<>(taskList));
            }
            projectRepository.saveAll(projectList);
        }
    }
}
