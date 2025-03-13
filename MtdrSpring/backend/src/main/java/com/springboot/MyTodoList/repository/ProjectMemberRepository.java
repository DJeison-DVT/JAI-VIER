package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

@Repository
@Transactional
@EnableTransactionManagement
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Integer> {

    List<ProjectMember> findByProjectId(int projectId);

    List<ProjectMember> findByUserId(int userId);

    Optional<ProjectMember> findByProjectIdAndUserId(int projectId, int userId);

    void deleteByProjectIdAndUserId(int projectId, int userId);
}
