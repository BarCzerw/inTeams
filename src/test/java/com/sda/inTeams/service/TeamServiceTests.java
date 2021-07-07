package com.sda.inTeams.service;

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

        private final String[] TEAM_NAMES = new String[]{
                "Test Team 001",
                "Test Team 002",
                "Test Team 007"
        };

        @BeforeEach
        void setupTest() {
            clearDatabase();
            assert_databaseInitiallyEmpty();
            addInitialTeams();
        }

        @Test
        void canGetAllTeams() {
            assert_allTeamsCount(3);
        }

        @Test
        void canGetTeamByValidId() {
            assert_gettingTeamById(getValidId(),true);
        }

        @Test
        void cannotGetTeamByInvalidId() {
            assert_gettingTeamById(-1L,false);
        }

        @Test
        void canAddValidTeam() {
            String NEW_TEAM_NAME = "New Test Team 001";
            try {
                teamService.addTeam(
                        Team.builder()
                        .name(NEW_TEAM_NAME)
                        .build());
            } catch (InvalidOperation invalidOperation) {
                invalidOperation.printStackTrace();
            }
            assert_allTeamsCount(4);
        }

        @Test
        void cannotAddInvalidTeam() {
            Assertions.assertThrows(InvalidOperation.class, () -> teamService.addTeam(null));
            assert_allTeamsCount(3);
        }

        @Test
        void canRemoveValidTeam() {
            try {
                teamService.removeTeam(getValidId());
            } catch (InvalidOperation invalidOperation) {
                invalidOperation.printStackTrace();
            }
        }

        @Test
        void cannotRemoveInvalidTeam() {
            Assertions.assertThrows(InvalidOperation.class, () -> teamService.removeTeam(-1L));
        }

        private void clearDatabase() {
            teamRepository.deleteAll();
        }

        private void assert_databaseInitiallyEmpty() {
            Assertions.assertEquals(0,teamService.getAllTeams().size());
        }

        private void addInitialTeams() {
            for (String teamName : TEAM_NAMES) {
                teamRepository.save(
                        Team.builder()
                                .name(teamName)
                                .build());
            }
        }

        private long getValidId() {
            return teamRepository.findAll().get(0).getId();
        }

        private void assert_gettingTeamById(long id, boolean expectedValue) {
            Optional<Team> teamOptional = teamService.getTeamById(id);
            Assertions.assertEquals(expectedValue,teamOptional.isPresent());
        }

        private void assert_allTeamsCount(int expected) {
            Assertions.assertEquals(expected,teamService.getAllTeams().size());
        }


    }

}
