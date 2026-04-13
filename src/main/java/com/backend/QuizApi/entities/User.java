package com.backend.QuizApi.entities;

import com.backend.QuizApi.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;
    private String password;
    private String name;
    private UserRole role;
}
