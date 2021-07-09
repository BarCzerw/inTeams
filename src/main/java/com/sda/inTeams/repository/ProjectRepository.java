package com.sda.inTeams.repository;

import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByProjectOwner(Team team);
}
