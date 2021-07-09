package com.sda.inTeams.service;

import com.sda.inTeams.TestUtility;
import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.ProjectRepository;
import com.sda.inTeams.repository.TeamRepository;
import com.sda.inTeams.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("tests")
public class UserServiceTests {

    private final TeamRepository teamRepository;
    private final TeamService teamService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ProjectRepository projectRepository;

    @Autowired
    public UserServiceTests(UserRepository userRepository, TeamRepository teamRepository, ProjectRepository projectRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.teamService = new TeamService(teamRepository, userRepository,projectRepository);
        this.userService = new UserService(userRepository,teamRepository);
    }

    @Test
    void testIsWorking() {
    }

    @Nested
    class UserManagementTest {

        private final List<User> INITIAL_USERS = new ArrayList<>(List.of(
                User.builder().firstName("Jan").lastName("Kowalski").build(),
                User.builder().firstName("Adam").lastName("Mickiewicz").build(),
                User.builder().firstName("Ewa").lastName("Nowak").build()
        ));

        private final Team INITIAL_TEAM = Team.builder().name("Test Team 001").build();

        private final long INITIAL_USERS_SIZE = INITIAL_USERS.size();

        @BeforeEach
        void setupTest() {
            TestUtility.clearDatabase(userRepository);
            TestUtility.assert_databaseSize(userRepository, 0L);
            TestUtility.addInitialData(userRepository, INITIAL_USERS);
            TestUtility.clearDatabase(teamRepository);
            TestUtility.assert_databaseSize(teamRepository, 0L);
        }

        @AfterEach
        void cleanup() throws InvalidOperation {
            for (Team team : teamService.getAllTeams()) {
                teamService.removeTeam(team.getId());
            }
            teamRepository.flush();
            for (User user : userService.getAllUsers()) {
                userService.removeUser(user.getId());
            }
            userRepository.flush();
        }

        @Test
        void canGetAllUsers() {
            Assertions.assertEquals(INITIAL_USERS_SIZE, userService.getAllUsers().size());
        }

        @Test
        void canGetUserByValidId() {
            assert_gettingUserById(TestUtility.getValidObjectId(userRepository), true);
        }

        @Test
        void cannotGetUserByValidId() {
            assert_gettingUserById(-1L, false);
        }

        @Test
        void canAddValidUser() {
            try {
                userService.addUser(
                        User.builder()
                                .firstName("Bartłomiej")
                                .lastName("Adamowicz")
                                .build()
                );
            } catch (InvalidOperation invalidOperation) {
                invalidOperation.printStackTrace();
            }
            TestUtility.assert_databaseSize(userRepository, INITIAL_USERS_SIZE + 1);
        }

        @Test
        void cannotAddInvalidUser() {
            Assertions.assertThrows(InvalidOperation.class, () -> userService.addUser(null));
        }

        @Test
        void canRemoveValidUserNotAnOwnerOfTeam() {
            teamRepository.save(INITIAL_TEAM);
            try {
                userService.removeUser(userRepository.findByFirstNameAndLastName("Adam", "Mickiewicz").orElseThrow().getId());
            } catch (InvalidOperation invalidOperation) {
                invalidOperation.printStackTrace();
            }
            TestUtility.assert_databaseSize(userRepository, INITIAL_USERS_SIZE - 1);
        }

        @Test
        void cannotRemoveOwnerOfTeam() {
            User user = userRepository.findByFirstNameAndLastName("Jan", "Kowalski").orElseThrow();
            INITIAL_TEAM.setTeamOwner(user);
            teamRepository.save(INITIAL_TEAM);
            Assertions.assertThrows(InvalidOperation.class, () -> userService.removeUser(user.getId()));
            TestUtility.assert_databaseSize(userRepository, INITIAL_USERS_SIZE);
        }

        @Test
        void cannotRemoveInvalidUser() {
            Assertions.assertThrows(InvalidOperation.class, () -> userService.removeUser(-1L));
            TestUtility.assert_databaseSize(userRepository, INITIAL_USERS_SIZE);
        }


        private void assert_gettingUserById(long id, boolean expectedValue) {
            Optional<User> userOptional = userService.getUserById(id);
            Assertions.assertEquals(expectedValue, userOptional.isPresent());
        }

    }

}
