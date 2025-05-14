package com.springboot.MyTodoList.repository;

import java.time.OffsetDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springboot.MyTodoList.model.Sprint;

@Repository
@Transactional
@EnableTransactionManagement
public interface SprintRepository extends JpaRepository<Sprint, Integer> {
    List<Sprint> findByProject_ID(int task_id);

    @Query("SELECT s FROM Sprint s WHERE s.project.ID = :projectId AND :now BETWEEN s.start_date AND s.end_date")
    List<Sprint> findActiveSprintsByProjectId(@Param("projectId") int projectId, @Param("now") OffsetDateTime now);

    List<Sprint> findDistinctByProject_Memberships_User_ID(Integer userId);
}