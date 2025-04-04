package com.springboot.MyTodoList.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.ProjectRepository;
import com.springboot.MyTodoList.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        return users;
    }

    public ResponseEntity<User> getItemById(int id) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public User addUser(User user) {
        Optional<User> existingUsername = userRepository.findByUsername(user.getUsername());
        if (existingUsername.isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        Optional<User> existingEmail = userRepository.findByEmail(user.getEmail());
        if (existingEmail.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        System.out.println("Hashing: " + user.getPassword_hash());
        String hashedPassword = passwordEncoder.encode(user.getPassword_hash());
        user.setPassword_hash(hashedPassword);
        user.setCreated_at(OffsetDateTime.now());
        user.setUpdated_at(OffsetDateTime.now());
        user.setActive(true);
        return userRepository.save(user);
    }

    public boolean checkUser(String username, String password) {
        Optional<User> userData = userRepository.findByUsername(username);
        if (userData.isPresent()) {
            User existing_user = userData.get();
            if (!existing_user.isActive()) {
                return false;
            }
            existing_user.setLast_login(OffsetDateTime.now());
            userRepository.save(existing_user);
            return passwordEncoder.matches(password, existing_user.getPassword_hash());
        } else {
            return false;
        }
    }

    public boolean deleteUser(int id) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            User existingUser = userData.get();
            existingUser.setActive(false);
            userRepository.save(existingUser);
            return true;
        } else {
            return false;
        }
    }

    public User updateUser(int id, User user) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            User existingUser = userData.get();
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setFull_name(user.getFull_name());
            existingUser.setWork_mode(user.getWork_mode());
            existingUser.setUpdated_at(OffsetDateTime.now());
            existingUser.setActive(user.isActive());
            Integer project_id = user.getSelectedProject_id();
            if (project_id != null) {
                Optional<Project> projectData = projectRepository.findById(project_id);
                if (projectData.isPresent()) {
                    // TODO Check if is project member
                    Project existingProject = projectData.get();
                    existingUser.setSelectedProject(existingProject);
                } else {
                    throw new IllegalArgumentException("Project not found with ID: " + project_id);
                }
            }
            return userRepository.save(existingUser);
        } else {
            return null;
        }
    }
}
