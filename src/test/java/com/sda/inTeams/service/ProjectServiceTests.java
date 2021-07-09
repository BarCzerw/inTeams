package com.sda.inTeams.service;

import com.sda.inTeams.TestUtility;
import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Project.ProjectStatus;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.repository.ProjectRepository;
import com.sda.inTeams.repository.TeamRepository;
import com.sda.inTeams.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("tests")
public class ProjectServiceTests {

    private final ProjectRepository projectRepository;
    private final ProjectService projectService;
    private final TeamRepository teamRepository;
    private final TeamService teamService;
    private final UserRepository userRepository;

    @Autowired
    public ProjectServiceTests(ProjectRepository projectRepository, TeamRepository teamRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamService = new TeamService(teamRepository, userRepository, projectRepository);
        this.projectService = new ProjectService(projectRepository,teamService);
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
        private List<Project> INITIAL_PROJECTS = List.of(MAIN_PROJECT,SUB_PROJECT);

        private final long INITIAL_PROJECT_COUNT = INITIAL_PROJECTS.size();
        private final long INITIAL_TEAM_COUNT = INITIAL_TEAMS.size();

        @BeforeEach
        void setup() {
            teamRepository.saveAll(INITIAL_TEAMS);
            projectRepository.saveAll(INITIAL_PROJECTS);
            TestUtility.assert_databaseSize(teamRepository,INITIAL_TEAM_COUNT);
            TestUtility.assert_databaseSize(projectRepository,INITIAL_PROJECT_COUNT);
        }

        @Test
        void canGetAllProjects() {
            Assertions.assertEquals(2,projectService.getAllProjects().size());
        }

        @Test
        void canGetAllProjectsOfTeam() throws InvalidOperation {
            Team mainTeam = teamService.getTeamByName("Test Team 001").orElseThrow();
            Assertions.assertEquals(1L,projectService.getAllProjectsOfTeam(mainTeam.getId()).size());
            Team subTeam = teamService.getTeamByName("Test Team 007").orElseThrow();
            Assertions.assertEquals(0L,projectService.getAllProjectsOfTeam(subTeam.getId()).size());
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
    class ProjectManagmentTest {

        private Team MAIN_TEAM = Team.builder().name("Test Team 001").build();

        private Project MAIN_PROJECT = Project.builder()
                .name("Test Project 1")
                .status(ProjectStatus.STARTED)
                .build();

        private Project SUB_PROJECT = Project.builder()
                .name("Test Project 2")
                .status(ProjectStatus.NOT_STARTED)
                .build();

        private final List<Project> INITIAL_PROJECTS = List.of(MAIN_PROJECT,SUB_PROJECT);

        private final long INITIAL_PROJECT_COUNT = INITIAL_PROJECTS.size();

        @BeforeEach
        void setup() {
            TestUtility.clearDatabase(projectRepository);
            TestUtility.assert_databaseSize(projectRepository,INITIAL_PROJECT_COUNT);
            TestUtility.addInitialData(projectRepository,INITIAL_PROJECTS);
        }

        @Test
        void canAddNewValidProjectToTeam() throws InvalidOperation {
            Team team = teamService.getTeamByName("Test Team 001").orElseThrow();
            Project newProject = projectRepository.save(Project.builder()
                    .name("Test Project 7")
                    .status(ProjectStatus.FINISHED)
                    .build());
            teamService.addProjectToTeam(team.getId(), newProject.getId());
            newProject = projectRepository.findById(newProject.getId()).orElseThrow();
            team = teamService.getTeamByName("Test Team 001").orElseThrow();
            List<Project> teamProject = projectRepository.findAllByProjectOwner(team);
            Assertions.assertEquals(newProject.getProjectOwner(), team);
            Assertions.assertTrue(teamProject.contains(newProject));
        }

        @Test
        void cannotAddInvalidProjectToTeam() throws InvalidOperation {
            throw new InvalidOperation("");

        }

        @Test
        void cannotAddProjectToTeamIfAlreadyAdded() throws InvalidOperation {
            throw new InvalidOperation("");
        }

        @Test
        void canRemoveProjectFromTeam() throws InvalidOperation {
            throw new InvalidOperation("");
        }

        @Test
        void cannotRemoveProjectFromTeamIfNotOwnedByTeam() throws InvalidOperation {
            throw new InvalidOperation("");
        }

        @AfterEach
        void cleanup() {
            projectRepository.deleteAll();
        }
    }

    @Nested
    class ProjectStatusTest {

        @Test
        void canChangeStatusOfProject() throws InvalidOperation {
            throw new InvalidOperation("");
        }

        @Test
        void cannotChangeStatusOfProjectToInvalidStatus() throws InvalidOperation {
            throw new InvalidOperation("");
        }

    }


}
