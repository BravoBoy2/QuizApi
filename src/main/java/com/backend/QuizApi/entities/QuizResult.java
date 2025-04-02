package com.backend.QuizApi.entities;

import com.backend.QuizApi.DTO.QuizResultDTO;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class QuizResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int totalQuestions;

    private int totalCorrectAnswers;

    private double percentageCorrAnswer;

    @ManyToOne
    @JoinColumn(name = "quiz_test_id")
    private QuizTest quizTest;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public QuizResultDTO QuizResultDTO() {
        QuizResultDTO quizResultDTO = new QuizResultDTO();

        quizResultDTO.setId(id);
        quizResultDTO.setQuizId(quizTest.getId());
        quizResultDTO.setUserId(user.getId());
        quizResultDTO.setTotalQuestions(totalQuestions);
        quizResultDTO.setTotalCorrectAnswers(totalCorrectAnswers);
        quizResultDTO.setPercentageCorrAnswer(percentageCorrAnswer);
        quizResultDTO.setQuizTitle(quizTest.getTitle());
        quizResultDTO.setUserName(user.getName());

        return quizResultDTO;
    }
}
