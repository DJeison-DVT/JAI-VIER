package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.security.AuthContext;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private AuthContext authContext;
    @Autowired
    private SprintService sprintService;

    @CrossOrigin
    @GetMapping(value = "/tasklist")
    public List<Task> getAllTasks(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(name = "admin", required = false, defaultValue = "false") boolean isAdmin) {

        User user = authContext.getCurrentUser(authHeader);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        if (isAdmin) {
            if (authContext.isAdmin(user)) {
                return taskService.findAll();
            }
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not an admin");
        }

        Project selectedProject = user.getSelectedProject();
        if (selectedProject != null) {
            if (authContext.isMember(user.getID(), selectedProject.getID())) {
                return taskService.getTasksByProjectId(selectedProject.getID());
            }
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Not a member of project " + selectedProject.getID());

        }

        return taskService.getTasksByUserId(user.getID());
    }

    @CrossOrigin
    @GetMapping(value = "/tasklist/{id}")
    public ResponseEntity<Task> getTaskById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int id) {
        System.out.println("getTaskById: " + id);
        try {
            ResponseEntity<Task> responseEntity = taskService.getItemById(id);
            Task task = responseEntity.getBody();
            System.out.println("getTaskById: " + task);

            ensureMember(authHeader, task);

            return new ResponseEntity<Task>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @PostMapping(value = "/tasklist")
    public ResponseEntity<Task> addTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Task task) throws Exception {
        if (task.getSprint_id() == 0) {
            throw new IllegalArgumentException("Task must be linked to an existing Sprint.");
        }

        ensureMember(authHeader, task);

        Task td = taskService.addTask(task);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + td.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        URI location = URI.create("" + td.getID());

        return ResponseEntity.created(location)
                .headers(responseHeaders).build();
    }

    @CrossOrigin
    @PutMapping(value = "tasklist/{id}")
    public ResponseEntity<Task> updateTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Task tk,
            @PathVariable int id) {

        try {
            Task task = taskService.getItemById(id).getBody();
            ensureMember(authHeader, task);
            task = taskService.updateTask(id, tk);
            System.out.println(task.toString());
            return new ResponseEntity<>(task, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @DeleteMapping(value = "tasklist/{id}")
    public ResponseEntity<Boolean> deleteTask(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("id") int id) {
        ResponseEntity<Task> task = taskService.getItemById(id);
        if (task.getStatusCode() == HttpStatus.NOT_FOUND) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        ensureMember(authHeader, task.getBody());

        Boolean flag = false;
        try {
            flag = taskService.deleteTask(id);
            return new ResponseEntity<>(flag, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
        }
    }

    private void ensureMember(String authHeader, Task task) {
        ResponseEntity<Sprint> spr = sprintService.getItemById(task.getSprint_id());
        if (spr.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sprint not found");
        }

        Sprint sprint = spr.getBody();
        Integer projectId = sprint.getProject().getID();

        User user = authContext.getCurrentUser(authHeader);
        if (!authContext.isMember(user.getID(), projectId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Not a member of project " + projectId);
        }
    }

}
