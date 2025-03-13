package com.springboot.MyTodoList.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.repository.ProjectRepository;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

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

    public Project addProject(Project project) {
        return projectRepository.save(project);
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
            existingProject.setCreated_at(pr.getCreated_at());
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
