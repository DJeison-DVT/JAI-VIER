package com.springboot.MyTodoList.model;

import javax.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ASSIGNEE")
public class Asignee {
    @EmbeddedId
    protected AsigneeId id;

    @Column(name = "CREATED_AT")
    private OffsetDateTime created_at;

    @ManyToOne()
    @MapsId("taskId")
    @JoinColumn(name = "TASK_ID")
    private Task task;

    @ManyToOne()
    @MapsId("userId")
    @JoinColumn(name = "USER_ID")
    private User user;

    public Asignee() {
    }

    public Asignee(int task_id, int user_id, OffsetDateTime created_at) {
        this.id = new AsigneeId(task_id, user_id);
        this.created_at = created_at;
    }

    public AsigneeId getId() {
        return id;
    }

    public void setId(AsigneeId id) {
        this.id = id;
    }

    public int getTask_id() {
        return id.getTaskId();
    }

    public void setTask_id(int task_id) {
        this.id.setTaskId(task_id);
    }

    public int getUser_id() {
        return id.getUserId();
    }

    public void setUser_id(int user_id) {
        this.id.setUserId(user_id);
    }

    public OffsetDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(OffsetDateTime created_at) {
        this.created_at = created_at;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Asignee{" +
                "task_id=" + id.getTaskId() +
                ", user_id=" + id.getUserId() +
                ", created_at=" + created_at +
                '}';
    }
}