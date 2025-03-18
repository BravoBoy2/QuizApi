package com.backend.QuizApi.services.Quiz;

import com.backend.QuizApi.DTO.QuestionDTO;
import com.backend.QuizApi.DTO.QuizDetailsDTO;
import com.backend.QuizApi.DTO.QuizTestDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuizTestService {
    
    /**
     * Creates a new quiz test
     * 
     * @param quizTestDTO The DTO containing quiz test information
     * @return The created quiz test as DTO
     */
    QuizTestDTO createQuizTest(QuizTestDTO quizTestDTO);
    
    /**
     * Retrieves all quiz tests
     * 
     * @return List of all quiz tests as DTOs
     */
    List<QuizTestDTO> getAllQuizzes();
    
    /**
     * Adds questions to an existing quiz
     * 
     * @param quizId The ID of the quiz to update
     * @param questionDTOs List of question DTOs to add to the quiz
     * @return The updated quiz test as DTO
     */
    @Transactional
    QuizTestDTO addQuestionsToQuiz(Long quizId, List<QuestionDTO> questionDTOs);

    /**
     * Retrieves a quiz with all its questions and options
     * 
     * @param id The ID of the quiz to retrieve
     * @return QuizDetailsDTO containing the quiz and all its questions
     */
    QuizDetailsDTO getAllQuestionsByQuiz(long id);
    
    /**
     * Creates a batch of new quiz tests
     * 
     * @param quizTestDTOs The list of DTOs containing quiz test information
     * @return List of created quiz tests as DTOs
     */
    List<QuizTestDTO> createQuizzesBatch(List<QuizTestDTO> quizTestDTOs);
}
