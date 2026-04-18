package com.ecommerce.analytics_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.analytics_backend.model.User;
import com.ecommerce.analytics_backend.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // We added URL parameters so you can choose which page to view
    @GetMapping
    public Page<User> getAllUsers(
            @RequestParam(defaultValue = "0") int page, // Defaults to the first page (index 0)
            @RequestParam(defaultValue = "100") int size // Defaults to 100 users per page
    ) {
        // This creates a pagination request for MySQL
        Pageable pageable = PageRequest.of(page, size);

        // Spring Data JPA automatically adds "LIMIT" and "OFFSET" to your SQL query
        // behind the scenes!
        return userRepository.findAll(pageable);
    }
}