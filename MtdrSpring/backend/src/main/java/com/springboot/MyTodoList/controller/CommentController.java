package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Comment;
import com.springboot.MyTodoList.service.CommentService;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @CrossOrigin
    @GetMapping(value = "/commentlist/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable int id) {
        try {
            ResponseEntity<Comment> responseEntity = commentService.getItemById(id);
            return new ResponseEntity<Comment>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @GetMapping(value = "/commentlist/task/{taskId}")
    public ResponseEntity<List<Comment>> getCommentsByTaskId(@PathVariable int taskId) {
        try {
            ResponseEntity<List<Comment>> responseEntity = commentService.getItemsByTaskId(taskId);
            return new ResponseEntity<List<Comment>>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @PostMapping(value = "/commentlist")
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) throws Exception {
        Comment c = commentService.addComment(comment);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("location", "" + c.getID());
        responseHeaders.set("Access-Control-Expose-Headers", "location");
        URI location = URI.create("" + c.getID());

        return ResponseEntity.created(location)
                .headers(responseHeaders).build();
    }

    @CrossOrigin
    @PutMapping(value = "commentlist/{id}")
    public ResponseEntity<Comment> updateComment(@RequestBody Comment comment, @PathVariable int id) {
        try {
            Comment comment1 = commentService.updateComment(id, comment);
            System.out.println(comment1.toString());
            return new ResponseEntity<>(comment1, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin
    @DeleteMapping(value = "/commentlist/{id}")
    public ResponseEntity<Boolean> deleteComment(@PathVariable("id") int id) {
        try {
            commentService.deleteComment(id);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
