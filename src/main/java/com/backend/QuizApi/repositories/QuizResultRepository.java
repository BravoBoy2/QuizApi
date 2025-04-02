package com.backend.QuizApi.repositories;

import com.backend.QuizApi.entities.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    // Find all results for a particular user
    List<QuizResult> findByUserId(Long userId);
    
    // Find all results for a particular quiz
    List<QuizResult> findByQuizTestId(Long quizId);
    
    // Find result for a specific user and quiz combination
    QuizResult findByUserIdAndQuizTestId(Long userId, Long quizId);
}
