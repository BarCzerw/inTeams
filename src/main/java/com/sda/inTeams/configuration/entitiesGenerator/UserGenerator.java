package com.sda.inTeams.configuration.entitiesGenerator;

import com.sda.inTeams.configuration.StringUtilities;
import com.sda.inTeams.model.User.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class UserGenerator {

    private static final Random random = new Random();

    public static List<User> generateUsers(int size) {
        List<User> users = new ArrayList<>();
        IntStream.range(0, size).forEach(ind -> users.add(generateSingleUser()));
        return users;
    }

    public static User generateSingleUser() {
        return User.builder()
                .firstName("Name" + StringUtilities.getRandomNumberAsString(4))
                .lastName("Last" + StringUtilities.getRandomNumberAsString(4))
                .build();
    }

    public static User pickRandomUserFromList(List<User> users) {
        return users.get(random.nextInt(users.size()));
    }
}
