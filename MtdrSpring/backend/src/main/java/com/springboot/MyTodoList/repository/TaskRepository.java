package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

import javax.transaction.Transactional;

@Repository
@Transactional
@EnableTransactionManagement
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findDistinctBySprint_Project_Memberships_User_ID(Integer userId);

    List<Task> findDistinctBySprint_Project_ID(Integer projectId);

    /**
     * Encuentra todas las tareas completadas de un sprint específico
     * 
     * @param sprintId ID del sprint
     * @param status   Estado de la tarea (DONE para completadas)
     * @return Lista de tareas completadas del sprint
     */
    @Query("SELECT t FROM Task t WHERE t.sprint.ID = :sprintId AND t.status = :status")
    List<Task> findBySprint_IDAndStatusEquals(@Param("sprintId") Integer sprintId, @Param("status") String status);

    /**
     * Encuentra todas las tareas completadas de un usuario específico en un sprint
     * específico
     * 
     * @param userId   ID del usuario
     * @param sprintId ID del sprint
     * @param status   Estado de la tarea (DONE para completadas)
     * @return Lista de tareas completadas del usuario en el sprint
     */
    @Query("SELECT t FROM Task t JOIN t.asignees a WHERE a.user.ID = :userId AND t.sprint.ID = :sprintId AND t.status = :status")
    List<Task> findByAsignees_User_IDAndSprint_IDAndStatusEquals(@Param("userId") Integer userId,
            @Param("sprintId") Integer sprintId, @Param("status") String status);
}