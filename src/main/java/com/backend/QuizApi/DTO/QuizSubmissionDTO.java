package com.backend.QuizApi.DTO;

import lombok.Data;
import java.util.List;

@Data
public class QuizSubmissionDTO {
    private Long quizId;
    private Long userId;
    private List<QuestionAnswerDTO> answers;
}
