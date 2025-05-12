package com.springboot.MyTodoList.dto;

public class UserSummary {
    private final Integer id;
    private final String username;
    private final String fullName;
    private final String email;
    private final String phone;

    public UserSummary(Integer id, String username, String fullName, String email, String phone) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
