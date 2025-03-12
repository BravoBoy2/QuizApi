package com.backend.QuizApi.DTO;

import java.util.List;

import lombok.Data;

@Data
public class QuizDetailsDTO {
    private QuizTestDTO quizTestDTO;

    private List<QuestionDTO> questions;

}
