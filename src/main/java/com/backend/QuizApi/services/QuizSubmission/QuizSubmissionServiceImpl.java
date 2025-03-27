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
            }
        }
        
        if (answeredQuestions == 0) {
            throw new IllegalArgumentException("None of the submitted question IDs match questions in this quiz. " +
                    "Quiz has questions with IDs: " + questionsMap.keySet());
        }
        
        // Calculate score and create result
        double percentageCorrect = (double) correctAnswers / totalQuestions * 100;
        
        QuizResult quizResult = new QuizResult();
        quizResult.setQuizTest(quiz);
        quizResult.setUser(user);
        quizResult.setTotalQuestions(totalQuestions);
        quizResult.setTotalCorrectAnswers(correctAnswers);
        quizResult.setPercentageCorrAnswer(percentageCorrect);
        
        QuizResult savedQuizResult = quizResultRepository.save(quizResult);
        return new QuizResultDTO(
                savedQuizResult.getId(),
                savedQuizResult.getQuizTest().getId(),
                savedQuizResult.getUser().getId(),
                savedQuizResult.getTotalQuestions(),
                savedQuizResult.getTotalCorrectAnswers(),
                savedQuizResult.getPercentageCorrAnswer()
        );
    }
    
    private boolean evaluateAnswer(Question question, QuestionAnswerDTO answer) {
        QuestionType type = question.getType();
        
        if (type == QuestionType.TEXT) {
            return evaluateTextAnswer(question, answer);
        } else if (type == QuestionType.SINGLE) {
            return evaluateSingleAnswer(question, answer);
        } else if (type == QuestionType.MCQ) {
            return evaluateMCQAnswer(question, answer);
        }
        return false;
    }
    
    private boolean evaluateTextAnswer(Question question, QuestionAnswerDTO answer) {
        // For text questions, compare the provided text answer with the correct answer
        String correctAnswer = question.getCorrectAnswer();
        String userAnswer = answer.getTextAnswer();
        
        // Add detailed logging
        logger.debug("TEXT Question ID: {}, Text: \"{}\"", question.getId(), question.getQuestionText());
        logger.debug("Correct answer (raw): \"{}\"", correctAnswer);
        logger.debug("User answer (raw): \"{}\"", userAnswer);
        
        // Handle null values
        if (correctAnswer == null) {
            logger.warn("Question {} has null correctAnswer", question.getId());
            return false;
        }
        
        if (userAnswer == null) {
            logger.debug("User provided null answer for question {}", question.getId());
            return false;
        }
        
        // Normalize answers for comparison (trim, remove extra spaces, case-insensitive)
        String normalizedCorrect = correctAnswer.trim().toLowerCase().replaceAll("\\s+", " ");
        String normalizedUser = userAnswer.trim().toLowerCase().replaceAll("\\s+", " ");
        
        logger.debug("Normalized correct: \"{}\"", normalizedCorrect);
        logger.debug("Normalized user: \"{}\"", normalizedUser);
        
        // First try exact match after normalization
        boolean exactMatch = normalizedCorrect.equals(normalizedUser);
        logger.debug("Exact match result: {}", exactMatch);
        
        if (exactMatch) {
            return true;
        }
        
        // Try with punctuation and special chars removed
        String strippedCorrect = normalizedCorrect.replaceAll("[^a-z0-9 ]", "");
        String strippedUser = normalizedUser.replaceAll("[^a-z0-9 ]", "");
        
        logger.debug("Stripped correct: \"{}\"", strippedCorrect);
        logger.debug("Stripped user: \"{}\"", strippedUser);
        
        boolean strippedMatch = strippedCorrect.equals(strippedUser);
        logger.debug("Stripped match result: {}", strippedMatch);
        
        // If still no match, try with very loose comparison (contains the most important words)
        if (!strippedMatch) {
            // Split into words and check if key words are present
            String[] correctWords = strippedCorrect.split(" ");
            String[] userWords = strippedUser.split(" ");
            
            // Filter out common small words that don't affect meaning much
            java.util.Set<String> stopWords = java.util.Set.of("a", "an", "the", "and", "or", "but", "is", "are", "was", "were", "be", "to", "of", "in", "that", "have", "it", "for", "on", "with");
            java.util.Set<String> significantCorrectWords = new java.util.HashSet<>();
            
            // Only keep significant words (longer than 3 chars or not in stop words)
            for (String word : correctWords) {
                if (word.length() > 3 || !stopWords.contains(word)) {
                    significantCorrectWords.add(word);
                }
            }
            
            // Count how many significant words from the correct answer are in the user answer
            int matchingWords = 0;
            for (String word : userWords) {
                if (significantCorrectWords.contains(word)) {
                    matchingWords++;
                }
            }
            
            // If user answer contains at least 70% of significant words from correct answer, consider it correct
            if (!significantCorrectWords.isEmpty()) {
                double matchRatio = (double) matchingWords / significantCorrectWords.size();
                logger.debug("Significant word match ratio: {} ({}/{})", 
                    String.format("%.2f", matchRatio), matchingWords, significantCorrectWords.size());
                
                if (matchRatio >= 0.7) {
                    logger.debug("Accepting answer based on significant word match");
                    return true;
                }
            }
        }
        
        return strippedMatch;
    }
    
    private boolean evaluateSingleAnswer(Question question, QuestionAnswerDTO answer) {
        // For single-select questions, exactly one option should be selected
        List<Long> selectedOptionIds = answer.getSelectedOptionIds();
        List<Option> questionOptions = question.getOptions();
        
        if (selectedOptionIds == null || selectedOptionIds.size() != 1) {
            return false;
        }
        
        Long selectedOptionId = selectedOptionIds.get(0);
        
        // Find the selected option directly from the question's options list
        for (Option option : questionOptions) {
            if (option.getId() == selectedOptionId) {
                return option.isCorrect();
            }
        }
        
        return false;
    }
    
    private boolean evaluateMCQAnswer(Question question, QuestionAnswerDTO answer) {
        // For MCQ questions, all correct options should be selected and no incorrect options
        List<Long> selectedOptionIds = answer.getSelectedOptionIds();
        List<Option> questionOptions = question.getOptions();
        
        if (selectedOptionIds == null || selectedOptionIds.isEmpty()) {
            return false;
        }
        
        if (questionOptions == null || questionOptions.isEmpty()) {
            return false;
        }
        
        // Get correct option IDs for this question
        Set<Long> correctOptionIds = questionOptions.stream()
                .filter(Option::isCorrect)
                .map(Option::getId)
                .collect(Collectors.toSet());
        
        // Convert selected IDs to set for comparison
        Set<Long> selectedIds = Set.copyOf(selectedOptionIds);
        
        // User must select all correct options and no incorrect ones
        return selectedIds.equals(correctOptionIds);
    }
}
