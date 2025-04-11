package com.springboot.MyTodoList.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;

@Embeddable
public class ProjectMemberId implements Serializable {
    private int projectId;
    private int userId;

    // Default constructor (required by JPA)
    public ProjectMemberId() {
    }

    public ProjectMemberId(int projectId, int userId) {
        this.projectId = projectId;
        this.userId = userId;
    }

    // Getters and Setters
    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Override equals() and hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProjectMemberId that = (ProjectMemberId) o;
        return projectId == that.projectId && userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, userId);
    }
}
