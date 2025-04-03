package com.springboot.MyTodoList.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "TASK_COMMENT")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @Column(name = "CONTENT")
    String content;
    @Column(name = "CREATED_AT")
    OffsetDateTime created_at;

    @JsonBackReference(value = "task-comments")
    @ManyToOne
    @JoinColumn(name = "TASK_ID", nullable = false)
    private Task task;
    @Transient
    private int task_id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
    @Transient
    private int user_id;

    public Comment() {
    }

    public Comment(int ID, String content, OffsetDateTime created_at, Task task, User user) {
        this.ID = ID;
        this.content = content;
        this.created_at = created_at;
        this.task = task;
        this.user = user;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OffsetDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(OffsetDateTime created_at) {
        this.created_at = created_at;
    }

    @JsonIgnore
    public Task getTask() {
        return task;
    }

    @JsonIgnore
    public void setTask(Task task) {
        this.task = task;
    }

    public int getTask_id() {
        return task != null ? task.getID() : task_id;
    }

    public void setTask_id(int task_id) {
        this.task_id = task_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUser_id() {
        return user != null ? user.getID() : user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "ID=" + ID +
                ", content='" + content + '\'' +
                ", created_at=" + created_at +
                ", task_id=" + task_id +
                ", user_id=" + user_id +
                '}';
    }

    public String description() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return String.format(
                "📝 %s\n" +
                        "   🆔 ID: %d | 📌 Sent by: %s | Made by: %s\n",
                content, ID, user.getUsername(), created_at.format(formatter));
    }
}
