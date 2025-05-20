package com.springboot.MyTodoList;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.service.UserService;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Clase de demostración para probar las funcionalidades requeridas:
 * 1. Crear tarea
 * 2. Ver las tareas completadas de un sprint
 * 3. Ver las tareas completadas de un usuario en un sprint
 */
@SpringBootTest
public class DemoTest {

    private static final Logger logger = LoggerFactory.getLogger(DemoTest.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    /**
     * Test para demostrar la creación de una tarea
     */
    @Test
    public void testCrearTarea() {
        logger.info("=== DEMOSTRACIÓN: CREAR TAREA ===");

        // Crear objeto de tarea para la demostración
        Task nuevaTarea = new Task();
        nuevaTarea.setTitle("Tarea de Demostración");
        nuevaTarea.setDescription("Esta es una tarea creada para la demostración");
        nuevaTarea.setDue_date(OffsetDateTime.now().plusDays(7)); // Fecha límite en 7 días
        nuevaTarea.setPriority(2); // Prioridad media
        nuevaTarea.setStatus(0); // Estado: TODO
        nuevaTarea.setEstimated_hours(8);
        nuevaTarea.setSprint_id(1); // Ajusta según el ID de sprint disponible en tu BD

        try {
            // Guardar la tarea
            Task tareaGuardada = taskService.addTask(nuevaTarea);

            // Mostrar evidencia de que la tarea fue creada
            logger.info("Tarea creada exitosamente con ID: {}", tareaGuardada.getID());
            logger.info("Detalles de la tarea: {}", tareaGuardada.publicDescription());
        } catch (Exception e) {
            logger.error("Error al crear la tarea: {}", e.getMessage());
        }
    }

    /**
     * Test para demostrar la visualización de tareas completadas de un sprint
     */
    @Test
    public void testVerTareasCompletadasSprint() {
        logger.info("=== DEMOSTRACIÓN: VER TAREAS COMPLETADAS DE UN SPRINT ===");

        // Definir ID del sprint para la prueba (ajusta según tu BD)
        int sprintId = 1;

        try {
            // Obtener tareas completadas del sprint
            List<Task> tareasCompletadas = taskService.getCompletedTasksBySprintId(sprintId);

            // Mostrar evidencia
            logger.info("Se encontraron {} tareas completadas en el Sprint {}", tareasCompletadas.size(), sprintId);

            // Mostrar detalles de cada tarea completada
            for (Task tarea : tareasCompletadas) {
                logger.info("Tarea completada: {}", tarea.quickDescription());
            }

            if (tareasCompletadas.isEmpty()) {
                logger.info("No hay tareas completadas en este sprint.");
            }
        } catch (Exception e) {
            logger.error("Error al obtener tareas completadas del sprint: {}", e.getMessage());
        }
    }

    /**
     * Test para demostrar la visualización de tareas completadas de un usuario en
     * un sprint
     */
    @Test
    public void testVerTareasCompletadasUsuarioSprint() {
        logger.info("=== DEMOSTRACIÓN: VER TAREAS COMPLETADAS DE UN USUARIO EN UN SPRINT ===");

        // Definir IDs para la prueba (ajusta según tu BD)
        int userId = 1; // Ajusta según el ID de usuario disponible
        int sprintId = 1; // Ajusta según el ID de sprint disponible

        try {
            // Obtener el usuario para mostrar su nombre en los logs
            ResponseEntity<User> userResponse = userService.getItemById(userId);
            String userName = "Usuario desconocido";

            if (userResponse.getBody() != null) {
                userName = userResponse.getBody().getUsername();
            }

            // Obtener tareas completadas del usuario en el sprint
            List<Task> tareasCompletadasUsuario = taskService.getCompletedTasksByUserAndSprintId(userId, sprintId);

            // Mostrar evidencia
            logger.info("Se encontraron {} tareas completadas por el usuario {} en el Sprint {}",
                    tareasCompletadasUsuario.size(), userName, sprintId);

            // Mostrar detalles de cada tarea completada
            for (Task tarea : tareasCompletadasUsuario) {
                logger.info("Tarea completada por {}: {}", userName, tarea.quickDescription());
            }

            if (tareasCompletadasUsuario.isEmpty()) {
                logger.info("El usuario {} no tiene tareas completadas en este sprint.", userName);
            }
        } catch (Exception e) {
            logger.error("Error al obtener tareas completadas del usuario en el sprint: {}", e.getMessage());
        }
    }
}