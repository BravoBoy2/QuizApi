package com.backend.QuizApi.entities;

import com.backend.QuizApi.DTO.QuizResultDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private QuizTest quizTest;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private int totalQuestions;
    
    private int totalCorrectAnswers;
    
    private double percentageCorrAnswer;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /**
     * Converts this entity to a DTO for API responses
     * @return QuizResultDTO representation of this entity
     */
    public QuizResultDTO toDTO() {
        return new QuizResultDTO(
            this.id,
            this.quizTest.getId(),
            this.user.getId(),
            this.totalQuestions,
            this.totalCorrectAnswers,
            this.percentageCorrAnswer
        );
    }
}
