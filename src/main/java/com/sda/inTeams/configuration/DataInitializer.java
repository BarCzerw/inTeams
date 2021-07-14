package com.sda.inTeams.configuration;

import com.sda.inTeams.model.Comment.Comment;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.repository.CommentRepository;
import com.sda.inTeams.repository.ProjectRepository;
import com.sda.inTeams.repository.TaskRepository;
import com.sda.inTeams.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashSet;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        List<Project> projectList = projectRepository.saveAll(DataUtilities.getRandomProjectList(5));
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
