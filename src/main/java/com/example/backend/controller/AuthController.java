package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired 
    private PasswordEncoder passwordEncoder;
    

    // Register a new user
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/signup")
    public String registerUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Encrypt the password
        user.setRole("ROLE_USER"); // Set default role
        userRepository.save(user); // Save the user to the database
        return "User registered successfully!";
    }

    // Sign in with email and password (Basic Authentication)
    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody User user) {
        try {
            // Authenticate user with provided credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            // Get the authenticated user details from SecurityContext
            User authenticatedUser = userRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Return a structured response with role
            Map<String, String> response = new HashMap<>();
            response.put("message", "User logged in successfully!");
            response.put("email", authenticatedUser.getEmail());
            response.put("role", authenticatedUser.getRole());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return error response for failed authentication
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials!");
        }
    }

}
