package com.springboot.MyTodoList.model;

import javax.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "PROJECT_MEMBER")
public class ProjectMember {
    @EmbeddedId
    protected ProjectMemberId id;

    @Column(name = "JOINED_DATE")
    private OffsetDateTime joined_date;

    public ProjectMember() {
    }

    public ProjectMember(int project_id, int user_id, OffsetDateTime joined_date) {
        this.id = new ProjectMemberId(project_id, user_id); // ✅ Initialize ID
        this.joined_date = joined_date;
    }

    public ProjectMemberId getId() {
        return id;
    }

    public void setId(ProjectMemberId id) {
        this.id = id;
    }

    public int getProject_id() {
        return id.getProjectId();
    }

    public void setProject_id(int project_id) {
        this.id.setProjectId(project_id);
    }

    public int getUser_id() {
        return id.getUserId();
    }

    public void setUser_id(int user_id) {
        this.id.setUserId(user_id);
    }

    public OffsetDateTime getJoined_date() {
        return joined_date;
    }

    public void setJoined_date(OffsetDateTime joined_date) {
        this.joined_date = joined_date;
    }

    @Override
    public String toString() {
        return "ProjectMember{" +
                "project=" + id.getProjectId() +
                ", user=" + id.getUserId() +
                ", joined_date=" + joined_date +
                '}';
    }
}
