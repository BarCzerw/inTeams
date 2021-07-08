package com.sda.inTeams;

import com.sda.inTeams.exception.InvalidOperation;
import com.sda.inTeams.model.Indexable;
import org.junit.jupiter.api.Assertions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Executable;
import java.util.List;
import java.util.function.Consumer;

public class TestUtility {

    public static <T> void clearDatabase(JpaRepository<T,Long> repository) {
        repository.deleteAll();
    }

    public static <T> void addInitialData(JpaRepository<T, Long> repository, Iterable<T> initialDataList) {
        repository.saveAll(initialDataList);
    }

    public static <T extends Indexable> Long getValidObjectId(JpaRepository<T, Long> repository) {
        List<T> indexables = repository.findAll();
        return indexables.get(0).getId();
    }

    public static <T> void assert_databaseSize(JpaRepository<T, Long> repository, long expectedSize) {
        Assertions.assertEquals(expectedSize, repository.findAll().size());
    }

}
