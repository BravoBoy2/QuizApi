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


public class QuestionServiceImpl implements QuestionService {

    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;
    private final QuizTestRepository quizTestRepository;

    public QuestionServiceImpl(OptionRepository optionRepository,
                               QuestionRepository questionRepository,
                               QuizTestRepository quizTestRepository) {
        this.optionRepository = optionRepository;
        this.questionRepository = questionRepository;
        this.quizTestRepository = quizTestRepository;
    }

    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        Optional<QuizTest> optionalQuizTest = quizTestRepository.findById(questionDTO.getQuiz().getId());
        if (optionalQuizTest.isEmpty()) {
            throw new EntityNotFoundException("Quiz not found");
        }

        QuizTest quiz = optionalQuizTest.get();

        Question question = new Question();
        question.setQuiz(quiz);
        question.setQuestion(questionDTO.getQuestion());
        question.setType(questionDTO.getType());

        // Convert options directly inside the method
        if ((question.getType() == QuestionType.MCQ || question.getType() == QuestionType.SINGLE)
                && (questionDTO.getOptions() == null || questionDTO.getOptions().isEmpty())) {
            throw new IllegalArgumentException("Options are required for MCQ and single select questions.");
        }

        List<Option> options = questionDTO.getOptions().stream().map(optionDTO -> {
            Option option = new Option();
            option.setQuestion(question);
            option.setAnswerText(optionDTO.getAnswerText());
            option.setCorrect(optionDTO.isCorrect());
            return option;
        }).collect(Collectors.toList());

        question.setOptions(optionRepository.saveAll(options));

        // Save question with options
        return questionRepository.save(question).getQuestionDTO();
    }
}
