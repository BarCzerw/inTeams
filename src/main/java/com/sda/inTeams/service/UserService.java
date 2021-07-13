package com.sda.inTeams.service;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.User.User;
import com.sda.inTeams.repository.TeamRepository;
import com.sda.inTeams.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(long userId) {
        return userRepository.findById(userId);
    }

    public User addUser(User user) throws InvalidOperation {
        if (!Objects.isNull(user)) {
            return saveUserToDatabase(user);
        } else {
            throw new InvalidOperation("Cannot add user - Object is null!");
        }
    }

    public void removeUser(long userId) throws InvalidOperation {
        User user = getUserByIdOrError(userId);
        if (!teamRepository.findAllByTeamOwner(user).isEmpty()) {
            throw new InvalidOperation("Cannot remove user id:" + user.getId() + " - User is still owner of a Team");
        }
        userRepository.delete(user);
    }

    private User getUserByIdOrError(long userId) throws InvalidOperation {
        return userRepository.findById(userId).orElseThrow(() -> new InvalidOperation("User id:" + userId + " not found!"));
    }

    private User saveUserToDatabase(User user) {
        return userRepository.save(user);
    }

}
