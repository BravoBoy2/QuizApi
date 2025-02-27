package com.backend.QuizApi.services.Question;

import com.backend.QuizApi.DTO.QuestionDTO;
import org.springframework.stereotype.Service;

@Service
public interface QuestionService {

    QuestionDTO createQuestion(QuestionDTO questionDTO);
}
