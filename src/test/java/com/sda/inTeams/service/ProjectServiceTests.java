package com.sda.inTeams.service;

import com.sda.inTeams.TestUtility;
import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Project.ProjectStatus;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.repository.ProjectRepository;
import com.sda.inTeams.repository.TaskRepository;
import com.sda.inTeams.repository.TeamRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Random;

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
    class ProjectGettingTests {

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
            Assertions.assertEquals(2, projectService.getAll().size());
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
            Project expected = projectService.getByIdOrThrow(project.getId());
            Assertions.assertEquals(expected, project);
        }

        @Test
        void cannotGetProjectByInvalidId() throws InvalidOperation {
            Assertions.assertThrows(InvalidOperation.class,
                    () -> projectService.getByIdOrThrow(-1L));
        }

        @AfterEach
        void cleanup() {
            projectRepository.deleteAll();
            teamRepository.deleteAll();
        }

    }

    @Nested
    class ProjectManagmentTests {

        @BeforeEach
        void setup() {
            TestUtility.clearDatabase(projectRepository);
            TestUtility.assert_databaseSize(projectRepository, 0L);
        }

        @Test
        void canAddNewProject() throws InvalidOperation {
            Project newProject = projectService.add(Project.builder().name("New Project").status(ProjectStatus.STARTED).build());
            TestUtility.assert_databaseSize(projectRepository, 1L);
            Assertions.assertEquals("New Project", newProject.getName());
            Assertions.assertEquals(ProjectStatus.STARTED, newProject.getStatus());
        }

        @Test
        void canRemoveExistingProject() throws InvalidOperation {
            Project newProject = projectService.add(Project.builder().name("New project").status(ProjectStatus.STARTED).build());
            TestUtility.assert_databaseSize(projectRepository, 1L);
            projectService.delete(newProject.getId());
            TestUtility.assert_databaseSize(projectRepository, 0L);
        }

        @Test
        void cannotRemoveProjectIfInvalidId() {
            Assertions.assertThrows(InvalidOperation.class, () -> projectService.delete(-1L));
        }

        @AfterEach
        void cleanup() {
            projectRepository.deleteAll();
        }

    }

    @Nested
    class ProjectTasksTests {

        private final Project INITIAL_PROJECT = Project.builder().name("Test Project 001").status(ProjectStatus.STARTED).build();
        private Project MAIN_PROJECT;
        private long MAIN_PROJECT_ID;

        @BeforeEach
        void setup() {
            TestUtility.clearDatabase(projectRepository);
            TestUtility.clearDatabase(taskRepository);
            TestUtility.assert_databaseSize(projectRepository, 0L);
            TestUtility.assert_databaseSize(taskRepository, 0L);
            MAIN_PROJECT = projectRepository.save(INITIAL_PROJECT);
            MAIN_PROJECT_ID = MAIN_PROJECT.getId();
        }

        @Test
        void canAddTaskToProject() throws InvalidOperation {
            Task newTaskToAdd = addNewTaskToDatabase();
            projectService.addTaskToProject(MAIN_PROJECT.getId(), newTaskToAdd.getId());
            MAIN_PROJECT = projectService.getByIdOrThrow(MAIN_PROJECT_ID);
            newTaskToAdd = taskRepository.getById(newTaskToAdd.getId());
            Assertions.assertTrue(taskRepository.findAllByProject(MAIN_PROJECT).contains(newTaskToAdd));
        }

        @Test
        void cannotAddInvalidTaskToProject() {
            Assertions.assertThrows(InvalidOperation.class, () -> projectService.addTaskToProject(MAIN_PROJECT_ID, -1L));
        }

        @Test
        void cannotAddTaskToInvalidProject() {
            Task newTaskToAdd = addNewTaskToDatabase();
            Assertions.assertThrows(InvalidOperation.class, () -> projectService.addTaskToProject(-1L, newTaskToAdd.getId()));
        }

        @Test
        void cannotAddTaskIfAlreadyAdded() throws InvalidOperation {
            Task newTaskToAdd = addNewTaskToDatabase();
            projectService.addTaskToProject(MAIN_PROJECT_ID, newTaskToAdd.getId());
            MAIN_PROJECT = projectService.getByIdOrThrow(MAIN_PROJECT_ID);
            newTaskToAdd = taskRepository.getById(newTaskToAdd.getId());
            Assertions.assertTrue(taskRepository.findAllByProject(MAIN_PROJECT).contains(newTaskToAdd));
            long TASK_ID = newTaskToAdd.getId();
            Assertions.assertThrows(InvalidOperation.class, () -> projectService.addTaskToProject(MAIN_PROJECT_ID, TASK_ID));
        }

        @Test
        void canRemoveTaskFromProject() throws InvalidOperation {
            Task newTaskToAdd = addNewTaskToDatabase();
            long TASK_ID = newTaskToAdd.getId();
            projectService.addTaskToProject(MAIN_PROJECT_ID, TASK_ID);
            MAIN_PROJECT = projectService.getByIdOrThrow(MAIN_PROJECT_ID);
            newTaskToAdd = taskRepository.getById(TASK_ID);
            Assertions.assertTrue(taskRepository.findAllByProject(MAIN_PROJECT).contains(newTaskToAdd));
            projectService.removeTaskFromProject(MAIN_PROJECT_ID, TASK_ID);
            Assertions.assertFalse(taskRepository.findAllByProject(MAIN_PROJECT).contains(newTaskToAdd));
        }

        @Test
        void cannotRemoveTaskFromProjectIfTaskDoesNotExist() {
            Assertions.assertThrows(InvalidOperation.class, () -> projectService.removeTaskFromProject(MAIN_PROJECT_ID, -1L));
        }

        @Test
        void cannotRemoveTaskFromProjectIfProjectDoesNotContainTask() {
            Task anotherTask = addNewTaskToDatabase();
            Assertions.assertThrows(InvalidOperation.class, () -> projectService.removeTaskFromProject(MAIN_PROJECT_ID, anotherTask.getId()));
        }

        @AfterEach
        void cleanup() {
            projectRepository.deleteAll();
            taskRepository.deleteAll();
        }

        private Task addNewTaskToDatabase() {
            return taskRepository.save(Task.builder().status(TaskStatus.WORK_IN_PROGRESS).description("New desc " + new Random().nextInt(100)).build());
        }

    }

    @Nested
    class ProjectStatusTests {

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
