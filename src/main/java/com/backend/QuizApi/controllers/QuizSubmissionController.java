package com.backend.QuizApi.controllers;

import com.backend.QuizApi.DTO.QuestionAnswerDTO;
import com.backend.QuizApi.DTO.QuizResultDTO;
import com.backend.QuizApi.DTO.QuizSubmissionDTO;
import com.backend.QuizApi.entities.QuizResult;
import com.backend.QuizApi.entities.QuizTest;
import com.backend.QuizApi.entities.User;
import com.backend.QuizApi.enums.QuestionType;
import com.backend.QuizApi.repositories.QuizResultRepository;
import com.backend.QuizApi.repositories.QuizTestRepository;
import com.backend.QuizApi.repositories.UserRepository;
import com.backend.QuizApi.services.QuizSubmission.QuizSubmissionService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quiz-submissions")
@CrossOrigin("*")
public class QuizSubmissionController {

    private final QuizSubmissionService quizSubmissionService;
    private final QuizTestRepository quizTestRepository;
    private static final Logger logger = LoggerFactory.getLogger(QuizSubmissionController.class);
    private final UserRepository userRepository;
    private final QuizResultRepository quizResultRepository;


    public QuizSubmissionController(QuizSubmissionService quizSubmissionService, 
                                    QuizTestRepository quizTestRepository,
                                    UserRepository userRepository,
                                    QuizResultRepository quizResultRepository) {
        this.quizSubmissionService = quizSubmissionService;
        this.quizTestRepository = quizTestRepository;
        this.userRepository = userRepository;
        this.quizResultRepository = quizResultRepository;
    }

    /**
     * Submit a completed quiz with user answers
     * 
     * @param submission The quiz submission with answers
     * @return QuizResultDTO containing scoring information
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmissionDTO submission) {
        logger.info("Received quiz submission for quiz ID: {}", submission.getQuizId());
        try {
            QuizResultDTO result = quizSubmissionService.submitQuiz(submission);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error processing quiz submission: ", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Get quiz results for a specific user
     * 
     * @param userId The ID of the user to get results for
     * @return List of QuizResultDTOs for the user
     */
    @GetMapping("/results/user/{userId}")
    public ResponseEntity<?> getUserResults(@PathVariable Long userId) {
        try {
            // Validate user exists
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
                
            // Get all results for this user
            List<QuizResult> userResults = quizResultRepository.findByUserId(userId);
            
            List<QuizResultDTO> resultDTOs = userResults.stream()
                .filter(result -> result.getQuizTest() != null) // Skip results with null quizTest
                .map(QuizResult::QuizResultDTO)
                .collect(Collectors.toList());
                
            return new ResponseEntity<>(resultDTOs, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error retrieving user quiz results: ", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get quiz results for a specific quiz
     * 
     * @param quizId The ID of the quiz to get results for
     * @return List of QuizResultDTOs for the quiz
     */
    @GetMapping("/results/quiz/{quizId}")
    public ResponseEntity<?> getQuizResults(@PathVariable Long quizId) {
        try {
            // Validate quiz exists
            QuizTest quiz = quizTestRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));
                
            // Get all results for this quiz
            List<QuizResult> quizResults = quizResultRepository.findByQuizTestId(quizId);
            
            List<QuizResultDTO> resultDTOs = quizResults.stream()
                .map(QuizResult::QuizResultDTO)
                .collect(Collectors.toList());
                
            return new ResponseEntity<>(resultDTOs, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error retrieving quiz results: ", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all quiz results across all users (for admin)
     * 
     * @return List of QuizResultDTOs for all users
     */
    @GetMapping("/results")
    public ResponseEntity<?> getAllResults() {
        try {
            List<QuizResult> allResults = quizResultRepository.findAll();
            
            List<QuizResultDTO> resultDTOs = allResults.stream()
                .filter(result -> result.getQuizTest() != null) // Skip results with null quizTest
                .map(QuizResult::QuizResultDTO)
                .collect(Collectors.toList());
                
            return new ResponseEntity<>(resultDTOs, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving all quiz results: ", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Generate a template for quiz submission
     */
    @GetMapping("/template/{quizId}")
    public ResponseEntity<?> getSubmissionTemplate(@PathVariable Long quizId) {
        try {
            QuizTest quiz = quizTestRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));
            
            List<QuestionAnswerDTO> templateAnswers = quiz.getQuestions().stream()
                .map(q -> {
                    QuestionAnswerDTO answerTemplate = new QuestionAnswerDTO();
                    answerTemplate.setQuestionId(q.getId());
                    
                    if (q.getType() == QuestionType.TEXT) {
                        answerTemplate.setTextAnswer("Your answer here");
                    } else {
                        // For MCQ or SINGLE, provide empty list that user should fill
                        answerTemplate.setSelectedOptionIds(new ArrayList<>());
                    }
                    
                    return answerTemplate;
                })
                .collect(Collectors.toList());
            
            QuizSubmissionDTO template = new QuizSubmissionDTO();
            template.setQuizId(quizId);
            template.setUserId(1L); // This is a placeholder - user should replace with actual user ID
            template.setAnswers(templateAnswers);
            
            return new ResponseEntity<>(template, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error generating submission template: ", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
