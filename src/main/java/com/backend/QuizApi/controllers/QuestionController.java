package com.backend.QuizApi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.backend.QuizApi.DTO.QuestionDTO;
import com.backend.QuizApi.services.Question.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin("*")
public class QuestionController {

    private final QuestionService questionService;
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping()
    public ResponseEntity<?> createQuestion(@RequestBody QuestionDTO questionDTO) {
        logger.info("Received request to create question: {}", questionDTO);
        logger.debug("QuestionDTO details: {}", questionDTO);
        try {
            QuestionDTO createdQuestion = questionService.createQuestion(questionDTO);
            logger.info("Successfully created question: {}", createdQuestion);
            return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating question: ", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
