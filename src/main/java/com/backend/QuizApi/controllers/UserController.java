package com.backend.QuizApi.controllers;

import com.backend.QuizApi.entities.User;
import com.backend.QuizApi.services.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> registerUser(@Validated @RequestBody User user) {

        if(user.getEmail() == null
                || user.getEmail().isEmpty()
                || user.getPassword() == null
                || user.getPassword().isEmpty())
        {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.BAD_REQUEST);
        }

        if (userService.hasUserWithEmail(user.getEmail())) {
            return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
        }

        User newUser = userService.createUser(user);
        if (newUser == null) {
            return new ResponseEntity<>("User creation failed", HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
