package com.springboot.MyTodoList.typeBuilder;

import com.springboot.MyTodoList.model.User;
import java.util.Map;

public class UserBuilder implements TypeBuilder<User> {
    @Override
    public User build(Map<String, String> fields) throws IllegalArgumentException {
        User user = new User();

        if (!fields.containsKey("username")) {
            throw new IllegalArgumentException("username is required");
        }
        user.setUsername(fields.get("username"));

        if (!fields.containsKey("email")) {
            throw new IllegalArgumentException("Email is required");
        }
        user.setEmail(fields.get("email"));

        if (!fields.containsKey("password")) {
            throw new IllegalArgumentException("Password is required");
        }
        user.setPassword_hash(fields.get("password"));

        if (!fields.containsKey("full_name")) {
            throw new IllegalArgumentException("Status is required");
        }
        user.setFull_name(fields.get("full_name"));

        user.setWork_mode(fields.getOrDefault("work_mode", "NORMAL"));

        return user;
    }
}
