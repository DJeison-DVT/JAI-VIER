package com.springboot.MyTodoList.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class SprintController {

    @Autowired
    private SprintService sprintService;

    // @CrossOrigin
    @GetMapping(value = "/sprintlist")
    public List<Sprint> getAllSprints(@RequestParam(value = "project_id", required = false) Integer projectId) {
        if (projectId != null) {
            return sprintService.findByProject_ID(projectId);
        } else {
            return sprintService.findAll();
        }
    }

    @GetMapping(value = "/sprintlist/{id}")
    public ResponseEntity<Sprint> getSprintById(@PathVariable int id) {
        try {
            ResponseEntity<Sprint> responseEntity = sprintService.getItemById(id);
            return new ResponseEntity<Sprint>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // @CrossOrigin
    @GetMapping(value = "/sprintlist/active")
    public List<Sprint> getActiveSprints(@RequestParam(value = "project_id") int projectId) {
        return sprintService.findActiveSprintsByProjectId(projectId);
    }

    // @CrossOrigin
    @PostMapping(value = "/sprintlist")
    public ResponseEntity<Sprint> addSprint(@RequestBody Sprint sprint) throws Exception {
        System.out.println("Received sprint: " + sprint.toString());
        Sprint sd = sprintService.addSprint(sprint);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + sd.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        URI location = URI.create("" + sd.getID());

        return ResponseEntity.created(location)
                .headers(responseHeaders).build();

    }

    // @CrossOrigin
    @PutMapping(value = "sprintlist/{id}")
    public ResponseEntity<Sprint> updateSprint(@RequestBody Sprint sprint, @PathVariable int id) {
        try {
            Sprint sprint1 = sprintService.updateSprint(id, sprint);
            System.out.println(sprint1.toString());
            return new ResponseEntity<>(sprint1, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // @CrossOrigin
    @DeleteMapping(value = "sprintlist/{id}")
    public ResponseEntity<Boolean> deleteSprint(@PathVariable("id") int id) {
        Boolean flag = false;
        try {
            flag = sprintService.deleteSprint(id);
            return new ResponseEntity<>(flag, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
