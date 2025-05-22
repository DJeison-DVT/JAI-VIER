package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.security.AuthContext;
import com.springboot.MyTodoList.service.ProjectService;

import java.net.URI;
import java.util.Collections;
import java.util.List;

@RestController
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private AuthContext authContext;

    @CrossOrigin
    @GetMapping(value = "/projectlist")
    public List<Project> getAllProjects(@RequestHeader("Authorization") String authHeader,
            @RequestParam(name = "admin", required = false, defaultValue = "false") boolean isAdmin) {

        User user = authContext.getCurrentUser(authHeader);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        if (isAdmin) {
            if (authContext.isAdmin(user)) {
                return projectService.findAll();
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not an admin");
        }

        Project selectedProject = user.getSelectedProject();
        if (selectedProject != null) {
            if (authContext.isMember(user.getID(), selectedProject.getID())) {
                Project proj = projectService
                        .getItemById(selectedProject.getID())
                        .getBody();
                if (proj == null) {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Project not found: " + selectedProject.getID());
                }
                return Collections.singletonList(proj);
            }
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Not a member of project " + selectedProject.getID());
        }

        return projectService.getProjectByUserID(user.getID());
    }

    @CrossOrigin
    @GetMapping(value = "/projectlist/{id}")
    public ResponseEntity<Project> getProjectById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int id) {
        ResponseEntity<Project> responseEntity = projectService.getItemById(id);

        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ensureMember(authHeader, responseEntity.getBody());
        return new ResponseEntity<Project>(responseEntity.getBody(), HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping(value = "/projectlist")
    public ResponseEntity<Project> addProject(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Project project) throws Exception {
        User user = authContext.getCurrentUser(authHeader);
        Project pr = projectService.addProject(project, user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + pr.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        URI location = URI.create("" + pr.getID());

        return ResponseEntity.created(location)
                .headers(responseHeaders).build();
    }

    @CrossOrigin
    @PutMapping(value = "projectlist/{id}")
    public ResponseEntity<Project> updateProject(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Project p,
            @PathVariable int id) {
        try {
            Project project = projectService.getItemById(id).getBody();
            ensureMember(authHeader, project);
            project = projectService.updateProject(id, p);
            System.out.println(project.toString());
            return new ResponseEntity<>(project, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @DeleteMapping(value = "/projectlist/{id}")
    public ResponseEntity<Boolean> deleteProject(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") int id) {
        ResponseEntity<Project> project = projectService.getItemById(id);
        if (project.getStatusCode() != HttpStatus.OK) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        ensureMember(authHeader, project.getBody());

        projectService.deleteProject(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    private void ensureMember(String authHeader, Project project) {
        User user = authContext.getCurrentUser(authHeader);
        if (!authContext.isMember(user.getID(), project.getID())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of the project");
        }
    }
}
