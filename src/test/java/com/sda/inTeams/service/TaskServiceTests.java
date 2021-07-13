package com.sda.inTeams.service;

import com.sda.inTeams.TestUtility;
import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Task.Task;
import com.sda.inTeams.model.Task.TaskStatus;
import com.sda.inTeams.repository.ProjectRepository;
import com.sda.inTeams.repository.TaskRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
@ActiveProfiles("tests")
public class TaskServiceTests {

    private final TaskService taskService;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public TaskServiceTests(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.taskService = new TaskService(taskRepository, projectRepository);
    }

    private Task addNewTaskToDatabase() {
        return taskRepository.save(Task.builder().status(TaskStatus.WORK_IN_PROGRESS).description("New desc " + new Random().nextInt(100)).build());
    }

    @Nested
    class TaskManagmentTests {

        List<Task> INITIAL_DATA = new ArrayList<>(List.of(
                Task.builder().status(TaskStatus.NOT_STARTED).description("Desc 01").build(),
                Task.builder().status(TaskStatus.NOT_STARTED).description("Desc 02").build(),
                Task.builder().status(TaskStatus.WORK_IN_PROGRESS).description("Desc 03").build(),
                Task.builder().status(TaskStatus.WORK_IN_PROGRESS).description("Desc 04").build(),
                Task.builder().status(TaskStatus.WORK_IN_PROGRESS).description("Desc 05").build(),
                Task.builder().status(TaskStatus.WORK_IN_PROGRESS).description("Desc 06").build(),
                Task.builder().status(TaskStatus.PENDING_VERIFICATION).description("Desc 07").build(),
                Task.builder().status(TaskStatus.PENDING_VERIFICATION).description("Desc 08").build(),
                Task.builder().status(TaskStatus.PENDING_VERIFICATION).description("Desc 09").build(),
                Task.builder().status(TaskStatus.FINISHED).description("Desc 10").build(),
                Task.builder().status(TaskStatus.FINISHED).description("Desc 11").build(),
                Task.builder().status(TaskStatus.FINISHED).description("Desc 12").build(),
                Task.builder().status(TaskStatus.FINISHED).description("Desc 13").build(),
                Task.builder().status(TaskStatus.FINISHED).description("Desc 14").build(),
                Task.builder().status(TaskStatus.FINISHED).description("Desc 15").build(),
                Task.builder().status(TaskStatus.FINISHED).description("Desc 16").build(),
                Task.builder().status(TaskStatus.FINISHED).description("Desc 17").build()
        ));

        private final long INITIAL_DATA_SIZE = INITIAL_DATA.size();

        @BeforeEach
        void setUp() {
            TestUtility.clearDatabase(taskRepository);
            TestUtility.assert_databaseSize(taskRepository, 0L);
            TestUtility.addInitialData(taskRepository, INITIAL_DATA);
        }

        @Test
        void canGetListOfAllTasks() {
            Assertions.assertEquals(INITIAL_DATA_SIZE, taskService.getAllTasks().size());
        }

        @Test
        void canGetTaskOfValidId() throws InvalidOperation {
            Task newTask = taskRepository.save(Task.builder().build());
            Assertions.assertEquals(newTask, taskService.getTaskByIdOrError(newTask.getId()));
        }

        @Test
        void cannotGetTaskOfInvalidId() {
            Assertions.assertThrows(InvalidOperation.class, () -> taskService.getTaskByIdOrError(-1L));
        }

        @Test
        void canAddNewTask() throws InvalidOperation {
            taskService.addTask(addNewTaskToDatabase());
            TestUtility.assert_databaseSize(taskRepository, INITIAL_DATA_SIZE + 1);
        }

        @Test
        void canDeleteTaskByValidId() throws InvalidOperation {
            taskService.deleteTask(taskService.getAllTasks().get(0).getId());
            Assertions.assertEquals(INITIAL_DATA_SIZE - 1, taskRepository.findAll().size());
        }

        @Test
        void cannotDeleteTaskByInvalidId() {
            Assertions.assertThrows(InvalidOperation.class, () -> taskService.deleteTask(-1L));
        }

        @AfterEach
        void cleanup() {
            projectRepository.deleteAll();
        }

    }

    @Nested
    class TaskStatusTests {

        Task TASK = Task.builder()
                .id(99)
                .status(TaskStatus.NOT_STARTED)
                .description("Desc 99")
                .build();

        long VALID_ID;

        @BeforeEach
        void setUp() {
            TestUtility.clearDatabase(taskRepository);
            TestUtility.assert_databaseSize(taskRepository, 0L);
            VALID_ID = taskRepository.save(TASK).getId();
        }

        @Test
        void canChangeTaskStatusToFinished() throws InvalidOperation {
            TaskStatus FINAL_STATUS = TaskStatus.FINISHED;
            taskService.changeTaskStatus(VALID_ID, FINAL_STATUS);
            Assertions.assertEquals(FINAL_STATUS, taskRepository.findById(VALID_ID).get().getStatus());
        }

        @AfterEach
        void cleanup() {
            projectRepository.deleteAll();
        }

    }

}
