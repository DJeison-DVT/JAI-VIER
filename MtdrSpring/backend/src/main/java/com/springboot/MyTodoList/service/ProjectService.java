package com.springboot.MyTodoList.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.ProjectMember;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.ProjectMemberRepository;
import com.springboot.MyTodoList.repository.ProjectRepository;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;

    public List<Project> findAll() {
        List<Project> projects = projectRepository.findAll();
        return projects;
    }

    public ResponseEntity<Project> getItemById(int id) {
        Optional<Project> projectData = projectRepository.findById(id);
        if (projectData.isPresent()) {
            return new ResponseEntity<>(projectData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public List<Project> getProjectByUserID(Integer userId) {
        return projectRepository.findDistinctByMemberships_User_ID(userId);
    }

    public Project addProject(Project project, User user) {
        project.setCreated_at(OffsetDateTime.now());
        project.setUpdated_at(OffsetDateTime.now());
        Project saved = projectRepository.save(project);

        ProjectMember member = new ProjectMember();
        member.setUser(user);
        member.setProject(saved);
        member.setUser_id(user.getID());
        member.setProject_id(saved.getID());
        member.setJoined_date(OffsetDateTime.now());
        member.setRole("manager");
        projectMemberRepository.save(member);

        return saved;
    }

    public boolean deleteProject(int id) {
        try {
            projectRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Project updateProject(int id, Project pr) {
        Optional<Project> projectData = projectRepository.findById(id);
        if (projectData.isPresent()) {
            Project existingProject = projectData.get();
            existingProject.setName(pr.getName());
            existingProject.setDescription(pr.getDescription());
            existingProject.setUpdated_at(OffsetDateTime.now());
            existingProject.setStatus(pr.getStatus());
            existingProject.setEnd_date(pr.getEnd_date());
            existingProject.setStart_date(pr.getStart_date());
            return projectRepository.save(existingProject);
        } else {
            return null;
        }
    }
}
