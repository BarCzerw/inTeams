package com.sda.inTeams.repository;

import com.sda.inTeams.model.Project.Project;
import com.sda.inTeams.model.Team.Team;
import com.sda.inTeams.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByMembersContaining(User member);
    Optional<Team> findByName(String name);
    List<Team> findAllByTeamOwner(User user);
    List<Team> findAllByProjectsContaining(Project project);
}
