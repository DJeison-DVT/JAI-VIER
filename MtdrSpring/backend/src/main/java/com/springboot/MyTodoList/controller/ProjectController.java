package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.service.ProjectService;

import java.net.URI;
import java.util.List;

@RestController
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    // @CrossOrigin
    @GetMapping(value = "/projectlist")
    public List<Project> getAllProjects() {
        return projectService.findAll();
    }

    // @CrossOrigin
    @GetMapping(value = "/projectlist/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable int id) {
        try {
            ResponseEntity<Project> responseEntity = projectService.getItemById(id);
            return new ResponseEntity<Project>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // @CrossOrigin
    @PostMapping(value = "/projectlist")
    public ResponseEntity<Project> addProject(@RequestBody Project project) throws Exception {
        Project pr = projectService.addProject(project);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + pr.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        URI location = URI.create("" + pr.getID());

        return ResponseEntity.created(location)
                .headers(responseHeaders).build();
    }

    // @CrossOrigin
    @PutMapping(value = "projectlist/{id}")
    public ResponseEntity<Project> updateProject(@RequestBody Project project, @PathVariable int id) {
        try {
            Project project1 = projectService.updateProject(id, project);
            System.out.println(project1.toString());
            return new ResponseEntity<>(project1, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // @CrossOrigin
    @DeleteMapping(value = "/projectlist/{id}")
    public ResponseEntity<Boolean> deleteProject(@PathVariable("id") int id) {
        try {
            projectService.deleteProject(id);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
