package com.sda.inTeams.service;

import com.sda.inTeams.TestUtility;
import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.UserRepository;
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
public class UserServiceTests {

    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserServiceTests(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = new UserService(userRepository);
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

        private final long INITIAL_USERS_SIZE = INITIAL_USERS.size();

        @BeforeEach
        void setupTest() {
            TestUtility.clearDatabase(userRepository);
            TestUtility.assert_databaseSize(userRepository, 0L);
            TestUtility.addInitialData(userRepository, INITIAL_USERS);
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
        void canAddValidTeam() {
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
        void canRemoveValidTeam() {
            try {
                userService.removeUser(TestUtility.getValidObjectId(userRepository));
            } catch (InvalidOperation invalidOperation) {
                invalidOperation.printStackTrace();
            }
            TestUtility.assert_databaseSize(userRepository, INITIAL_USERS_SIZE - 1);
        }

        @Test
        void cannotRemoveInvalidTeam() {
            Assertions.assertThrows(InvalidOperation.class, () -> userService.removeUser(-1L));
            TestUtility.assert_databaseSize(userRepository, INITIAL_USERS_SIZE);
        }


        private void assert_gettingUserById(long id, boolean expectedValue) {
            Optional<User> userOptional = userService.getUserById(id);
            Assertions.assertEquals(expectedValue, userOptional.isPresent());
        }

    }

}
