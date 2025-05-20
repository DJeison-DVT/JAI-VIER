package com.springboot.MyTodoList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.service.TaskService;
import com.springboot.MyTodoList.service.UserService;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Demo runner para ejecutar durante el arranque de la aplicación.
 * Esta clase demostrará las funcionalidades requeridas sin necesidad de
 * ejecutar tests.
 */
@Component
public class DemoRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DemoRunner.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("===================================================");
        logger.info("INICIANDO DEMOSTRACIÓN DE FUNCIONALIDADES");
        logger.info("===================================================");

        // Demo 1: Crear tarea
        demostrarCreacionTarea();

        // Demo 2: Ver tareas completadas de un sprint
        demostrarTareasCompletadasSprint(1); // Ajustar ID del sprint según BD

        // Demo 3: Ver tareas completadas de un usuario en un sprint
        demostrarTareasCompletadasUsuarioSprint(1, 1); // Ajustar IDs según BD

        logger.info("===================================================");
        logger.info("FIN DE LA DEMOSTRACIÓN");
        logger.info("===================================================");
    }

    /**
     * Demuestra la creación de una tarea
     */
    private void demostrarCreacionTarea() {
        logger.info("=== DEMOSTRACIÓN 1: CREAR TAREA ===");

        // Crear objeto de tarea para la demostración
        Task nuevaTarea = new Task();
        nuevaTarea.setTitle("Tarea de Demostración");
        nuevaTarea.setDescription("Esta es una tarea creada para la demostración");
        nuevaTarea.setDue_date(OffsetDateTime.now().plusDays(7)); // Fecha límite en 7 días
        nuevaTarea.setPriority(2); // Prioridad media
        nuevaTarea.setStatus(0); // Estado: TODO
        nuevaTarea.setEstimated_hours(8);
        nuevaTarea.setSprint_id(1); // Ajustar según el ID de sprint disponible en la BD

        try {
            // Guardar la tarea
            Task tareaGuardada = taskService.addTask(nuevaTarea);

            // Mostrar evidencia de que la tarea fue creada
            logger.info("✅ Tarea creada exitosamente con ID: {}", tareaGuardada.getID());
            logger.info("Detalles de la tarea: {}", tareaGuardada.quickDescription());
        } catch (Exception e) {
            logger.error("❌ Error al crear la tarea: {}", e.getMessage());
        }
    }

    /**
     * Demuestra la visualización de tareas completadas de un sprint
     * 
     * @param sprintId ID del sprint
     */
    private void demostrarTareasCompletadasSprint(int sprintId) {
        logger.info("=== DEMOSTRACIÓN 2: VER TAREAS COMPLETADAS DE UN SPRINT ===");

        try {
            // Obtener tareas completadas del sprint
            List<Task> tareasCompletadas = taskService.getCompletedTasksBySprintId(sprintId);

            // Mostrar evidencia
            logger.info("Se encontraron {} tareas completadas en el Sprint {}",
                    tareasCompletadas.size(), sprintId);

            // Mostrar detalles de cada tarea completada
            if (!tareasCompletadas.isEmpty()) {
                logger.info("Lista de tareas completadas:");
                for (Task tarea : tareasCompletadas) {
                    logger.info("  → {}", tarea.quickDescription());
                }
            } else {
                logger.info("⚠️ No hay tareas completadas en este sprint.");

                // Crear una tarea de ejemplo y marcarla como completada para demostración
                Task tareaDemo = new Task();
                tareaDemo.setTitle("Tarea Demo para Sprint");
                tareaDemo.setDescription("Tarea creada para demostrar las tareas completadas");
                tareaDemo.setDue_date(OffsetDateTime.now());
                tareaDemo.setPriority(2);
                tareaDemo.setStatus(3); // COMPLETED
                tareaDemo.setEstimated_hours(4);
                tareaDemo.setSprint_id(sprintId);

                try {
                    Task tareaGuardada = taskService.addTask(tareaDemo);
                    logger.info("✅ Tarea de demostración creada y marcada como completada: {}",
                            tareaGuardada.quickDescription());

                    // Volver a consultar
                    tareasCompletadas = taskService.getCompletedTasksBySprintId(sprintId);
                    logger.info("Después de crear tarea: Se encontraron {} tareas completadas",
                            tareasCompletadas.size());
                } catch (Exception e) {
                    logger.error("❌ No se pudo crear la tarea de demostración: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("❌ Error al obtener tareas completadas del sprint: {}", e.getMessage());
        }
    }

    /**
     * Demuestra la visualización de tareas completadas de un usuario en un sprint
     * 
     * @param userId   ID del usuario
     * @param sprintId ID del sprint
     */
    private void demostrarTareasCompletadasUsuarioSprint(int userId, int sprintId) {
        logger.info("=== DEMOSTRACIÓN 3: VER TAREAS COMPLETADAS DE UN USUARIO EN UN SPRINT ===");

        try {
            // Obtener información del usuario
            User usuario = null;
            try {
                usuario = userService.getItemById(userId).getBody();
            } catch (Exception e) {
                logger.warn("⚠️ No se pudo obtener información del usuario: {}", e.getMessage());
            }

            String nombreUsuario = (usuario != null) ? usuario.getUsername() : "Usuario ID " + userId;

            // Obtener tareas completadas del usuario en el sprint
            List<Task> tareasCompletadasUsuario = taskService.getCompletedTasksByUserAndSprintId(userId, sprintId);

            // Mostrar evidencia
            logger.info("Se encontraron {} tareas completadas por el usuario {} en el Sprint {}",
                    tareasCompletadasUsuario.size(), nombreUsuario, sprintId);

            // Mostrar detalles de cada tarea completada
            if (!tareasCompletadasUsuario.isEmpty()) {
                logger.info("Lista de tareas completadas por el usuario:");
                for (Task tarea : tareasCompletadasUsuario) {
                    logger.info("  → {}", tarea.quickDescription());
                }
            } else {
                logger.info("⚠️ El usuario {} no tiene tareas completadas en este sprint.", nombreUsuario);
                logger.info("  Nota: Para ver esta funcionalidad en acción, asigne una tarea a este usuario,");
                logger.info("  márquela como completada y ejecute la aplicación nuevamente.");
            }
        } catch (Exception e) {
            logger.error("❌ Error al obtener tareas completadas del usuario en el sprint: {}", e.getMessage());
        }
    }
}