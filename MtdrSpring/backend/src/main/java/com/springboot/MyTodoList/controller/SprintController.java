package com.springboot.MyTodoList.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.security.AuthContext;
import com.springboot.MyTodoList.service.ProjectService;
import com.springboot.MyTodoList.service.SprintService;

import io.swagger.models.Response;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class SprintController {

    @Autowired
    private SprintService sprintService;
    @Autowired
    private AuthContext authContext;
    @Autowired
    private ProjectService projectService;

    @CrossOrigin
    @GetMapping(value = "/sprintlist")
    public List<Sprint> getAllSprints(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "project_id", required = false) Integer projectId,
            @RequestParam(name = "admin", required = false, defaultValue = "false") boolean isAdmin) {

        User user = authContext.getCurrentUser(authHeader);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        if (isAdmin) {
            if (authContext.isAdmin(user)) {
                return sprintService.findAll();
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not an admin");
        }

        Project selectedProject = user.getSelectedProject();
        if (selectedProject != null) {
            if (authContext.isMember(user.getID(), selectedProject.getID())) {
                return sprintService.findByProject_ID(selectedProject.getID());
            }
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Not a member of project " + selectedProject.getID());

        }

        return sprintService.findByUser_ID(user.getID());
    }

    @CrossOrigin
    @GetMapping(value = "/sprintlist/{id}")
    public ResponseEntity<Sprint> getSprintById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int id) {
        ResponseEntity<Sprint> sprint = sprintService.getItemById(id);
        if (sprint.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ensureMember(authHeader, sprint.getBody());
        return new ResponseEntity<Sprint>(sprint.getBody(), HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping(value = "/sprintlist/active")
    public List<Sprint> getActiveSprints(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "project_id") int projectId) {
        return sprintService.findActiveSprintsByProjectId(projectId);
    }

    @CrossOrigin
    @PostMapping(value = "/sprintlist")
    public ResponseEntity<Sprint> addSprint(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Sprint sprint) throws Exception {
        if (sprint.getProject_id() == 0) {
            throw new IllegalArgumentException("Project ID is required");
        }

        ensureMember(authHeader, sprint);

        System.out.println("Received sprint: " + sprint.toString());
        Sprint sd = sprintService.addSprint(sprint);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + sd.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        URI location = URI.create("" + sd.getID());

        return ResponseEntity.created(location)
                .headers(responseHeaders).build();

    }

    @CrossOrigin
    @PutMapping(value = "sprintlist/{id}")
    public ResponseEntity<Sprint> updateSprint(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Sprint sprint,
            @PathVariable int id) {
        ensureMember(authHeader, sprint);

        try {
            Sprint sprint1 = sprintService.updateSprint(id, sprint);
            System.out.println(sprint1.toString());
            return new ResponseEntity<>(sprint1, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @DeleteMapping(value = "sprintlist/{id}")
    public ResponseEntity<Boolean> deleteSprint(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") int id) {
        ResponseEntity<Sprint> sprint = sprintService.getItemById(id);
        if (sprint.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ensureMember(authHeader, sprint.getBody());

        Boolean flag = false;
        try {
            flag = sprintService.deleteSprint(id);
            return new ResponseEntity<>(flag, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    private void ensureMember(String authHeader, Sprint sprint) {
        ResponseEntity<Project> prj = projectService.getItemById(sprint.getProject_id());
        if (prj.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
        }

        Project project = prj.getBody();
        Integer projectId = project.getID();

        User user = authContext.getCurrentUser(authHeader);
        if (!authContext.isMember(user.getID(), projectId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Not a member of project " + projectId);
        }

    }
}
