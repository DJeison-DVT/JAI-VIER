package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Comment;
import com.springboot.MyTodoList.model.Task;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.CommentRepository;
import com.springboot.MyTodoList.repository.TaskRepository;
import com.springboot.MyTodoList.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Comment> findAll() {
        List<Comment> comments = commentRepository.findAll();
        return comments;
    }

    public ResponseEntity<Comment> getItemById(int id) {
        Optional<Comment> commentData = commentRepository.findById(id);
        if (commentData.isPresent()) {
            return new ResponseEntity<>(commentData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<List<Comment>> getItemsByTaskId(int taskId) {
        List<Comment> comments = commentRepository.findByTask_ID(taskId);
        if (comments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(comments, HttpStatus.OK);
        }
    }

    public Comment addComment(Comment comment) {

        if (comment.getTask_id() == 0) {
            throw new IllegalArgumentException("Comment must be linked to an existing Task.");
        }

        if (comment.getUser_id() == 0) {
            throw new IllegalArgumentException("Comment must be linked to an existing User.");
        }

        Task existingTask = taskRepository.findById(comment.getTask_id()).orElseThrow(
                () -> new IllegalArgumentException("Task not found with ID: " +
                        comment.getTask_id()));

        User existingUser = userRepository.findById(comment.getUser_id()).orElseThrow(
                () -> new IllegalArgumentException("User not found with ID: " +
                        comment.getUser_id()));

        comment.setTask(existingTask);
        comment.setUser(existingUser);
        comment.setCreated_at(OffsetDateTime.now());

        return commentRepository.save(comment);
    }

    public boolean deleteComment(int id) {
        try {
            commentRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Comment updateComment(int id, Comment c) {
        Optional<Comment> commentData = commentRepository.findById(id);
        if (commentData.isPresent()) {
            Comment comment = commentData.get();
            comment.setID(id);
            comment.setContent(c.getContent());
            comment.setCreated_at(OffsetDateTime.now());
            return commentRepository.save(comment);
        } else {
            return null;
        }
    }
}
