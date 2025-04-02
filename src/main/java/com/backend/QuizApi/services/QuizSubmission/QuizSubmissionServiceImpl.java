package com.backend.QuizApi.services.QuizSubmission;

import com.backend.QuizApi.DTO.QuestionAnswerDTO;
import com.backend.QuizApi.DTO.QuizResultDTO;
import com.backend.QuizApi.DTO.QuizSubmissionDTO;
import com.backend.QuizApi.entities.Option;
import com.backend.QuizApi.entities.Question;
import com.backend.QuizApi.entities.QuizResult;
import com.backend.QuizApi.entities.QuizTest;
import com.backend.QuizApi.entities.User;
import com.backend.QuizApi.enums.QuestionType;
import com.backend.QuizApi.repositories.QuizResultRepository;
import com.backend.QuizApi.repositories.QuizTestRepository;
import com.backend.QuizApi.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuizSubmissionServiceImpl implements QuizSubmissionService {
    
    private final QuizTestRepository quizTestRepository;
    private final UserRepository userRepository;
    private final QuizResultRepository quizResultRepository;
    private static final Logger logger = LoggerFactory.getLogger(QuizSubmissionServiceImpl.class);
    
    public QuizSubmissionServiceImpl(
            QuizTestRepository quizTestRepository,
            UserRepository userRepository,
            QuizResultRepository quizResultRepository) {
        this.quizTestRepository = quizTestRepository;
        this.userRepository = userRepository;
        this.quizResultRepository = quizResultRepository;
    }
    
    @Override
    @Transactional
    public QuizResultDTO submitQuiz(QuizSubmissionDTO submission) {
        logger.info("Processing quiz submission for quiz ID: {}", submission.getQuizId());
        
        // Fetch quiz and user
        QuizTest quiz = quizTestRepository.findById(submission.getQuizId())
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + submission.getQuizId()));
        
        User user = userRepository.findById(submission.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + submission.getUserId()));
        
        // Get all questions for this quiz
        List<Question> questions = quiz.getQuestions();
        int totalQuestions = questions.size();
        
        if (totalQuestions == 0) {
            throw new IllegalStateException("Quiz has no questions");
        }
        
        // Organize questions by ID for efficient lookup
        Map<Long, Question> questionsMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));
        
        // Log question IDs to help with debugging
        logger.info("Question IDs in quiz {}: {}", quiz.getId(), questionsMap.keySet());
        logger.info("User submitted answers for question IDs: {}", 
                submission.getAnswers().stream()
                        .map(QuestionAnswerDTO::getQuestionId)
                        .collect(Collectors.toList()));
        
        // Map options by ID for efficient lookup
        Map<Long, Option> optionsMap = questions.stream()
                .filter(q -> q.getOptions() != null)
                .flatMap(q -> q.getOptions().stream())
                .collect(Collectors.toMap(Option::getId, o -> o));
        
        // Evaluate user answers
        int correctAnswers = 0;
        int answeredQuestions = 0;
        
        for (QuestionAnswerDTO answer : submission.getAnswers()) {
            Question question = questionsMap.get(answer.getQuestionId());
            
            if (question == null) {
                logger.warn("Question ID {} is not part of quiz ID {}", answer.getQuestionId(), quiz.getId());
                continue;
            }
            
            answeredQuestions++;
            boolean isCorrect = evaluateAnswer(question, answer);
            
            if (isCorrect) {
                correctAnswers++;
                logger.info("Question ID {} answered correctly", answer.getQuestionId());
            } else {
                logger.info("Question ID {} answered incorrectly", answer.getQuestionId());
            }
        }
        
        // If no questions were answered, fail gracefully
        if (answeredQuestions == 0) {
            throw new IllegalArgumentException("None of the submitted question IDs match questions in this quiz. " +
                    "Quiz has questions with IDs: " + questionsMap.keySet());
        }
        
        // Calculate score based on answered questions
        double percentageCorrect = (double) correctAnswers / totalQuestions * 100;
        
        // Create and save quiz result
        QuizResult quizResult = new QuizResult();
        quizResult.setQuizTest(quiz);
        quizResult.setUser(user);
        quizResult.setTotalQuestions(totalQuestions);
        quizResult.setTotalCorrectAnswers(correctAnswers);
        quizResult.setPercentageCorrAnswer(percentageCorrect);
        
        QuizResult savedResult = quizResultRepository.save(quizResult);
        logger.info("Quiz result saved: correctAnswers={}, totalQuestions={}, percentage={}", 
                correctAnswers, totalQuestions, percentageCorrect);
        
        // Return result DTO
        return savedResult.QuizResultDTO();
    }
    
    // Simplified method signature since we don't use the optionsMap anymore
    private boolean evaluateAnswer(Question question, QuestionAnswerDTO answer) {
        QuestionType type = question.getType();
        
        logger.info("Evaluating {} question: {}", type, question.getQuestionText());
        
        if (type == QuestionType.TEXT) {
            return evaluateTextAnswer(question, answer);
        } else if (type == QuestionType.SINGLE) {
            return evaluateSingleAnswer(question, answer);
        } else if (type == QuestionType.MCQ) {
            return evaluateMCQAnswer(question, answer);
        } else {
            logger.warn("Unknown question type: {}", type);
            return false;
        }
    }
    
    private boolean evaluateTextAnswer(Question question, QuestionAnswerDTO answer) {
        // For text questions, compare the provided text answer with the correct answer
        String correctAnswer = question.getCorrectAnswer();
        String userAnswer = answer.getTextAnswer();
        
        logger.debug("Text question - Correct answer: '{}', User answer: '{}'", correctAnswer, userAnswer);
        
        if (correctAnswer == null || userAnswer == null) {
            logger.debug("Text question - Null answer detected");
            return false;
        }
        
        // Normalize answers for comparison (trim and case-insensitive)
        boolean isCorrect = correctAnswer.trim().equalsIgnoreCase(userAnswer.trim());
        logger.debug("Text answer evaluation result: {}", isCorrect);
        return isCorrect;
    }
    
    private boolean evaluateSingleAnswer(Question question, QuestionAnswerDTO answer) {
        // For single-select questions, exactly one option should be selected
        List<Long> selectedOptionIds = answer.getSelectedOptionIds();
        List<Option> questionOptions = question.getOptions();
        
        logger.debug("Single question - Options: {}, Selected: {}", 
                questionOptions.stream().map(Option::getId).collect(Collectors.toList()),
                selectedOptionIds);
        
        if (selectedOptionIds == null || selectedOptionIds.size() != 1) {
            logger.debug("Single question - Invalid number of selections");
            return false;
        }
        
        Long selectedOptionId = selectedOptionIds.get(0);
        
        // Find the selected option directly from the question's options list
        for (Option option : questionOptions) {
            if (option.getId() == selectedOptionId) {
                boolean isCorrect = option.isCorrect();
                logger.debug("Single answer option {} is correct: {}", selectedOptionId, isCorrect);
                return isCorrect;
            }
        }
        
        logger.debug("Single question - Selected option not found");
        return false;
    }
    
    private boolean evaluateMCQAnswer(Question question, QuestionAnswerDTO answer) {
        // For MCQ questions, all correct options should be selected and no incorrect options
        List<Long> selectedOptionIds = answer.getSelectedOptionIds();
        List<Option> questionOptions = question.getOptions();
        
        logger.debug("MCQ question - Options: {}, Selected: {}", 
                questionOptions.stream().map(o -> o.getId() + ":" + o.isCorrect()).collect(Collectors.toList()),
                selectedOptionIds);
        
        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            logger.debug("MCQ question - No options selected");
            return false;
        }
        
        if (questionOptions == null || questionOptions.isEmpty()) {
            logger.debug("MCQ question - No options available");
            return false;
        }
        
        // Get correct option IDs for this question
        Set<Long> correctOptionIds = questionOptions.stream()
                .filter(Option::isCorrect)
                .map(Option::getId)
                .collect(Collectors.toSet());
        
        logger.debug("MCQ question - Correct options: {}", correctOptionIds);
        
        // Convert selected IDs to set for comparison
        Set<Long> selectedIds = Set.copyOf(selectedOptionIds);
        
        // User must select all correct options and no incorrect ones
        boolean isCorrect = selectedIds.equals(correctOptionIds);
        logger.debug("MCQ answer evaluation result: {}", isCorrect);
        return isCorrect;
    }
}
