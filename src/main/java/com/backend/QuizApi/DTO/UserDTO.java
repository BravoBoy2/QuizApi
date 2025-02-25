package com.backend.QuizApi.DTO;
import com.backend.QuizApi.enums.UserRole;
import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String name;
//    private Date createdAt;
    private UserRole userRole;

    public UserDTO(String email, String name, UserRole userRole) {
        this.email = email;
        this.name = name;
        this.userRole = userRole;
//        this.createdAt = createdAt;
    }

}
