package com.springboot.MyTodoList.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;

import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.repository.ProjectRepository;
import com.springboot.MyTodoList.repository.SprintRepository;

@Service
public class SprintService {

    @Autowired
    private SprintRepository sprintRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<Sprint> findAll() {
        List<Sprint> sprints = sprintRepository.findAll();
        for (Sprint sprint : sprints) {
            if (sprint.getProject() != null) {
                sprint.setProject_id(sprint.getProject().getID());
            }
        }
        return sprints;
    }

    public List<Sprint> findByProject_ID(int project_id) {
        List<Sprint> sprints = sprintRepository.findByProject_ID(project_id);
        for (Sprint sprint : sprints) {
            if (sprint.getProject() != null) {
                sprint.setProject_id(sprint.getProject().getID());
            }
        }
        return sprints;
    }

    public ResponseEntity<Sprint> getItemById(int id) {
        Optional<Sprint> sprintData = sprintRepository.findById(id);
        if (sprintData.isPresent()) {
            Sprint sprint = sprintData.get();
            if (sprint.getProject() != null) {
                sprint.setProject_id(sprint.getProject().getID());
            }
            return new ResponseEntity<>(sprint, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public List<Sprint> findActiveSprintsByProjectId(int projectId) {
        List<Sprint> sprints = findActiveSprintsByProjectId(projectId, OffsetDateTime.now());
        for (Sprint sprint : sprints) {
            if (sprint.getProject() != null) {
                sprint.setProject_id(sprint.getProject().getID());
            }
        }
        return sprints;
    }

    public List<Sprint> findActiveSprintsByProjectId(int projectId, OffsetDateTime now) {
        return sprintRepository.findActiveSprintsByProjectId(projectId, now);
    }

    public Sprint addSprint(Sprint sprint) {
        if (sprint.getProject_id() == 0) {
            throw new IllegalArgumentException("Sprint must be linked to an existing Project.");
        }

        Project existingProject = projectRepository.findById(sprint.getProject_id()).orElseThrow(
                () -> new IllegalArgumentException("Project not found with ID: " + sprint.getProject_id()));

        sprint.setProject(existingProject);
        sprint.setCreated_at(OffsetDateTime.now());
        sprint.setUpdated_at(OffsetDateTime.now());

        return sprintRepository.save(sprint);
    }

    public boolean deleteSprint(int id) {
        try {
            sprintRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Sprint updateSprint(int id, Sprint sp) {
        if (sp.getProject_id() == 0) {
            throw new IllegalArgumentException("Sprint must be linked to an existing Project.");
        }

        Project existingProject = projectRepository.findById(sp.getProject_id()).orElseThrow(
                () -> new IllegalArgumentException("Project not found with ID: " + sp.getProject_id()));

        Optional<Sprint> existingSprintData = sprintRepository.findById(id);
        if (existingSprintData.isPresent()) {
            Sprint sprint = existingSprintData.get();
            sprint.setProject(existingProject);
            sprint.setUpdated_at(OffsetDateTime.now());
            sprint.setName(sp.getName());
            sprint.setStart_date(sp.getStart_date());
            sprint.setEnd_date(sp.getEnd_date());
            sprint.setStatus(sp.getStatus());
            sprint.setDescription(sp.getDescription());
            return sprintRepository.save(sprint);
        } else {
            return null;
        }
    }
}
