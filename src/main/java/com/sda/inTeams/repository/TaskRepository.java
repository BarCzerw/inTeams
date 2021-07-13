package com.sda.inTeams.repository;

import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    List<Task> findAllByProject(Project project);

}
