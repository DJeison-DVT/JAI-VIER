package com.springboot.MyTodoList.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.springboot.MyTodoList.model.ProjectMember;
import com.springboot.MyTodoList.repository.ProjectMemberRepository;

@Service
public class ProjectMemberService {
    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    public List<ProjectMember> findAll() {
        List<ProjectMember> projectMembers = projectMemberRepository.findAll();
        return projectMembers;
    }

    public List<ProjectMember> getProjectMembersByProjectId(int projectId) {
        List<ProjectMember> projectMembers = projectMemberRepository.findByProjectId(projectId);
        return projectMembers;
    }

    public List<ProjectMember> getProjectMembersByUserId(int userId) {
        List<ProjectMember> projectMembers = projectMemberRepository.findByUserId(userId);
        return projectMembers;
    }

    public ResponseEntity<ProjectMember> getItemById(int projectId, int userId) {
        Optional<ProjectMember> projectMemberData = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
        if (projectMemberData.isPresent()) {
            return new ResponseEntity<>(projectMemberData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ProjectMember addProjectMember(ProjectMember projectMember) {
        projectMember.setJoined_date(OffsetDateTime.now());
        return projectMemberRepository.save(projectMember);
    }

    public boolean deleteProjectMember(int projectId, int userId) {
        try {
            projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}