package com.springboot.MyTodoList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
}
