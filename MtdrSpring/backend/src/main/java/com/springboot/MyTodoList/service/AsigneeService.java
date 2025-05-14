package com.springboot.MyTodoList.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.springboot.MyTodoList.dto.UserSummary;
import com.springboot.MyTodoList.model.Asignee;
import com.springboot.MyTodoList.model.AsigneeId;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.AsigneeRepository;
import com.springboot.MyTodoList.repository.TaskRepository;
import com.springboot.MyTodoList.repository.UserRepository;

@Service
public class AsigneeService {
    @Autowired
    private AsigneeRepository asigneeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    public List<Asignee> findAll() {
        List<Asignee> asignees = asigneeRepository.findAll();
        return asignees;
    }

    public List<UserSummary> getAsigneesByTaskId(int taskId) {
        List<UserSummary> asignees = asigneeRepository.findUserSummariesByTaskId(taskId);
        return asignees;
    }

    public List<Asignee> getAsigneesByUserId(int userId) {
        List<Asignee> asignees = asigneeRepository.findById_UserId(userId);
        return asignees;
    }

    public ResponseEntity<Asignee> getItemById(int taskId, int userId) {
        AsigneeId asigneeId = new AsigneeId(taskId, userId);
        Optional<Asignee> asigneeData = asigneeRepository.findById(asigneeId);
        if (asigneeData.isPresent()) {
            return new ResponseEntity<>(asigneeData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public Asignee addAsignee(Asignee asignee) {
        User user = userRepository.findById(asignee.getUser_id()).orElseThrow(
                () -> new IllegalArgumentException("User with ID " + asignee.getUser_id() + " does not exist."));
        Task task = taskRepository.findById(asignee.getTask_id()).orElseThrow(
                () -> new IllegalArgumentException("Task with ID " + asignee.getTask_id() + " does not exist."));

        AsigneeId asigneeId = new AsigneeId(asignee.getTask_id(), asignee.getUser_id());

        Asignee as = new Asignee();
        as.setId(asigneeId);
        as.setUser(user);
        as.setTask(task);
        as.setCreated_at(OffsetDateTime.now());

        return asigneeRepository.save(as);
    }

    public boolean deleteAsignee(int taskId, int userId) {
        try {
            AsigneeId asigneeId = new AsigneeId(taskId, userId);
            asigneeRepository.deleteById(asigneeId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
