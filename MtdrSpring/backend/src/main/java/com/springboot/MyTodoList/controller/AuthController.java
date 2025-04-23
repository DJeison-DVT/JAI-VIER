package com.springboot.MyTodoList.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.MyTodoList.dto.LoginRequest;
import com.springboot.MyTodoList.dto.LoginResponse;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.security.JwtService;
import com.springboot.MyTodoList.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    // @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        UserDetails userDetails = userService.loadUserByUsername(request.getUsername());
        if (!userService.checkPassword(
                request.getPassword(),
                userDetails.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Generate JWT tokens
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        userService.generateLogin(userDetails.getUsername());

        return ResponseEntity.ok(new LoginResponse(accessToken, refreshToken));
    }

    // @CrossOrigin
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody String refreshToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userService.loadUserByUsername(username);

            if (!jwtService.isTokenValid(refreshToken, userDetails)) {
                return ResponseEntity.badRequest().build();
            }

            String newAccessToken = jwtService.generateToken(userDetails);
            userService.generateLogin(username);

            return ResponseEntity.ok(new LoginResponse(newAccessToken, refreshToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        try {
            String username = jwtService.extractUsername(token);
            UserDetails user = userService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, user)) {
                return ResponseEntity.ok().build();
            }
        } catch (Exception ignored) {
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        try {
            String username = jwtService.extractUsername(token);
            User user = userService.getUserByUsername(username).getBody();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            UserDetails userDetails = userService.loadUserByUsername(username);

            if (jwtService.isTokenValid(token, userDetails)) {
                return ResponseEntity.ok(user);
            }
        } catch (Exception ignored) {
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}