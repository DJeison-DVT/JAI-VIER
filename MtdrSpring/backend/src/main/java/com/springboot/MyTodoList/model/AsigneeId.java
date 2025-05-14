package com.springboot.MyTodoList.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Embeddable
public class AsigneeId implements Serializable {
    private int taskId;
    private int userId;

    public AsigneeId() {
    }

    public AsigneeId(int taskId, int userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    @JsonIgnore
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @JsonIgnore
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AsigneeId that = (AsigneeId) o;
        return taskId == that.taskId && userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, userId);
    }
}
