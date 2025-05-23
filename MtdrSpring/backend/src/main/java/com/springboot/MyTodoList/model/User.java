package com.springboot.MyTodoList.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.springboot.MyTodoList.dto.ProjectSummary;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @Column(name = "USERNAME")
    String username;
    @Column(name = "EMAIL")
    String email;
    @Column(name = "FULL_NAME")
    String full_name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "PASSWORD_HASH")
    String password_hash;
    @Column(name = "WORK_MODE")
    String work_mode;
    @Column(name = "CREATED_AT")
    OffsetDateTime created_at;
    @Column(name = "UPDATED_AT")
    OffsetDateTime updated_at;
    @Column(name = "LAST_LOGIN")
    OffsetDateTime last_login;
    @Column(name = "ACTIVE")
    boolean active;
    @ManyToOne
    @JoinColumn(name = "SELECTED_PROJECT_ID")
    private Project selected_project;
    @Transient
    private Integer selected_project_id;
    @Column(name = "CHAT_ID")
    private Long chatId;
    @Column(name = "PHONE")
    private String phone;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<ProjectMember> memberships = new HashSet<>();

    @Transient
    @JsonProperty("projects")
    public List<ProjectSummary> getProjects() {
        return memberships.stream()
                .map(pm -> {
                    Project p = pm.getProject();
                    return new ProjectSummary(
                            p.getID(),
                            p.getName(),
                            p.getDescription(),
                            p.getStart_date(),
                            p.getEnd_date(),
                            p.getStatus(),
                            p.getCreated_at(),
                            p.getUpdated_at());
                })
                .collect(Collectors.toList());
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Asignee> asignees = new HashSet<>();

    public User() {
    }

    public User(int ID, String username, String email, String full_name, String password_hash,
            String work_mode, OffsetDateTime created_at, OffsetDateTime updated_at, boolean active, Long chat_id,
            String phone) {
        this.ID = ID;
        this.username = username;
        this.email = email;
        this.full_name = full_name;
        this.password_hash = password_hash;
        this.work_mode = work_mode;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.active = active;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getWork_mode() {
        return work_mode;
    }

    public void setWork_mode(String work_mode) {
        this.work_mode = work_mode;
    }

    public OffsetDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(OffsetDateTime created_at) {
        this.created_at = created_at;
    }

    public OffsetDateTime getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(OffsetDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public OffsetDateTime getLast_login() {
        return last_login;
    }

    public void setLast_login(OffsetDateTime last_login) {
        this.last_login = last_login;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @JsonIgnore
    public Project getSelectedProject() {
        return selected_project;
    }

    public void setSelectedProject(Project selected_project) {
        this.selected_project = selected_project;
    }

    public Integer getSelectedProject_id() {
        return (selected_project != null) ? selected_project.getID() : null;
    }

    public void setSelectedProject_id(Integer selected_project_id) {
        this.selected_project_id = selected_project_id;
    }

    @JsonProperty("chat_id")
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", full_name='" + full_name + '\'' +
                ", password_hash='" + password_hash + '\'' +
                ", work_mode='" + work_mode + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", active=" + active +
                ", selected_project_id=" + (selected_project != null ? selected_project.getID() : "null") +
                ", chat_id='" + chatId + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }

    public String publicDescription() {
        return String.format("👤 *User Profile*\n" + "🆔 ID: %d\n" + "👤 Username: %s\n" + "📧 Email: %s\n"
                + "👨‍💼 Full Name: %s\n" + "🌍 Work Mode: %s\n" + ID, username, email, full_name, work_mode);
    }

    public String quickDescription() {
        return String.format("👤 %s, | 👨‍💼 %s", username, full_name);
    }
}