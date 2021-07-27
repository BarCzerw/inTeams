package com.sda.inTeams.service;

import com.sda.inTeams.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository  commentRepository;

    public long getUsersCount() {
        return userRepository.count();
    }

    public long getTeamsCount() {
        return teamRepository.count();
    }

    public long getProjectsCount() {
        return projectRepository.count();
    }

    public long getTasksCount() {
        return taskRepository.count();
    }

    public long getCommentsCount() {
        return commentRepository.count();
    }

    public DatabaseInfo getDatabaseInfo() {
        return DatabaseInfo.builder()
                .usersCount(getUsersCount())
                .teamsCount(getTeamsCount())
                .projectsCount(getProjectsCount())
                .tasksCount(getTasksCount())
                .commentsCount(getCommentsCount())
                .build();
    }

}
