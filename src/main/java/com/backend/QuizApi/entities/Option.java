package com.backend.QuizApi.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Option {


/*
* Entity class for Quiz options
* */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String answerText; //answer text


    private boolean isCorrect; //For MCQ/Single questions


    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;
}
