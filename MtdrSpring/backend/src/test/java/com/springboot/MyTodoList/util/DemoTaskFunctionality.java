package com.springboot.MyTodoList.demo;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Clase para demostrar la funcionalidad requerida para la presentación
 * - Crear tarea
 * - Ver las tareas completadas de un sprint
 * - Ver las tareas completadas de un usuario en un sprint
 */
@Configuration
public class DemoTaskFunctionality {

    @Autowired
    private TaskService taskService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Bean
    public CommandLineRunner demoTaskFeatures() {
        return args -> {
            System.out.println("\n==============================================================");
            System.out.println("DEMOSTRACIÓN DE FUNCIONALIDADES DE TAREAS");
            System.out.println("==============================================================\n");

            // Demo 1: Crear una nueva tarea
            demoCreateTask();

            // Demo 2: Ver tareas completadas de un sprint
            int sprintId = 1; // Cambia esto según tu base de datos
            demoViewCompletedTasksOfSprint(sprintId);

            // Demo 3: Ver tareas completadas de un usuario en un sprint
            int userId = 1; // Cambia esto según tu base de datos
            demoViewCompletedTasksOfUserInSprint(userId, sprintId);
        };
    }

    private void demoCreateTask() {
        System.out.println("\n----- DEMO 1: CREAR UNA NUEVA TAREA -----");

        try {
            // Crear una nueva tarea
            Task newTask = new Task();
            newTask.setTitle("Tarea de demostración");
            newTask.setDescription("Esta es una tarea creada para la demostración");
            newTask.setPriority(2); // Prioridad media
            newTask.setStatus(0); // Estado: TODO
            newTask.setSprint_id(1); // Sprint ID: 1 (cámbialo según tu BD)
            newTask.setDue_date(OffsetDateTime.now().plusDays(7)); // Fecha de vencimiento en 7 días
            newTask.setEstimated_hours(8); // 8 horas estimadas

            // Guardar la tarea
            Task savedTask = taskService.addTask(newTask);

            // Mostrar detalles de la tarea guardada
            System.out.println("✅ Tarea creada exitosamente con ID: " + savedTask.getID());
            System.out.println("Detalles de la tarea:");
            printTaskDetails(savedTask);

        } catch (Exception e) {
            System.err.println("❌ Error al crear la tarea: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void demoViewCompletedTasksOfSprint(int sprintId) {
        System.out.println("\n----- DEMO 2: VER TAREAS COMPLETADAS DE UN SPRINT -----");
        System.out.println("Sprint ID: " + sprintId);

        try {
            // Obtener tareas completadas del sprint
            List<Task> completedTasks = taskService.getCompletedTasksBySprintId(sprintId);

            // Mostrar resultados
            System.out.println("✅ Se encontraron " + completedTasks.size() + " tareas completadas en el sprint");

            if (completedTasks.isEmpty()) {
                System.out.println("No hay tareas completadas en este sprint.");
            } else {
                System.out.println("\nListado de tareas completadas:");
                for (Task task : completedTasks) {
                    printTaskDetails(task);
                    System.out.println("----------------");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error al buscar tareas completadas del sprint: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void demoViewCompletedTasksOfUserInSprint(int userId, int sprintId) {
        System.out.println("\n----- DEMO 3: VER TAREAS COMPLETADAS DE UN USUARIO EN UN SPRINT -----");
        System.out.println("Usuario ID: " + userId);
        System.out.println("Sprint ID: " + sprintId);

        try {
            // Obtener tareas completadas del usuario en el sprint
            List<Task> userCompletedTasks = taskService.getCompletedTasksByUserAndSprintId(userId, sprintId);

            // Mostrar resultados
            System.out.println("✅ Se encontraron " + userCompletedTasks.size()
                    + " tareas completadas por el usuario en el sprint");

            if (userCompletedTasks.isEmpty()) {
                System.out.println("No hay tareas completadas por este usuario en este sprint.");
            } else {
                System.out.println("\nListado de tareas completadas por el usuario:");
                for (Task task : userCompletedTasks) {
                    printTaskDetails(task);
                    System.out.println("----------------");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error al buscar tareas completadas del usuario en el sprint: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printTaskDetails(Task task) {
        System.out.println("ID: " + task.getID());
        System.out.println("Título: " + task.getTitle());
        System.out.println("Descripción: " + task.getDescription());
        System.out.println("Prioridad: " + task.priorityText());
        System.out.println("Estado: " + task.statusText());

        if (task.getDue_date() != null) {
            System.out.println("Fecha límite: " + task.getDue_date().format(FORMATTER));
        }

        System.out.println("Horas estimadas: " + task.getEstimated_hours());

        if (task.getReal_hours() != null) {
            System.out.println("Horas reales: " + task.getReal_hours());
        }

        if (task.getSprint() != null) {
            System.out.println("Sprint: " + task.getSprint().getName() + " (ID: " + task.getSprint().getID() + ")");
        } else {
            System.out.println("Sprint ID: " + task.getSprint_id());
        }
    }
}