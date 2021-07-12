package com.sda.inTeams.service;

import com.sda.inTeams.TestUtility;
import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Project.ProjectStatus;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.repository.ProjectRepository;
import com.sda.inTeams.repository.TaskRepository;
import com.sda.inTeams.repository.TeamRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("tests")
public class ProjectServiceTests {

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public ProjectServiceTests(ProjectRepository projectRepository, TeamRepository teamRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.taskRepository = taskRepository;
        this.projectService = new ProjectService(projectRepository, teamRepository, taskRepository);
    }

    @Test
    void testIsWorking() {
    }

    @Nested
    class ProjectGettingTest {

        private Team MAIN_TEAM = Team.builder().name("Test Team 001").build();
        private Team SUB_TEAM_ONE = Team.builder().name("Test Team 002").build();
        private Team SUB_TEAM_TWO = Team.builder().name("Test Team 007").build();

        private Project MAIN_PROJECT = Project.builder()
                .name("Test Project 1")
                .projectOwner(MAIN_TEAM)
                .status(ProjectStatus.STARTED)
                .build();

        private Project SUB_PROJECT = Project.builder()
                .name("Test Project 2")
                .projectOwner(SUB_TEAM_ONE)
                .status(ProjectStatus.NOT_STARTED)
                .build();

        private List<Team> INITIAL_TEAMS = List.of(MAIN_TEAM, SUB_TEAM_ONE, SUB_TEAM_TWO);
        private final long INITIAL_TEAM_COUNT = INITIAL_TEAMS.size();
        private List<Project> INITIAL_PROJECTS = List.of(MAIN_PROJECT, SUB_PROJECT);
        private final long INITIAL_PROJECT_COUNT = INITIAL_PROJECTS.size();

        @BeforeEach
        void setup() {
            teamRepository.saveAll(INITIAL_TEAMS);
            projectRepository.saveAll(INITIAL_PROJECTS);
            TestUtility.assert_databaseSize(teamRepository, INITIAL_TEAM_COUNT);
            TestUtility.assert_databaseSize(projectRepository, INITIAL_PROJECT_COUNT);
        }

        @Test
        void canGetAllProjects() {
            Assertions.assertEquals(2, projectService.getAllProjects().size());
        }

        @Test
        void canGetAllProjectsOfTeam() throws InvalidOperation {
            Team mainTeam = teamRepository.findByName("Test Team 001").orElseThrow();
            Assertions.assertEquals(1L, projectService.getAllProjectsOfTeam(mainTeam.getId()).size());
            Team subTeam = teamRepository.findByName("Test Team 007").orElseThrow();
            Assertions.assertEquals(0L, projectService.getAllProjectsOfTeam(subTeam.getId()).size());
        }

        @Test
        void canGetProjectByValidId() throws InvalidOperation {
            Project project = projectRepository.findAll().get(0);
            Project expected = projectService.getProjectByIdOrError(project.getId());
            Assertions.assertEquals(expected, project);
        }

        @Test
        void cannotGetProjectByInvalidId() throws InvalidOperation {
            Assertions.assertThrows(InvalidOperation.class,
                    () -> projectService.getProjectByIdOrError(-1L));
        }

        @AfterEach
        void cleanup() {
            projectRepository.deleteAll();
            teamRepository.deleteAll();
        }

    }

    @Nested
    class ProjectTasksTest {

        @BeforeEach
        void setup() {

        }

        @Test
        void canAddTaskToProject() {

        }

        @Test
        void cannotAddInvalidTaskToProject() {

        }

        @Test
        void cannotAddTaskToInvalidProject() {

        }

        @Test
        void cannotAddTaskIfAlreadyAdded() {

        }

        @Test
        void canRemoveTaskFromProject() {

        }

        @Test
        void cannotRemoveInvalidTaskFromProject() {

        }

        @Test
        void cannotRemoveTaskIfWrongProjectIdProvided() {

        }

        @AfterEach
        void cleanup() {

        }


    }

    @Nested
    class ProjectStatusTest {

        private Project MAIN_PROJECT = Project.builder()
                .name("Test Project 1")
                .status(ProjectStatus.STARTED)
                .build();

        @BeforeEach
        void setup() {
            TestUtility.clearDatabase(projectRepository);
            TestUtility.assert_databaseSize(projectRepository, 0L);
            TestUtility.addInitialData(projectRepository, List.of(MAIN_PROJECT));
        }

        @Test
        void canChangeStatusOfProject() throws InvalidOperation {
            ProjectStatus FINAL_STATUS = ProjectStatus.FINISHED;
            Project project = changeStatusTo(FINAL_STATUS);
            Assertions.assertEquals(FINAL_STATUS, project.getStatus());
        }

        private Project changeStatusTo(ProjectStatus status) throws InvalidOperation {
            return projectService.changeStatus(projectRepository.findAll().get(0).getId(), status);
        }

        @AfterEach
        void cleanUp() {
            projectRepository.deleteAll();
        }

    }


}
