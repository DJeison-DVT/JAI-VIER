package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.service.SubtaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SubtaskController {

    @Autowired
    private SubtaskService subtaskService;

    // @CrossOrigin
    @GetMapping(value = "/subtasklist/{id}")
    public ResponseEntity<Subtask> getSubtaskById(@PathVariable int id) {
        try {
            ResponseEntity<Subtask> responseEntity = subtaskService.getItemById(id);
            return new ResponseEntity<Subtask>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // @CrossOrigin
    @PostMapping(value = "/subtasklist")
    public ResponseEntity addSubtask(@RequestBody Subtask subtask) throws Exception {
        Subtask st = subtaskService.addSubtask(subtask);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + st.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        // URI location = URI.create(""+st.getID())

        return ResponseEntity.ok()
                .headers(responseHeaders).build();
    }

    // @CrossOrigin
    @PutMapping(value = "subtasklist/{id}")
    public ResponseEntity updateSubtask(@RequestBody Subtask subtask, @PathVariable int id) {
        try {
            Subtask subtask1 = subtaskService.updateSubtask(id, subtask);
            System.out.println(subtask1.toString());
            return new ResponseEntity<>(subtask1, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // @CrossOrigin
    @DeleteMapping(value = "/subtasklist/{id}")
    public ResponseEntity<Boolean> deleteSubtask(@PathVariable("id") int id) {
        Boolean flag = false;
        flag = subtaskService.deleteSubtask(id);
        if (flag) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
