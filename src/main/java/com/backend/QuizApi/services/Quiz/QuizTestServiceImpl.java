package com.backend.QuizApi.services.Quiz;

import com.backend.QuizApi.DTO.QuestionDTO;
import com.backend.QuizApi.DTO.QuizDetailsDTO;
import com.backend.QuizApi.DTO.QuizTestDTO;
import com.backend.QuizApi.entities.Option;
import com.backend.QuizApi.entities.Question;
import com.backend.QuizApi.entities.QuizTest;
import com.backend.QuizApi.enums.QuestionType;
import com.backend.QuizApi.repositories.OptionRepository;
import com.backend.QuizApi.repositories.QuestionRepository;
import com.backend.QuizApi.repositories.QuizTestRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizTestServiceImpl implements QuizTestService {

    private final QuizTestRepository quizTestRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

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
        QuizTest quiz = quizTestRepository.findById(quizId)
            .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));
        
        // Initialize questions list inline if needed
        if (quiz.getQuestions() == null) {
            quiz.setQuestions(new ArrayList<>());
        }
        
        // Create all questions at once using streams
        List<Question> questions = questionDTOs.stream()
            .map(questionDTO -> createQuestionFromDTO(questionDTO, quiz))
            .collect(Collectors.toList());
        
        // Add all questions to quiz
        quiz.getQuestions().addAll(questions);
        
        // Save the quiz with all its questions in one operation
        QuizTest savedQuiz = quizTestRepository.save(quiz);
        return savedQuiz.getDTO();
    }
    
    private Question createQuestionFromDTO(QuestionDTO questionDTO, QuizTest quiz) {
        validateQuestionData(questionDTO);
        
        Question question = new Question();
        question.setQuiz(quiz);
        question.setQuestionText(questionDTO.getQuestionText());
        question.setType(questionDTO.getType());
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());
        
        question = questionRepository.save(question);
        
        if (questionDTO.getType() != QuestionType.TEXT) {
            addOptionsToQuestion(question, questionDTO.getOptions());
        }
        
        return question;
    }
    
    private void validateQuestionData(QuestionDTO questionDTO) {
        // Validate question text for all question types
        if (questionDTO.getQuestionText() == null || questionDTO.getQuestionText().isEmpty()) {
            throw new IllegalArgumentException("Question text cannot be empty");
        }
        
        // Validate based on question type
        switch (questionDTO.getType()) {
            case TEXT:
                if (questionDTO.getCorrectAnswer() == null || questionDTO.getCorrectAnswer().isEmpty()) {
                    throw new IllegalArgumentException("Correct answer is required for TEXT questions");
                }
                break;
            case MCQ:
            case SINGLE:
                if (questionDTO.getOptions() == null || questionDTO.getOptions().isEmpty()) {
                    throw new IllegalArgumentException("Options are required for " + questionDTO.getType() + " questions");
                }
                break;
        }
    }
    
    private void addOptionsToQuestion(Question question, List<Option> optionDTOs) {
        // Create the list once and save all options in a batch operation
        List<Option> options = optionDTOs.stream()
            .map(optionDTO -> {
                Option option = new Option();
                option.setQuestion(question);
                option.setAnswerText(optionDTO.getAnswerText());
                option.setIsCorrect(optionDTO.isCorrect());
                return option;
            })
            .collect(Collectors.toList());
        
        // Set options to the question
        question.setOptions(options);
        
        // Save all options at once using saveAll instead of individual saves
        optionRepository.saveAll(options);
    }

    public QuizDetailsDTO getAllQuestionsByQuiz(long id) {
        return quizTestRepository.findById(id)
            .map(quiz -> {
                QuizDetailsDTO quizDetailsDTO = new QuizDetailsDTO();
                quizDetailsDTO.setQuizTestDTO(quiz.getDTO());
                
                // Use the existing list or an empty list if null
                quizDetailsDTO.setQuestions(
                    quiz.getQuestions() == null ? 
                    List.of() :  // Use immutable empty list instead of new ArrayList<>()
                    quiz.getQuestions().stream()
                        .map(this::mapQuestionToDTO)
                        .collect(Collectors.toList())
                );
                
                return quizDetailsDTO;
            })
            .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + id));
    }
    
    private QuestionDTO mapQuestionToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        // Only include the necessary data for the frontend, not the internal ID
        // dto.setId(question.getId()); // Removing this line to avoid exposing internal ID
        dto.setQuestionText(question.getQuestionText());
        dto.setType(question.getType());
        dto.setCorrectAnswer(question.getCorrectAnswer());
        
        // Create new Option objects instead of using the entity directly
        if (question.getOptions() != null) {
            List<Option> optionDTOs = question.getOptions().stream()
                .map(option -> {
                    Option optionDTO = new Option();
                    // Don't set the ID to avoid exposing it
                    optionDTO.setAnswerText(option.getAnswerText());
                    optionDTO.setIsCorrect(option.isCorrect());
                    // Don't set the question reference to avoid circular references
                    return optionDTO;
                })
                .collect(Collectors.toList());
            dto.setOptions(optionDTOs);
        } else {
            dto.setOptions(List.of());
        }
        
        return dto;
    }
}
