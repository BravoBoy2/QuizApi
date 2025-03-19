package com.backend.QuizApi.DTO;

import lombok.Data;

@Data
public class QuizResultDTO {
    private long id;

    private int totalQuestions;

    private int totalCorrectAnswers;

    private double percentageCorrAnswer;

    private String QuizTitle;

    private String userName;
}
