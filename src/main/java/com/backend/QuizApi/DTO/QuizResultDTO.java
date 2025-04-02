package com.backend.QuizApi.DTO;

import lombok.Data;

@Data
public class QuizResultDTO {
    private long id;
    private long quizId;
    private long userId;
    private int totalQuestions;
    private int totalCorrectAnswers;
    private double percentageCorrAnswer;
    private String quizTitle; // Changed field name from QuizTitle to quizTitle
    private String userName; // Changed field name from 'name' to 'userName'
}
