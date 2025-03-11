package com.backend.QuizApi.services.Question;

import com.backend.QuizApi.DTO.QuestionDTO;
import com.backend.QuizApi.entities.Option;
import com.backend.QuizApi.entities.Question;
import com.backend.QuizApi.entities.QuizTest;
import com.backend.QuizApi.enums.QuestionType;
import com.backend.QuizApi.repositories.OptionRepository;
import com.backend.QuizApi.repositories.QuestionRepository;
import com.backend.QuizApi.repositories.QuizTestRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;
    private final QuizTestRepository quizTestRepository;
    private static final Logger logger = LoggerFactory.getLogger(QuestionServiceImpl.class);

    public QuestionServiceImpl(OptionRepository optionRepository,
                               QuestionRepository questionRepository,
                               QuizTestRepository quizTestRepository) {
        this.optionRepository = optionRepository;
        this.questionRepository = questionRepository;
        this.quizTestRepository = quizTestRepository;
    }

    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        logger.info("Creating question with data: {}", questionDTO);
        Optional<QuizTest> optionalQuizTest = quizTestRepository.findById(questionDTO.getQuiz().getId());
        if (optionalQuizTest.isEmpty()) {
            throw new EntityNotFoundException("Quiz not found");
        }

        QuizTest quiz = optionalQuizTest.get();

        Question question = new Question();
        question.setQuiz(quiz);
        
        // Ensure the questionText is properly set
        if (questionDTO.getQuestionText() == null || questionDTO.getQuestionText().trim().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }
        question.setQuestionText(questionDTO.getQuestionText());
        
        question.setType(questionDTO.getType());
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());

        if ((question.getType() == QuestionType.MCQ || question.getType() == QuestionType.SINGLE)
                && (questionDTO.getOptions() == null || questionDTO.getOptions().isEmpty())) {
            throw new IllegalArgumentException("Options are required for MCQ and single select questions.");
        }

        List<Option> options = questionDTO.getOptions().stream().map(optionDTO -> {
            Option option = new Option();
            option.setQuestion(question);
            option.setAnswerText(optionDTO.getAnswerText());
            option.setIsCorrect(optionDTO.isCorrect());
            return option;
        }).collect(Collectors.toList());

        question.setOptions(optionRepository.saveAll(options));

        QuestionDTO createdQuestionDTO = questionRepository.save(question).getQuestionDTO();
        logger.info("Successfully created question: {}", createdQuestionDTO);
        return createdQuestionDTO;
    }
}
