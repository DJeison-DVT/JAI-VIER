package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.springboot.MyTodoList.dto.LoginRequest;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.UserService;

import java.net.URI;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // @CrossOrigin
    @GetMapping(value = "/userlist")
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    // @CrossOrigin
    @GetMapping(value = "/userlist/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        try {
            ResponseEntity<User> responseEntity = userService.getItemById(id);
            return new ResponseEntity<User>(responseEntity.getBody(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/userlist")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        try {
            User us = userService.addUser(user);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("location", String.valueOf(us.getID()));
            responseHeaders.set("Access-Control-Expose-Headers", "location");
            URI location = URI.create(String.valueOf(us.getID()));

            return ResponseEntity.created(location)
                    .headers(responseHeaders).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // @CrossOrigin
    @PostMapping(value = "/userlist/login")
    public ResponseEntity<Boolean> checkUser(@RequestBody LoginRequest loginRequest) {
        try {
            boolean check = userService.checkUser(loginRequest.getUsername(), loginRequest.getPassword());
            return new ResponseEntity<>(check, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    // @CrossOrigin
    @PutMapping(value = "userlist/{id}")
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable int id) {
        try {
            User user1 = userService.updateUser(id, user);
            System.out.println(user1.toString());
            return new ResponseEntity<>(user1, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // @CrossOrigin
    @DeleteMapping(value = "/userlist/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable int id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
