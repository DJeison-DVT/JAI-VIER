package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.ProjectMember;
import com.springboot.MyTodoList.model.ProjectMemberId;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
