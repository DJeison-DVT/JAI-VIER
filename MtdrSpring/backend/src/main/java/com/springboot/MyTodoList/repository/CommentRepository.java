package com.springboot.MyTodoList.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.springboot.MyTodoList.model.Comment;

import java.util.List;

import javax.transaction.Transactional;

@Repository
@Transactional
@EnableTransactionManagement
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByTask_ID(int taskId);
}
