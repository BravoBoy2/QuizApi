package com.backend.QuizApi.services.Quiz;

import com.backend.QuizApi.DTO.QuestionDTO;
import com.backend.QuizApi.DTO.QuizTestDTO;
import com.backend.QuizApi.entities.Option;
import com.backend.QuizApi.entities.Question;
import com.backend.QuizApi.entities.QuizTest;
import com.backend.QuizApi.enums.QuestionType;
import com.backend.QuizApi.repositories.OptionRepository;
import com.backend.QuizApi.repositories.QuestionRepository;
import com.backend.QuizApi.repositories.QuizTestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuizTestServiceImpl implements QuizTestService {

    private final QuizTestRepository quizTestRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private static final Logger logger = LoggerFactory.getLogger(QuizTestServiceImpl.class);

    public QuizTestServiceImpl(QuizTestRepository quizTestRepository, 
                               QuestionRepository questionRepository, 
                               OptionRepository optionRepository) {
        this.quizTestRepository = quizTestRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }

    public QuizTestDTO createQuizTest(QuizTestDTO quizTestDTO) {
        QuizTest quizTest = new QuizTest();
        quizTest.setTitle(quizTestDTO.getTitle());
        quizTest.setDescription(quizTestDTO.getDescription());
        quizTest.setTime(quizTestDTO.getTime());

        return quizTestRepository.save(quizTest).getDTO();
    }

    public List<QuizTestDTO> getAllQuizzes() {
        List<QuizTest> quizzes = quizTestRepository.findAll();
        return quizzes.stream()
                .map(QuizTest::getDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuizTestDTO addQuestionsToQuiz(Long quizId, List<QuestionDTO> questionDTOs) {
        logger.info("Adding questions to quiz with ID: {}", quizId);
        logger.info("Questions to add: {}", questionDTOs);
        
        Optional<QuizTest> optionalQuizTest = quizTestRepository.findById(quizId);
        if (optionalQuizTest.isEmpty()) {
            throw new EntityNotFoundException("Quiz not found with ID: " + quizId);
        }

        QuizTest quiz = optionalQuizTest.get();
        
        if (quiz.getQuestions() == null) {
            quiz.setQuestions(new ArrayList<>());
        }
        
        for (QuestionDTO questionDTO : questionDTOs) {
            Question question = new Question();
            question.setQuiz(quiz);
            
            // Make sure we're setting the text correctly here
            if (questionDTO.getQuestionText() != null && !questionDTO.getQuestionText().isEmpty()) {
                question.setQuestionText(questionDTO.getQuestionText());
            } else {
                throw new IllegalArgumentException("Question text cannot be empty");
            }
            
            question.setType(questionDTO.getType());
            question.setCorrectAnswer(questionDTO.getCorrectAnswer());
            
            if (questionDTO.getType() == QuestionType.TEXT) {
                // For TEXT questions, no options needed
                if (questionDTO.getCorrectAnswer() == null || questionDTO.getCorrectAnswer().isEmpty()) {
                    throw new IllegalArgumentException("Correct answer is required for TEXT questions.");
                }
                question = questionRepository.save(question);
            } else {
                // For MCQ and SINGLE questions, options are required
                if (questionDTO.getOptions() == null || questionDTO.getOptions().isEmpty()) {
                    throw new IllegalArgumentException("Options are required for MCQ and SINGLE questions.");
                }
                
                question = questionRepository.save(question);
                
                List<Option> options = new ArrayList<>();
                for (Option optionDTO : questionDTO.getOptions()) {
                    Option option = new Option();
                    option.setQuestion(question);
                    option.setAnswerText(optionDTO.getAnswerText());
                    option.setIsCorrect(optionDTO.isCorrect());
                    options.add(optionRepository.save(option));
                }
                
                question.setOptions(options);
                question = questionRepository.save(question);
            }
            
            quiz.getQuestions().add(question);
        }
        
        QuizTest savedQuiz = quizTestRepository.save(quiz);
        logger.info("Successfully added questions to quiz. Updated quiz: {}", savedQuiz);
        return savedQuiz.getDTO();
    }
}
