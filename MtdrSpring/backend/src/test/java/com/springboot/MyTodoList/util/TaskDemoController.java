package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.security.AuthContext;
import com.springboot.MyTodoList.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la demostración de funcionalidades de tareas
 */
@RestController
@RequestMapping("/demo")
@Api(value = "API de demostración para tareas", description = "Operaciones para demostrar funcionalidades específicas de tareas")
public class TaskDemoController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private AuthContext authContext;

    @PostMapping("/tasks")
    @ApiOperation(value = "Crear una nueva tarea", notes = "Crea una nueva tarea en el sistema")
    public ResponseEntity<Map<String, Object>> createTask(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Task task) {

        User user = authContext.getCurrentUser(authHeader);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        try {
            Task createdTask = taskService.addTask(task);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Tarea creada exitosamente");
            response.put("task", createdTask);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al crear la tarea: " + e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/sprints/{sprintId}/completed-tasks")
    @ApiOperation(value = "Ver tareas completadas de un sprint", notes = "Obtiene todas las tareas con estado DONE en un sprint específico")
    public ResponseEntity<Map<String, Object>> getCompletedTasksBySprintId(
            @RequestHeader("Authorization") String authHeader,
            @ApiParam(value = "ID del sprint", required = true) @PathVariable Integer sprintId) {

        User user = authContext.getCurrentUser(authHeader);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        try {
            List<Task> completedTasks = taskService.getCompletedTasksBySprintId(sprintId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("sprintId", sprintId);
            response.put("completedTasksCount", completedTasks.size());
            response.put("completedTasks", completedTasks);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener tareas completadas: " + e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{userId}/sprints/{sprintId}/completed-tasks")
    @ApiOperation(value = "Ver tareas completadas de un usuario en un sprint", notes = "Obtiene todas las tareas con estado DONE asignadas a un usuario específico en un sprint específico")
    public ResponseEntity<Map<String, Object>> getCompletedTasksByUserAndSprintId(
            @RequestHeader("Authorization") String authHeader,
            @ApiParam(value = "ID del usuario", required = true) @PathVariable Integer userId,
            @ApiParam(value = "ID del sprint", required = true) @PathVariable Integer sprintId) {

        User user = authContext.getCurrentUser(authHeader);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        // Verificar si el usuario actual es admin o es el mismo usuario solicitado
        if (!authContext.isAdmin(user) && !user.getID().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permisos para ver las tareas de este usuario");
        }

        try {
            List<Task> userCompletedTasks = taskService.getCompletedTasksByUserAndSprintId(userId, sprintId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("sprintId", sprintId);
            response.put("completedTasksCount", userCompletedTasks.size());
            response.put("completedTasks", userCompletedTasks);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener tareas completadas del usuario: " + e.getMessage());

            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}