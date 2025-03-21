package com.backend.QuizApi.DTO;

import lombok.Data;
import java.util.List;

@Data
public class QuestionAnswerDTO {
    private Long questionId;
    private String textAnswer;     // For text-type questions
    private List<Long> selectedOptionIds; // For MCQ and single-select questions
}
