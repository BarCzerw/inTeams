package com.sda.inTeams;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Indexable;
import com.sda.inTeams.model.User.User;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Executable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TestUtility {

    public static <T> void clearDatabase(JpaRepository<T,Long> repository) {
        repository.deleteAll();
    }

    public static <T> void addInitialData(JpaRepository<T, Long> repository, Iterable<T> initialDataList) {
        repository.saveAll(initialDataList);
    }

    public static List<User> getInitialUserList() {
        return new ArrayList<>(List.of(
                User.builder().firstName("Jan").lastName("Kowalski").build(),
                User.builder().firstName("Adam").lastName("Mickiewicz").build(),
                User.builder().firstName("Ewa").lastName("Nowak").build()
        ));
    }

    public static <T extends Indexable> T getValidObject(JpaRepository<T, Long> repository) {
        List<T> indexables = repository.findAll();
        return indexables.get(0);
    }

    public static <T extends Indexable> Long getValidObjectId(JpaRepository<T, Long> repository) {
        return getValidObject(repository).getId();
    }

    public static <T> void assert_databaseSize(JpaRepository<T, Long> repository, long expectedSize) {
        Assertions.assertEquals(expectedSize, repository.findAll().size());
    }

}
