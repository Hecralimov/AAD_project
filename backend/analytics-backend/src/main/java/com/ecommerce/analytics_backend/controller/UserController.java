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
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String id) {
        return userService.getUserById(id)
                .map(u -> ResponseEntity.ok(new UserResponseDTO(u.getId(), u.getEmail(), u.getRoleType())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody User user) {
        User u = userService.createUser(user);
        return ResponseEntity.ok(new UserResponseDTO(u.getId(), u.getEmail(), u.getRoleType()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable String id, @RequestBody User user) {
        User u = userService.updateUser(id, user);
        return ResponseEntity.ok(new UserResponseDTO(u.getId(), u.getEmail(), u.getRoleType()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/suspend")
    public ResponseEntity<UserResponseDTO> suspendUser(@PathVariable String id,
            @RequestBody java.util.Map<String, Boolean> payload) {
        Boolean suspended = payload.get("suspended");
        if (suspended == null) {
            suspended = true;
        }
        User u = userService.suspendUser(id, suspended);
        return ResponseEntity.ok(new UserResponseDTO(u.getId(), u.getEmail(), u.getRoleType()));
    }
}