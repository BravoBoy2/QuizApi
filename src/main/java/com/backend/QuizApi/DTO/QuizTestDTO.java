package com.backend.QuizApi.DTO;

import lombok.Data;
import java.sql.Time;
import java.util.List;

@Data
public class QuizTestDTO {
    private long id;
    private String title;
    private String description;
    private Time time;
    private List<QuestionDTO> questions;
}
