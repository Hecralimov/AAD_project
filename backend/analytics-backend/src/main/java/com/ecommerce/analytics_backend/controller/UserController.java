package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.dto.UserResponseDTO;
import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers()
                .stream()
                .map(u -> new UserResponseDTO(u.getId(), u.getEmail(), u.getRoleType()))
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<User> suspendUser(@PathVariable String id,
            @RequestBody java.util.Map<String, Boolean> payload) {
        Boolean suspended = payload.get("suspended");
        if (suspended == null) {
            suspended = true;
        }
        return ResponseEntity.ok(userService.suspendUser(id, suspended));
    }
}