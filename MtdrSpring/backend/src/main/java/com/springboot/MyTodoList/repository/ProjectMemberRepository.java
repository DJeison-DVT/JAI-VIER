package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.dto.ProjectSummary;
import com.springboot.MyTodoList.model.ProjectMember;
import com.springboot.MyTodoList.model.ProjectMemberId;

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
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
        List<ProjectMember> findById_ProjectId(int projectId);

        List<ProjectMember> findById_UserId(int userId);

        @Query("SELECT new com.springboot.MyTodoList.dto.ProjectSummary(" +
                        "  p.ID, p.name, p.description, " +
                        "  p.start_date, p.end_date, p.status, " +
                        "  p.created_at, p.updated_at" +
                        ") " +
                        "FROM ProjectMember pm " +
                        "JOIN pm.project p ")
        List<ProjectSummary> findAllProjectSummaries();

        @Query("SELECT new com.springboot.MyTodoList.dto.ProjectSummary(" +
                        "  p.ID, p.name, p.description, " +
                        "  p.start_date, p.end_date, p.status, " +
                        "  p.created_at, p.updated_at" +
                        ") " +
                        "FROM ProjectMember pm " +
                        "JOIN pm.project p " +
                        "WHERE pm.id.userId = :userId")
        List<ProjectSummary> findSummariesByUserId(@Param("userId") int userId);

}
