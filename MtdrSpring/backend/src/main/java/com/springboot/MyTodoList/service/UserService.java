package com.springboot.MyTodoList.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.MyTodoList.model.Project;
import com.springboot.MyTodoList.model.ProjectMember;
import com.springboot.MyTodoList.model.User;
import com.springboot.MyTodoList.repository.ProjectRepository;
import com.springboot.MyTodoList.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectMemberService projectMemberService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void linkPhoneWithChatId(long chat_id, String phone) {
        Optional<User> userData = userRepository.findByPhone(phone);
        if (userData.isPresent()) {
            User existingUser = userData.get();
            existingUser.setChatId(chat_id);
            userRepository.save(existingUser);
        } else {
            throw new NoSuchElementException("No user found with phone number: " + phone);
        }
    }

    private boolean userExists(String username, String email, String phone, Long chat_id) {
        if (userRepository.findByUsername(username).isPresent()) {
            return true;
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return true;
        }
        if (phone != null && userRepository.findByPhone(phone).isPresent()) {
            return true;
        }
        if (chat_id != null && userRepository.findByChatId(chat_id).isPresent()) {
            return true;
        }

        return false;
    }

    private String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    public List<User> findAll() {
        List<User> users = userRepository.findAll();
        return users;
    }

    public ResponseEntity<User> getItemById(int id) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<User> getUserByPhone(String phone) {
        Optional<User> userData = userRepository.findByPhone(phone);
        if (userData.isPresent()) {
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<User> getUserByChatId(Long chat_id) {
        if (chat_id == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        System.out.println("Fetching user by chat_id: " + chat_id);
        Optional<User> userData = userRepository.findByChatId(chat_id);
        System.out.println("User data: " + userData);
        if (userData.isPresent()) {
            return new ResponseEntity<>(userData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public User addUser(User user) {
        if (userExists(user.getUsername(), user.getEmail(), user.getPhone(), user.getChatId())) {
            throw new IllegalArgumentException("User with the same username, email, phone or chat_id already exists.");
        }
        user.setPassword_hash(hashPassword(user.getPassword_hash()));
        user.setCreated_at(OffsetDateTime.now());
        user.setUpdated_at(OffsetDateTime.now());
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User u = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user: " + username));

        List<GrantedAuthority> auths = new ArrayList<>();
        List<ProjectMember> projectMemberships = projectMemberService.getProjectMembersByUserId(u.getID());
        // extract roles from project memberships

        return org.springframework.security.core.userdetails.User.withUsername(u.getUsername())
                .password(u.getPassword_hash())
                .authorities(auths)
                .credentialsExpired(false).disabled(!u.isActive()).build();

    }

    public boolean deleteUser(int id) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            User existingUser = userData.get();
            existingUser.setActive(false);
            userRepository.save(existingUser);
            return true;
        } else {
            return false;
        }
    }

    public User updateUser(int id, User user) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            User existingUser = userData.get();
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setFull_name(user.getFull_name());
            existingUser.setWork_mode(user.getWork_mode());
            existingUser.setChatId(user.getChatId());
            existingUser.setPhone(user.getPhone());
            existingUser.setUpdated_at(OffsetDateTime.now());
            existingUser.setActive(user.isActive());
            if (user.getPassword_hash() != null && !user.getPassword_hash().isEmpty()) {
                existingUser.setPassword_hash(hashPassword(user.getPassword_hash()));
            }
            Integer project_id = user.getSelectedProject_id();
            if (project_id != null) {
                Optional<Project> projectData = projectRepository.findById(project_id);
                if (projectData.isPresent()) {
                    // TODO Check if is project member
                    Project existingProject = projectData.get();
                    existingUser.setSelectedProject(existingProject);
                } else {
                    throw new IllegalArgumentException("Project not found with ID: " + project_id);
                }
            }
            return userRepository.save(existingUser);
        } else {
            return null;
        }
    }
}
