package com.sda.inTeams;

import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Project.ProjectStatus;
import com.sda.inTeams.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private final ProjectRepository projectRepository;

    private final List<Project> INITIAL_PROJECT_LIST = new ArrayList<>(List.of(
            Project.builder().name("Przykładowy projekt 001").status(ProjectStatus.STARTED).build(),
            Project.builder().name("Przykładowy projekt 002").status(ProjectStatus.FINISHED).build(),
            Project.builder().name("Przykładowy projekt 003").status(ProjectStatus.NOT_STARTED).build(),
            Project.builder().name("Przykładowy projekt 004").status(ProjectStatus.NOT_STARTED).build(),
            Project.builder().name("Przykładowy projekt 005").status(ProjectStatus.STARTED).build()
    ));


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        projectRepository.saveAll(INITIAL_PROJECT_LIST);
    }
}
