package com.springboot.MyTodoList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springboot.MyTodoList.dto.UserSummary;
import com.springboot.MyTodoList.model.Asignee;
import com.springboot.MyTodoList.model.AsigneeId;

import java.util.List;

import javax.transaction.Transactional;

@Repository
@Transactional
@EnableTransactionManagement
public interface AsigneeRepository extends JpaRepository<Asignee, AsigneeId> {
    List<Asignee> findById_TaskId(int taskId);

    List<Asignee> findById_UserId(int userId);

    @Query("SELECT new com.springboot.MyTodoList.dto.UserSummary(" +
            "  u.ID, u.username, u.full_name, u.email, u.phone" +
            ") " +
            "FROM Asignee a " +
            "JOIN a.user u " +
            "WHERE a.id.taskId = :taskId")
    List<UserSummary> findUserSummariesByTaskId(@Param("taskId") int taskId);
}
