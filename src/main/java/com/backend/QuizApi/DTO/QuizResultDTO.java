package com.backend.QuizApi.DTO;

import lombok.Data;

@Data
public class QuizResultDTO {
    private Long id;
    private Long quizId;
    private Long userId;
    private int totalQuestions;
    private int totalCorrectAnswers;
    private double percentageCorrAnswer;

    // Default constructor
    public QuizResultDTO() {
    }

    // All-args constructor - fixed to match actual fields
    public QuizResultDTO(Long id, Long quizId, Long userId, int totalQuestions, 
                        int totalCorrectAnswers, double percentageCorrAnswer) {
        this.id = id;
        this.quizId = quizId;
        this.userId = userId;
        this.totalQuestions = totalQuestions;
        this.totalCorrectAnswers = totalCorrectAnswers;
        this.percentageCorrAnswer = percentageCorrAnswer;
    }
}
