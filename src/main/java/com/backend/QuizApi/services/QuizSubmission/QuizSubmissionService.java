package com.backend.QuizApi.services.QuizSubmission;

import com.backend.QuizApi.DTO.QuizResultDTO;
import com.backend.QuizApi.DTO.QuizSubmissionDTO;

public interface QuizSubmissionService {
    /**
     * Process a quiz submission, grade it, and store results
     * 
     * @param submission The quiz submission with answers
     * @return QuizResultDTO containing the results and scoring information
     */
    QuizResultDTO submitQuiz(QuizSubmissionDTO submission);
}
