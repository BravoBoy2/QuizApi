package com.backend.QuizApi.controllers;

import com.backend.QuizApi.DTO.QuestionDTO;
import com.backend.QuizApi.DTO.QuizDetailsDTO;
import com.backend.QuizApi.DTO.QuizTestDTO;
import com.backend.QuizApi.services.Quiz.QuizTestService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin("*")
public class QuizTestController {

    private final QuizTestService quizTestService;

    public QuizTestController(QuizTestService quizTestService) {
        this.quizTestService = quizTestService;
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
}