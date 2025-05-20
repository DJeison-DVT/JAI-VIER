package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test creating a new task")
    public void testAddTask() {
        // Arrange
        Task newTask = new Task();
        newTask.setTitle("Tarea de prueba");
        newTask.setDescription("Descripción de la tarea de prueba");
        newTask.setPriority(2); // Media prioridad
        newTask.setStatus(0); // TODO
        newTask.setSprint_id(1);
        newTask.setDue_date(OffsetDateTime.now().plusDays(7));

        Task savedTask = new Task();
        savedTask.setID(1);
        savedTask.setTitle("Tarea de prueba");
        savedTask.setDescription("Descripción de la tarea de prueba");
        savedTask.setPriority(2);
        savedTask.setStatus(0);
        savedTask.setSprint_id(1);
        savedTask.setDue_date(OffsetDateTime.now().plusDays(7));

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        // Act
        Task result = taskService.addTask(newTask);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getID());
        assertEquals("Tarea de prueba", result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));

        System.out.println("✅ Test de creación de tarea exitoso");
        System.out.println("Tarea creada: " + result.toString());
    }

    @Test
    @DisplayName("Test viewing completed tasks of a sprint")
    public void testGetCompletedTasksBySprintId() {
        // Arrange
        int sprintId = 1;
        List<Task> completedTasks = new ArrayList<>();

        Task task1 = new Task();
        task1.setID(1);
        task1.setTitle("Tarea Completada 1");
        task1.setStatus(3); // Completed status

        Task task2 = new Task();
        task2.setID(2);
        task2.setTitle("Tarea Completada 2");
        task2.setStatus(3); // Completed status

        completedTasks.add(task1);
        completedTasks.add(task2);

        when(taskRepository.findBySprint_IDAndStatusEquals(anyInt(), eq("DONE"))).thenReturn(completedTasks);

        // Act
        List<Task> result = taskService.getCompletedTasksBySprintId(sprintId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Tarea Completada 1", result.get(0).getTitle());
        assertEquals("Tarea Completada 2", result.get(1).getTitle());
        verify(taskRepository, times(1)).findBySprint_IDAndStatusEquals(sprintId, "DONE");

        System.out.println("✅ Test de visualización de tareas completadas de un sprint exitoso");
        System.out.println("Número de tareas completadas encontradas: " + result.size());
        for (Task task : result) {
            System.out.println("- " + task.getTitle());
        }
    }

    @Test
    @DisplayName("Test viewing completed tasks of a user in a sprint")
    public void testGetCompletedTasksByUserAndSprintId() {
        // Arrange
        int userId = 10;
        int sprintId = 1;
        List<Task> userCompletedTasks = new ArrayList<>();

        Task task1 = new Task();
        task1.setID(1);
        task1.setTitle("Tarea de Usuario Completada 1");
        task1.setStatus(3); // Completed status

        userCompletedTasks.add(task1);

        when(taskRepository.findByAsignees_User_IDAndSprint_IDAndStatusEquals(anyInt(), anyInt(), eq("DONE")))
                .thenReturn(userCompletedTasks);

        // Act
        List<Task> result = taskService.getCompletedTasksByUserAndSprintId(userId, sprintId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Tarea de Usuario Completada 1", result.get(0).getTitle());
        verify(taskRepository, times(1))
                .findByAsignees_User_IDAndSprint_IDAndStatusEquals(userId, sprintId, "DONE");

        System.out.println("✅ Test de visualización de tareas completadas de un usuario en un sprint exitoso");
        System.out.println("Usuario ID: " + userId);
        System.out.println("Sprint ID: " + sprintId);
        System.out.println("Número de tareas completadas encontradas: " + result.size());
        for (Task task : result) {
            System.out.println("- " + task.getTitle());
        }
    }
}