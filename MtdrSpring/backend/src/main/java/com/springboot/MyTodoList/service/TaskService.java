package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.repository.ProjectRepository;
import com.springboot.MyTodoList.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;

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
        if (task.getProject_id() == 0) {
            throw new IllegalArgumentException("Task must be linked to an existing Project.");
        }

        Project existingProject = projectRepository.findById(task.getProject_id()).orElseThrow(
                () -> new IllegalArgumentException("Project not found with ID: " + task.getProject_id()));

        task.setProject(existingProject);
        task.setCreated_at(OffsetDateTime.now());
        task.setUpdated_at(OffsetDateTime.now());

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
            task.setUpdated_at(OffsetDateTime.now());
            task.setDue_date(td.getDue_date());
            task.setPriority(td.getPriority());
            task.setStatus(td.getStatus());
            task.setEstimated_hours(td.getEstimated_hours());
            return taskRepository.save(task);
        } else {
            return null;
        }
    }

}
