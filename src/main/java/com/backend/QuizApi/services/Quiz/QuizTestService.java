package com.backend.QuizApi.services.Quiz;

import com.backend.QuizApi.DTO.QuizTestDTO;

import java.util.List;

public interface QuizTestService {

    QuizTestDTO createQuizTest(QuizTestDTO quizTestDTO);

//    QuestionDTO createQuestionInQuiz(QuestionDTO questionDTO);

    //Listing quizzes with time for each questions within the quiz
   List<QuizTestDTO> getAllQuizzes();
}
