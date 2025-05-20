package com.springboot.MyTodoList.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.MyTodoList.dto.ProjectSummary;
import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.ProjectMember;
import com.springboot.MyTodoList.model.ProjectMemberId;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.ProjectMemberRepository;
import com.springboot.MyTodoList.repository.ProjectRepository;
import com.springboot.MyTodoList.repository.UserRepository;

@Service
public class ProjectMemberService {
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<ProjectMember> findAll() {
        List<ProjectMember> projectMembers = projectMemberRepository.findAll();
        return projectMembers;
    }

    public List<ProjectMember> getProjectMembersByProjectId(int projectId) {
        List<ProjectMember> projectMembers = projectMemberRepository.findById_ProjectId(projectId);
        return projectMembers;
    }

    public List<ProjectSummary> getProjectMembersByUserId(int userId) {
        List<ProjectSummary> projectMembers = projectMemberRepository.findSummariesByUserId(userId);
        return projectMembers;
    }

    public boolean isMember(Integer userId, Integer projectId) {
        return projectMemberRepository.existsByProjectIDAndUserID(projectId, userId);
    }

    public ResponseEntity<ProjectMember> getItemById(int projectId, int userId) {
        ProjectMemberId projectMemberId = new ProjectMemberId(projectId, userId);
        Optional<ProjectMember> projectMemberData = projectMemberRepository.findById(projectMemberId);
        if (projectMemberData.isPresent()) {
            return new ResponseEntity<>(projectMemberData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ProjectMember addProjectMember(ProjectMember projectMember) {
        User user = userRepository.findById(projectMember.getUser_id()).orElseThrow(
                () -> new IllegalArgumentException("User with ID " + projectMember.getUser_id() + " does not exist."));
        Project project = projectRepository.findById(projectMember.getProject_id()).orElseThrow(
                () -> new IllegalArgumentException(
                        "Project with ID " + projectMember.getProject_id() + " does not exist."));

        ProjectMemberId projectMemberId = new ProjectMemberId(projectMember.getProject_id(),
                projectMember.getUser_id());

        ProjectMember pm = new ProjectMember();
        pm.setId(projectMemberId);
        pm.setProject(project);
        pm.setUser(user);
        pm.setRole(projectMember.getRole());
        pm.setJoined_date(OffsetDateTime.now());

        return projectMemberRepository.save(pm);
    }

    public boolean deleteProjectMember(int projectId, int userId) {
        try {
            ProjectMemberId projectMemberId = new ProjectMemberId(projectId, userId);
            projectMemberRepository.deleteById(projectMemberId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}