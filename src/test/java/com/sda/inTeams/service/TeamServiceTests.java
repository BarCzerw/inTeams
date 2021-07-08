package com.sda.inTeams.service;

import com.sda.inTeams.TestUtility;
import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.TeamRepository;
import com.sda.inTeams.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@SpringBootTest
@ActiveProfiles("tests")
public class TeamServiceTests {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamService teamService;

    @Autowired
    public TeamServiceTests(TeamRepository teamRepository, TeamService teamService, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.teamService = new TeamService(teamRepository);
        this.userRepository = userRepository;
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

        private final long INITIAL_TEAMS_SIZE = INITIAL_TEAMS.size();

        @BeforeEach
        void setupTest() {
            TestUtility.clearDatabase(teamRepository);
            TestUtility.assert_databaseSize(teamRepository, 0L);
            TestUtility.addInitialData(teamRepository, INITIAL_TEAMS);
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
            TestUtility.assert_databaseSize(teamRepository, INITIAL_TEAMS_SIZE+1);
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
    class TeamMembersManagementTest{
        private final List<User> INITIAL_USERS = TestUtility.getInitialUserList();
        private final long INITIAL_TEAM_SIZE = INITIAL_USERS.size();
        private final Team INITIAL_TEAM = Team.builder().name("Test Team 001").members(new HashSet<>(INITIAL_USERS)).teamOwner(INITIAL_USERS.get(0)).build();
        private final User NEW_USER = User.builder().firstName("Barbara").lastName("Kownacka").build();

        @BeforeEach
        void setupTest() {
            setupInitialUsers();
            setupInitialTeams();
        }

        @Test
        void canAddValidMemberToTeam() {
            userRepository.save(NEW_USER);
            try {
                teamService.addUserToTeam(INITIAL_TEAM,NEW_USER);
            } catch (InvalidOperation invalidOperation) {
                invalidOperation.printStackTrace();
            }
            assert_teamMembersCount(INITIAL_TEAM_SIZE+1);
        }

        @Test
        void cannotAddMemberWhoIsAlreadyInTeam() {
            Team team = TestUtility.getValidObject(teamRepository);
            User user = TestUtility.getValidObject(userRepository);
            Assertions.assertThrows(InvalidOperation.class, () -> teamService.addUserToTeam(team, user));
            assert_teamMembersCount(INITIAL_TEAM_SIZE);
        }

        @Test
        void canRemoveValidMemberFromTeam() throws InvalidOperation {
            Team team = TestUtility.getValidObject(teamRepository);
            User user = TestUtility.getValidObject(userRepository);
            teamService.removeUserFromTeam(team, user);
            assert_teamMembersCount(INITIAL_TEAM_SIZE - 1);
        }

        private void setupInitialUsers() {
            TestUtility.clearDatabase(userRepository);
            TestUtility.assert_databaseSize(userRepository,0L);
            TestUtility.addInitialData(userRepository, INITIAL_USERS);
        }

        private void setupInitialTeams() {
            TestUtility.clearDatabase(teamRepository);
            TestUtility.assert_databaseSize(teamRepository,0L);
            TestUtility.addInitialData(teamRepository, List.of(INITIAL_TEAM));
        }

        private void assert_teamMembersCount(long expectedValue) {
            Assertions.assertEquals(expectedValue,teamRepository.findAll().get(0).getMembers().size());
        }

    }

}
