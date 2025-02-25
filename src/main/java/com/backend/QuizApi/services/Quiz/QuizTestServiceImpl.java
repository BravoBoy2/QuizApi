package com.backend.QuizApi.services.Quiz;

import com.backend.QuizApi.DTO.QuizTestDTO;
import com.backend.QuizApi.entities.QuizTest;
import com.backend.QuizApi.repositories.QuizTestRepository;
import org.springframework.stereotype.Service;

@Service
public class QuizTestServiceImpl implements QuizTestService {

    private final QuizTestRepository quizTestRepository;

    public QuizTestServiceImpl(QuizTestRepository quizTestRepository) {
        this.quizTestRepository = quizTestRepository;
    }

    //quiz creation
    public QuizTestDTO createQuizTest(QuizTestDTO quizTestDTO) {
        QuizTest quizTest = new QuizTest();

        quizTest.setTitle(quizTestDTO.getTitle());
        quizTest.setDescription(quizTestDTO.getDescription());
        quizTest.setTime(quizTestDTO.getTime());

        return quizTestRepository.save(quizTest).getDTO();

    }
}
