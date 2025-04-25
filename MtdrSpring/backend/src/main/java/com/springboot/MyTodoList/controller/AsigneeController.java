package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springboot.MyTodoList.model.Asignee;
import com.springboot.MyTodoList.service.AsigneeService;

import java.net.URI;
import java.util.List;

@RestController
public class AsigneeController {
    @Autowired
    private AsigneeService asigneeService;

    // @CrossOrigin
    @GetMapping(value = "/asignee")
    public List<Asignee> getAllAsignees() {
        return asigneeService.findAll();
    }

    // @CrossOrigin
    @GetMapping(value = "/asignee/task/{taskId}")
    public List<Asignee> getAsigneesByTaskId(@PathVariable int taskId) {
        return asigneeService.getAsigneesByTaskId(taskId);
    }

    // @CrossOrigin
    @GetMapping(value = "/asignee/user/{userId}")
    public List<Asignee> getAsigneesByUserId(@PathVariable int userId) {
        return asigneeService.getAsigneesByUserId(userId);
    }

    // @CrossOrigin
    @GetMapping(value = "/asignee/{taskId}/{userId}")
    public ResponseEntity<Asignee> getAsigneeById(@PathVariable int taskId, @PathVariable int userId) {
        try {
            ResponseEntity<Asignee> responseEntity = asigneeService.getItemById(taskId, userId);
            return new ResponseEntity<Asignee>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // @CrossOrigin
    @PostMapping(value = "/asignee")
    public ResponseEntity<Asignee> addAsignee(@RequestBody Asignee asignee) throws Exception {
        Asignee asg = asigneeService.addAsignee(asignee);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + asg.getId());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        URI location = URI.create("" + asg.getId());

        return ResponseEntity.created(location)
                .headers(responseHeaders).build();
    }

    // @CrossOrigin
    @DeleteMapping(value = "/asignee/{taskId}/{userId}")
    public ResponseEntity<Asignee> deleteAsignee(@PathVariable int taskId, @PathVariable int userId) {
        try {
            boolean result = asigneeService.deleteAsignee(taskId, userId);
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
