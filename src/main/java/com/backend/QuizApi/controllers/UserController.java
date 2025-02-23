package com.backend.QuizApi.controllers;

import com.backend.QuizApi.DTO.UserDTO;
import com.backend.QuizApi.entities.User;
import com.backend.QuizApi.services.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/")
public class UserController {

    private final UserService userService;
//    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService) {

        this.userService = userService;
    }

    //creating new from the api request
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        if (user.getEmail() == null || user.getEmail().isEmpty() ||
                user.getPassword() == null || user.getPassword().isEmpty()) {
            response.put("message", "Invalid email or password");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        if (userService.hasUserWithEmail(user.getEmail())) {
            response.put("message", "User already exists");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        User newUser = userService.createUser(user);
        if (newUser == null) {
            response.put("message", "User creation failed");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        // Convert User entity to DTO
        UserDTO userDTO = new UserDTO(newUser.getEmail(), newUser.getName());

        response.put("message", "User created successfully");
        response.put("user", userDTO); // Send only safe fields

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
