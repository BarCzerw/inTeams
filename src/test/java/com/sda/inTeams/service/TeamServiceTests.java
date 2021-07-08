package com.sda.inTeams.service;

import com.sda.inTeams.TestUtility;
import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.repository.TeamRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("tests")
public class TeamServiceTests {

    private final TeamRepository teamRepository;
    private final TeamService teamService;

    @Autowired
    public TeamServiceTests(TeamRepository teamRepository, TeamService teamService) {
        this.teamRepository = teamRepository;
        this.teamService = new TeamService(teamRepository);
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

}
