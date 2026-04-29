package com.ecommerce.analytics_backend.controller;

import com.ecommerce.analytics_backend.dto.UserResponseDTO;
import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 5), 100);

        PageRequest pageRequest = PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(userService.getUsers(pageRequest).map(this::toDto));
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
        return ResponseEntity.ok(toDto(userService.suspendUser(id, suspended)));
    }

    private UserResponseDTO toDto(User user) {
        return new UserResponseDTO(user.getId(), user.getEmail(), user.getRoleType(), user.isEnabled());
    }
}
