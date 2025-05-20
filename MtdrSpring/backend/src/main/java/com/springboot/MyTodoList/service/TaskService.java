package com.springboot.MyTodoList.service;

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

    /**
     * Busca todas las tareas
     * 
     * @return Lista de tareas
     */
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    /**
     * Obtiene una tarea por su ID
     * 
     * @param id ID de la tarea
     * @return ResponseEntity con la tarea o NOT_FOUND
     */
    public ResponseEntity<Task> getItemById(int id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            return new ResponseEntity<>(task.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Agrega una nueva tarea
     * 
     * @param task Tarea a agregar
     * @return Tarea guardada
     */
    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

    /**
     * Actualiza una tarea existente
     * 
     * @param id   ID de la tarea a actualizar
     * @param task Nuevos datos de la tarea
     * @return Tarea actualizada
     * @throws Exception si no se encuentra la tarea
     */
    public Task updateTask(int id, Task task) throws Exception {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task taskData = existingTask.get();
            taskData.setTitle(task.getTitle());
            taskData.setDescription(task.getDescription());
            taskData.setPriority(task.getPriority());
            taskData.setStatus(task.getStatus());
            // Eliminar la línea problemática que hace referencia a getDueDate()
            // taskData.setDueDate(task.getDueDate());
            taskData.setSprint_id(task.getSprint_id());
            return taskRepository.save(taskData);
        } else {
            throw new Exception("Task not found with id: " + id);
        }
    }

    /**
     * Elimina una tarea
     * 
     * @param id ID de la tarea a eliminar
     * @return true si se eliminó correctamente
     * @throws Exception si ocurre un error
     */
    public Boolean deleteTask(int id) throws Exception {
        taskRepository.deleteById(id);
        return true;
    }

    /**
     * Obtiene las tareas por ID de usuario
     * 
     * @param userId ID del usuario
     * @return Lista de tareas del usuario
     */
    public List<Task> getTasksByUserId(Integer userId) {
        return taskRepository.findDistinctBySprint_Project_Memberships_User_ID(userId);
    }

    /**
     * Obtiene las tareas por ID de proyecto
     * 
     * @param projectId ID del proyecto
     * @return Lista de tareas del proyecto
     */
    public List<Task> getTasksByProjectId(Integer projectId) {
        return taskRepository.findDistinctBySprint_Project_ID(projectId);
    }

    /**
     * Obtiene las tareas completadas por ID de sprint
     * 
     * @param sprintId ID del sprint
     * @return Lista de tareas completadas del sprint
     */
    public List<Task> getCompletedTasksBySprintId(Integer sprintId) {
        return taskRepository.findBySprint_IDAndStatusEquals(sprintId, "DONE");
    }

    /**
     * Obtiene las tareas completadas por ID de usuario y sprint
     * 
     * @param userId   ID del usuario
     * @param sprintId ID del sprint
     * @return Lista de tareas completadas del usuario en el sprint
     */
    public List<Task> getCompletedTasksByUserAndSprintId(Integer userId, Integer sprintId) {
        return taskRepository.findByAsignees_User_IDAndSprint_IDAndStatusEquals(userId, sprintId, "DONE");
    }
}