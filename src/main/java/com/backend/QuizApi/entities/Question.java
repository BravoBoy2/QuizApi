package com.backend.QuizApi.entities;

import com.backend.QuizApi.DTO.QuestionDTO;
import com.backend.QuizApi.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(columnDefinition = "TEXT")
    private String questionText; // Question text - this is the only field we should have for the question text

    @Enumerated(EnumType.STRING)
    private QuestionType type; //Type of question

    @ManyToOne
    @JoinColumn(name = "quiz_test_id")
    private QuizTest quiz;

    @OneToMany(mappedBy = "question",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Option> options;   //Possible answer for MCQ and & single type

    private String correctAnswer; // Answer for TEXT type (if applicable)

    public QuestionDTO getQuestionDTO() {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(id);
        questionDTO.setQuestionText(questionText);
        questionDTO.setType(type);
        questionDTO.setCorrectAnswer(correctAnswer);
        questionDTO.setOptions(options);
        questionDTO.setQuiz(quiz);

        return questionDTO;
    }
}
