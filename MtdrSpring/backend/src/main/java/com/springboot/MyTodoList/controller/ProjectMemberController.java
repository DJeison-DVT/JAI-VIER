package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springboot.MyTodoList.model.ProjectMember;
import com.springboot.MyTodoList.service.ProjectMemberService;

import java.net.URI;
import java.util.List;

@RestController
public class ProjectMemberController {
    @Autowired
    private ProjectMemberService projectMemberService;

    @CrossOrigin
    @GetMapping(value = "/projectmember")
    public List<ProjectMember> getAllProjectMembers() {
        return projectMemberService.findAll();
    }

    @CrossOrigin
    @GetMapping(value = "/projectmember/project/{projectId}")
    public List<ProjectMember> getProjectMembersByProjectId(@PathVariable int projectId) {
        return projectMemberService.getProjectMembersByProjectId(projectId);
    }

    @CrossOrigin
    @GetMapping(value = "/projectmember/user/{userId}")
    public List<ProjectMember> getProjectMembersByUserId(@PathVariable int userId) {
        return projectMemberService.getProjectMembersByUserId(userId);
    }

    @CrossOrigin
    @GetMapping(value = "/projectmember/{projectId}/{userId}")
    public ResponseEntity<ProjectMember> getProjectMemberById(@PathVariable int projectId, @PathVariable int userId) {
        try {
            ResponseEntity<ProjectMember> responseEntity = projectMemberService.getItemById(projectId, userId);
            return new ResponseEntity<ProjectMember>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @PostMapping(value = "/projectmember")
    public ResponseEntity<ProjectMember> addProjectMember(@RequestBody ProjectMember projectMember) throws Exception {
        ProjectMember pr = projectMemberService.addProjectMember(projectMember);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + pr.getId());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        URI location = URI.create("" + pr.getId());

        return ResponseEntity.created(location)
                .headers(responseHeaders).build();
    }

    @CrossOrigin
    @DeleteMapping(value = "/projectmember/{projectId}/{userId}")
    public ResponseEntity<ProjectMember> deleteProjectMember(@PathVariable int projectId, @PathVariable int userId) {
        try {
            boolean result = projectMemberService.deleteProjectMember(projectId, userId);
            if (result) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
