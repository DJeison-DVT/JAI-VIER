package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.repository.SubtaskRepository;
import com.springboot.MyTodoList.repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubtaskService {

    @Autowired
    private SubtaskRepository subtaskRepository;
    @Autowired
    private TaskRepository taskRepository;

    public List<Subtask> findAll() {
        List<Subtask> subtasks = subtaskRepository.findAll();
        return subtasks;
    }

    public ResponseEntity<Subtask> getItemById(int id) {
        Optional<Subtask> subtaskData = subtaskRepository.findById(id);
        if (subtaskData.isPresent()) {
            return new ResponseEntity<>(subtaskData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public Subtask addSubtask(Subtask subtask) {
        if (subtask.getTask_id() == 0) {
            throw new IllegalArgumentException("Subtask must be linked to an existing Task.");
        }

        Task existingTask = taskRepository.findById(subtask.getTask_id()).orElseThrow(
                () -> new IllegalArgumentException("Task not found with ID: " + subtask.getTask_id()));

        subtask.setTask(existingTask);

        return subtaskRepository.save(subtask);
    }

    public boolean deleteSubtask(int id) {
        try {
            subtaskRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Subtask updateSubtask(int id, Subtask st) {
        Optional<Subtask> subtaskData = subtaskRepository.findById(id);
        if (subtaskData.isPresent()) {
            Subtask subtask = subtaskData.get();
            subtask.setID(id);
            subtask.setTitle(st.getTitle());
            subtask.setDescription(st.getDescription());
            subtask.setCreated_at(st.getCreated_at());
            subtask.setUpdated_at(st.getUpdated_at());
            subtask.setStatus(st.getStatus());
            subtask.setTask(st.getTask());
            return subtaskRepository.save(subtask);
        } else {
            return null;
        }
    }
}
