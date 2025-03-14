package com.springboot.MyTodoList.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

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
    @Column(name = "ROLE")
    String role;
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

    public User() {
    }

    public User(int ID, String username, String email, String full_name, String password_hash, String role,
            String work_mode, OffsetDateTime created_at, OffsetDateTime updated_at, boolean active) {
        this.ID = ID;
        this.username = username;
        this.email = email;
        this.full_name = full_name;
        this.password_hash = password_hash;
        this.role = role;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", full_name='" + full_name + '\'' +
                ", password_hash='" + password_hash + '\'' +
                ", role='" + role + '\'' +
                ", work_mode='" + work_mode + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", active=" + active +
                '}';
    }

    public String publicDescription() {
        return String.format("👤 *User Profile*\n" + "🆔 ID: %d\n" + "👤 Username: %s\n" + "📧 Email: %s\n"
                + "👨‍💼 Full Name: %s\n" + "🛠 Role: %s\n" + "🌍 Work Mode: %s\n" + ID, username, email, full_name,
                role, work_mode);
    }

    public String quickDescription() {
        return String.format("👤 %s, | 👨‍💼 %s | 🛠 %s", username, full_name, role);
    }
}