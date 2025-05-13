package com.springboot.MyTodoList.security;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.UserRepository;
import com.springboot.MyTodoList.service.ProjectMemberService;

@Component
public class AuthContext {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ProjectMemberService projectMemberService;

    public AuthContext(JwtService jwtService,
            UserRepository userRepository, ProjectMemberService projectMemberService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.projectMemberService = projectMemberService;
    }

    /**
     * Strip "Bearer " and return the raw JWT.
     */
    public String extractToken(String bearerHeader) {
        if (!StringUtils.hasText(bearerHeader) ||
                !bearerHeader.toLowerCase().startsWith("bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header.");
        }
        return bearerHeader.substring(7);
    }

    /**
     * Pulls the userId claim out of the token.
     */
    public Integer extractUserId(String bearerHeader) {
        String token = extractToken(bearerHeader);
        return jwtService.extractClaim(
                token,
                claims -> claims.get("userId", Integer.class));
    }

    /**
     * Fetches your domain AppUser by the ID in the JWT.
     */
    public User getCurrentUser(String bearerHeader) {
        Integer userId = extractUserId(bearerHeader);
        System.out.println("User ID: " + userId);
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        return user.get();
    }

    // implement whitelist check
    public boolean isAdmin(User user) {
        return true;
    }

    public boolean isMember(Integer userId, Integer projectId) {
        return projectMemberService.isMember(userId, projectId);
    }
}
