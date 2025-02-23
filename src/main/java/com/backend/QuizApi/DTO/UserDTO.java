package com.backend.QuizApi.DTO;
import lombok.Data;

@Data
public class UserDTO {
    private String email;
    private String name;
//    private Date createdAt;

    public UserDTO(String email, String name) {
        this.email = email;
        this.name = name;
//        this.createdAt = createdAt;
    }

}
