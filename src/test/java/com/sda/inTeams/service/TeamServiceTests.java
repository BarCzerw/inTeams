package com.sda.inTeams.service;

import com.sda.inTeams.TestUtility;
import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.TeamRepository;
import com.sda.inTeams.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("tests")
public class TeamServiceTests {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamService teamService;
    private final UserService userService;

    @Autowired
    public TeamServiceTests(TeamRepository teamRepository, TeamService teamService, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.teamService = new TeamService(teamRepository, userRepository);
        this.userService = new UserService(userRepository, teamRepository);
    }

    @Test
    void testIsWorking() {
    }

    @Nested
    class TeamManagementTest {

        private final List<Team> INITIAL_TEAMS = new ArrayList<>(List.of(
                Team.builder().name("Test Team 001").build(),
                Team.builder().name("Test Team 002").build(),
                Team.builder().name("Test Team 007").build()
        ));

        private final User TEAM_MEMBER = User.builder().firstName("Adam").lastName("Miś").build();
        private final User TEAM_OWNER = User.builder().firstName("Ewa").lastName("Ryś").build();

        private final long INITIAL_TEAMS_SIZE = INITIAL_TEAMS.size();

        @BeforeEach
        void setupTest() {
            TestUtility.clearDatabase(teamRepository);
            TestUtility.assert_databaseSize(teamRepository, 0L);
            TestUtility.addInitialData(teamRepository, INITIAL_TEAMS);
            TestUtility.clearDatabase(userRepository);
            TestUtility.assert_databaseSize(userRepository, 0L);
        }

        @Test
        void canGetAllTeams() {
            Assertions.assertEquals(INITIAL_TEAMS_SIZE, teamService.getAllTeams().size());
        }

        @Test
        void canGetTeamByValidId() {
            assert_gettingTeamById(TestUtility.getValidObjectId(teamRepository), true);
        }

        @Test
        void canGetTeamByValidName() {
            Assertions.assertTrue(teamService.getTeamByName("Test Team 001").isPresent());
        }

        @Test
        void cannotGetTeamByInvalidName() {
            Assertions.assertFalse(teamService.getTeamByName("Team that does not exist").isPresent());
        }

        @Test
        void cannotGetTeamByInvalidId() {
            assert_gettingTeamById(-1L, false);
        }

        @Test
        void canAddValidTeam() {
            try {
                teamService.addTeam(
                        Team.builder()
                                .name("New Test Team 001")
                                .build());
            } catch (InvalidOperation invalidOperation) {
                invalidOperation.printStackTrace();
            }
            TestUtility.assert_databaseSize(teamRepository, INITIAL_TEAMS_SIZE + 1);
        }

        @Test
        void cannotAddInvalidTeam() {
            Assertions.assertThrows(InvalidOperation.class, () -> teamService.addTeam(null));
            TestUtility.assert_databaseSize(teamRepository, INITIAL_TEAMS_SIZE);
        }

        @Test
        void canRemoveValidTeam() {
            try {
                teamService.removeTeam(TestUtility.getValidObjectId(teamRepository));
            } catch (InvalidOperation invalidOperation) {
                invalidOperation.printStackTrace();
            }
            TestUtility.assert_databaseSize(teamRepository, INITIAL_TEAMS_SIZE - 1);
        }

        @Test
        void canRemoveValidTeamWithMember() throws InvalidOperation {
            Team team = teamService.getTeamByName("Test Team 001").orElseThrow();
            User user = userRepository.save(TEAM_MEMBER);
            teamService.addUserToTeam(team.getId(), user.getId());
            teamService.removeTeam(team.getId());
        }

        @Test
        void canRemoveValidTeamWithMemberAndOwner() throws InvalidOperation {
            Team team = teamService.getTeamByName("Test Team 001").orElseThrow();
            User user = userRepository.save(TEAM_MEMBER);
            User owner = userRepository.save(TEAM_OWNER);
            teamService.addUserToTeam(team.getId(), user.getId());
            teamService.addUserToTeam(team.getId(), owner.getId());
            teamService.setOwnerOfTeam(team.getId(), owner.getId());
            teamService.removeTeam(team.getId());
        }

        @Test
        void cannotRemoveInvalidTeam() {
            Assertions.assertThrows(InvalidOperation.class, () -> teamService.removeTeam(-1L));
            TestUtility.assert_databaseSize(teamRepository, INITIAL_TEAMS_SIZE);
        }

        private void assert_gettingTeamById(long id, boolean expectedValue) {
            Optional<Team> teamOptional = teamService.getTeamById(id);
            Assertions.assertEquals(expectedValue, teamOptional.isPresent());
        }

    }

    @Nested
    class TeamMembersManagementTest {
        private final List<User> INITIAL_USERS = TestUtility.getInitialUserList();
        private final long INITIAL_TEAM_SIZE = INITIAL_USERS.size();
        private final Team INITIAL_TEAM = Team.builder().name("Test Team 001").members(new HashSet<>(INITIAL_USERS)).teamOwner(INITIAL_USERS.get(0)).build();
        private final User NEW_USER = User.builder().firstName("Barbara").lastName("Kownacka").build();

        @BeforeEach
        void setupTest() {
            setupInitialUsers();
            setupInitialTeams();
        }

        @AfterEach
        void cleanUp() throws InvalidOperation {
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
        void canAddValidMemberToTeam() {
            User user = userRepository.save(NEW_USER);
            Team team = teamRepository.findByName("Test Team 001").orElseThrow();
            try {
                teamService.addUserToTeam(team.getId(), user.getId());
            } catch (InvalidOperation invalidOperation) {
                invalidOperation.printStackTrace();
            }
            assert_teamMembersCount(INITIAL_TEAM_SIZE + 1);
        }

        @Test
        void cannotAddMemberWhoIsAlreadyInTeam() {
            Team team = teamRepository.findByName("Test Team 001").orElseThrow();
            User user = userRepository.findByFirstNameAndLastName("Jan", "Kowalski").orElseThrow();
            Assertions.assertThrows(InvalidOperation.class, () -> teamService.addUserToTeam(team.getId(), user.getId()));
            assert_teamMembersCount(INITIAL_TEAM_SIZE);
        }

        @Test
        void canRemoveValidMemberFromTeam() throws InvalidOperation {
            Team team = teamRepository.findByName("Test Team 001").orElseThrow();
            User user = userRepository.findByFirstNameAndLastName("Ewa", "Nowak").orElseThrow();
            teamService.removeUserFromTeam(team.getId(), user.getId());
            assert_teamMembersCount(INITIAL_TEAM_SIZE - 1);
        }

        @Test
        void cannotRemoveInvalidMemberFromTeam() throws InvalidOperation {
            Team team = teamRepository.findByName("Test Team 001").orElseThrow();
            Assertions.assertThrows(InvalidOperation.class, () -> teamService.removeUserFromTeam(team.getId(), -1L));
            assert_teamMembersCount(INITIAL_TEAM_SIZE);
        }

        @Test
        void cannotRemoveOwnerFromTeam() throws InvalidOperation {
            Team team = teamRepository.findByName("Test Team 001").orElseThrow();
            User user = userRepository.findByFirstNameAndLastName("Jan", "Kowalski").orElseThrow();
            Assertions.assertThrows(InvalidOperation.class, () -> teamService.removeUserFromTeam(team.getId(), user.getId()));
            assert_teamMembersCount(INITIAL_TEAM_SIZE);
        }

        private void setupInitialUsers() {
            TestUtility.clearDatabase(userRepository);
            TestUtility.assert_databaseSize(userRepository, 0L);
            TestUtility.addInitialData(userRepository, INITIAL_USERS);
        }

        private void setupInitialTeams() {
            TestUtility.clearDatabase(teamRepository);
            TestUtility.assert_databaseSize(teamRepository, 0L);
            TestUtility.addInitialData(teamRepository, List.of(INITIAL_TEAM));
        }

        private void assert_teamMembersCount(long expectedValue) {
            Assertions.assertEquals(expectedValue,teamRepository.findAll().get(0).getMembers().size());
        }

    }

}