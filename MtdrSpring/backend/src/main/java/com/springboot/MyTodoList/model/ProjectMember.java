package com.springboot.MyTodoList.model;

import javax.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "PROJECT_MEMBER")
public class ProjectMember {

    @Transient
    @Column(name = "PROJECT_ID")
    int project_id;
    @Id
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    Project project;
    @Transient
    @Column(name = "USER_ID")
    int user_id;
    @Id
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    User user;
    @Column(name = "JOINED_DATE")
    OffsetDateTime joined_date;

    public ProjectMember() {
    }

    public ProjectMember(Project project, User user, OffsetDateTime joined_date) {
        this.project = project;
        this.user = user;
        this.joined_date = joined_date;
    }

    public int getProject_id() {
        return project_id;
    }

    public void setProject_id(int project_id) {
        this.project_id = project_id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
                "project=" + project +
                ", user=" + user +
                ", joined_date=" + joined_date +
                '}';
    }
}