package com.backend.QuizApi.DTO;

import lombok.Data;

import java.sql.Time;

@Data
public class QuizTestDTO {
    private long id;
    private String title;
    private String description;
    private Time time;
}
