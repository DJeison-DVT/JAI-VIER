package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> findAll() {
        List<Task> tasks = taskRepository.findAll();
        return tasks;
    }

    public ResponseEntity<Task> getItemById(int id) {
        Optional<Task> taskData = taskRepository.findById(id);
        if (taskData.isPresent()) {
            return new ResponseEntity<>(taskData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

    public boolean deleteTask(int id) {
        try {
            taskRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Task updateTask(int id, Task td) {
        Optional<Task> taskData = taskRepository.findById(id);
        if (taskData.isPresent()) {
            Task task = taskData.get();
            task.setID(id);
            task.setTitle(td.getTitle());
            task.setDescription(td.getDescription());
            task.setCreated_at(td.getCreated_at());
            task.setUpdated_at(td.getUpdated_at());
            task.setDue_date(td.getDue_date());
            task.setPriority(td.getPriority());
            task.setStatus(td.getStatus());
            task.setEstimated_hours(td.getEstimated_hours());
            Task savedTask = taskRepository.save(task);

            if (td.getSubtasks() != null) {
                for (Subtask subtask : td.getSubtasks()) {
                    subtask.setTask(savedTask);
                }
                savedTask.setSubtasks(td.getSubtasks());
            }
            return taskRepository.save(savedTask);
        } else {
            return null;
        }
    }

}
