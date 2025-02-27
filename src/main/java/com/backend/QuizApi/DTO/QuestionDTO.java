package com.backend.QuizApi.DTO;

import com.backend.QuizApi.entities.Option;
import com.backend.QuizApi.entities.QuizTest;
import com.backend.QuizApi.enums.QuestionType;
import lombok.Data;

import java.util.List;

@Data
public class QuestionDTO {

    private long id;
    private String question;
    private QuestionType type;
    private QuizTest quiz;
    private List<Option> options;
    private String CorrectAnswer;


}
