package com.backend.QuizApi.controllers;

import com.backend.QuizApi.services.Quiz.QuizTestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quiz")
public class QuizTestController {

    private final QuizTestService quizTestService;

    public QuizTestController(QuizTestService quizTestService) {
        this.quizTestService = quizTestService;
    }

}
