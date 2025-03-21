package com.backend.QuizApi.controllers;

import com.backend.QuizApi.DTO.QuestionDTO;
import com.backend.QuizApi.DTO.QuizTestDTO;
import com.backend.QuizApi.entities.Question;
import com.backend.QuizApi.entities.QuizTest;
import com.backend.QuizApi.repositories.QuizTestRepository;
import com.backend.QuizApi.services.Quiz.QuizTestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin("*")
public class QuizTestController {

    private final QuizTestService quizTestService;
    private final QuizTestRepository quizTestRepository;

    public QuizTestController(QuizTestService quizTestService, QuizTestRepository quizTestRepository) {
        this.quizTestService = quizTestService;
        this.quizTestRepository = quizTestRepository;
    }

    @PostMapping()
    public ResponseEntity<?> createQuiz(@RequestBody QuizTestDTO quizTestDTO) {
        try {
            return new ResponseEntity<>(quizTestService.createQuizTest(quizTestDTO), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<?> addQuestionsToQuiz(@PathVariable Long quizId, @RequestBody List<QuestionDTO> questionDTOs) {
        try {
            return new ResponseEntity<>(quizTestService.addQuestionsToQuiz(quizId, questionDTOs), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllQuizzes() {
        try {
            return new ResponseEntity<>(quizTestService.getAllQuizzes(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieve a quiz with all its questions and options
     * 
     * @param id The quiz ID to retrieve
     * @return A QuizDetailsDTO containing the quiz and all its questions
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizWithQuestions(@PathVariable long id) {
        try {
            return new ResponseEntity<>(quizTestService.getAllQuestionsByQuiz(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get just the question IDs for a quiz (useful for creating submissions)
     */
    @GetMapping("/{id}/question-ids")
    public ResponseEntity<?> getQuizQuestionIds(@PathVariable long id) {
        try {
            QuizTest quiz = quizTestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + id));
            
            List<Long> questionIds = quiz.getQuestions().stream()
                .map(Question::getId)
                .collect(Collectors.toList());
            
            return new ResponseEntity<>(Map.of("quizId", id, "questionIds", questionIds), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Create multiple quizzes in a single batch request
     * 
     * @param quizTestDTOs List of quizzes to create
     * @return A list of created quizzes
     */
    @PostMapping("/batch")
    public ResponseEntity<?> createQuizzesBatch(@RequestBody List<QuizTestDTO> quizTestDTOs) {
        try {
            return new ResponseEntity<>(quizTestService.createQuizzesBatch(quizTestDTOs), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    /**
     * Add questions to multiple quizzes in a single batch request
     * 
     * @param batchData Map with quiz IDs as keys and lists of questions as values
     * @return A list of updated quizzes
     */
    @PostMapping("/batch/questions")
    public ResponseEntity<?> addQuestionsToBatchQuizzes(@RequestBody Map<Long, List<QuestionDTO>> batchData) {
        try {
            List<QuizTestDTO> results = new ArrayList<>();
            for (Map.Entry<Long, List<QuestionDTO>> entry : batchData.entrySet()) {
                results.add(quizTestService.addQuestionsToQuiz(entry.getKey(), entry.getValue()));
            }
            return new ResponseEntity<>(results, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}