package com.backend.QuizApi.services.Quiz;

import com.backend.QuizApi.DTO.QuizTestDTO;
import com.backend.QuizApi.DTO.QuestionDTO;

import java.util.List;

public interface QuizTestService {

    QuizTestDTO createQuizTest(QuizTestDTO quizTestDTO);

    List<QuizTestDTO> getAllQuizzes();

    QuizTestDTO addQuestionsToQuiz(Long quizId, List<QuestionDTO> questionDTOs);
}
