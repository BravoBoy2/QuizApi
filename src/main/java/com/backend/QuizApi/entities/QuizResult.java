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
        QuizResultDTO QuizResultDTO = new QuizResultDTO();

        QuizResultDTO.setId(id);
        QuizResultDTO.setTotalQuestions(totalQuestions);
        QuizResultDTO.setTotalCorrectAnswers(totalCorrectAnswers);
        QuizResultDTO.setPercentageCorrAnswer(percentageCorrAnswer);
        QuizResultDTO.setQuizTitle(quizTest.getTitle());
        QuizResultDTO.setUserName(user.getName());

        return QuizResultDTO;

    }
}
