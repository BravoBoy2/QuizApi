package com.backend.QuizApi.controllers;

import com.backend.QuizApi.DTO.QuestionAnswerDTO;
import com.backend.QuizApi.DTO.QuizResultDTO;
import com.backend.QuizApi.DTO.QuizSubmissionDTO;
import com.backend.QuizApi.entities.Option;
import com.backend.QuizApi.entities.QuizTest;
import com.backend.QuizApi.enums.QuestionType;
import com.backend.QuizApi.repositories.QuizTestRepository;
import com.backend.QuizApi.services.QuizSubmission.QuizSubmissionService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/quiz-submissions")
@CrossOrigin("*")
public class QuizSubmissionController {

    private final QuizSubmissionService quizSubmissionService;
    private final QuizTestRepository quizTestRepository;
    private static final Logger logger = LoggerFactory.getLogger(QuizSubmissionController.class);

    public QuizSubmissionController(QuizSubmissionService quizSubmissionService, 
                                    QuizTestRepository quizTestRepository) {
        this.quizSubmissionService = quizSubmissionService;
        this.quizTestRepository = quizTestRepository;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(@RequestBody QuizSubmissionDTO submission) {
        try {
            QuizResultDTO result = quizSubmissionService.submitQuiz(submission);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error processing quiz submission: ", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/results/user/{userId}")
    public ResponseEntity<?> getUserResults(@PathVariable Long userId) {
        return new ResponseEntity<>("User quiz results endpoint - to be implemented", HttpStatus.OK);
    }
    
    @GetMapping("/results/quiz/{quizId}")
    public ResponseEntity<?> getQuizResults(@PathVariable Long quizId) {
        return new ResponseEntity<>("Quiz results endpoint - to be implemented", HttpStatus.OK);
    }

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
                        answerTemplate.setSelectedOptionIds(new ArrayList<>());
                    }
                    
                    return answerTemplate;
                })
                .collect(Collectors.toList());
            
            QuizSubmissionDTO template = new QuizSubmissionDTO();
            template.setQuizId(quizId);
            template.setUserId(1L);
            template.setAnswers(templateAnswers);
            
            return new ResponseEntity<>(template, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/test-template/{quizId}")
    public ResponseEntity<?> getDetailedSubmissionTemplate(@PathVariable Long quizId) {
        try {
            QuizTest quiz = quizTestRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));
            
            // Build quiz info structure
            Map<String, Object> quizInfo = new HashMap<>();
            quizInfo.put("quizId", quiz.getId());
            quizInfo.put("quizTitle", quiz.getTitle());
            
            // Add question details
            List<Map<String, Object>> questionsInfo = quiz.getQuestions().stream()
                .map(q -> {
                    Map<String, Object> questionInfo = new HashMap<>();
                    questionInfo.put("questionId", q.getId());
                    questionInfo.put("questionText", q.getQuestionText());
                    questionInfo.put("questionType", q.getType());
                    
                    if (q.getType() != QuestionType.TEXT) {
                        questionInfo.put("options", q.getOptions().stream()
                            .map(opt -> {
                                Map<String, Object> optionInfo = new HashMap<>();
                                optionInfo.put("optionId", opt.getId());
                                optionInfo.put("optionText", opt.getAnswerText());
                                optionInfo.put("isCorrect", opt.isCorrect());
                                return optionInfo;
                            })
                            .collect(Collectors.toList()));
                    } else {
                        questionInfo.put("correctAnswer", q.getCorrectAnswer());
                    }
                    
                    return questionInfo;
                })
                .collect(Collectors.toList());
            
            quizInfo.put("questions", questionsInfo);
            
            // Create template with correct answers
            List<QuestionAnswerDTO> templateAnswers = quiz.getQuestions().stream()
                .map(q -> {
                    QuestionAnswerDTO answerTemplate = new QuestionAnswerDTO();
                    answerTemplate.setQuestionId(q.getId());
                    
                    if (q.getType() == QuestionType.TEXT) {
                        answerTemplate.setTextAnswer(q.getCorrectAnswer());
                    } else {
                        answerTemplate.setSelectedOptionIds(q.getOptions().stream()
                            .filter(Option::isCorrect)
                            .map(Option::getId)
                            .collect(Collectors.toList()));
                    }
                    
                    return answerTemplate;
                })
                .collect(Collectors.toList());
            
            QuizSubmissionDTO template = new QuizSubmissionDTO();
            template.setQuizId(quizId);
            template.setUserId(1L);
            template.setAnswers(templateAnswers);
            
            Map<String, Object> response = new HashMap<>();
            response.put("quizStructure", quizInfo);
            response.put("submissionTemplate", template);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
