package com.backend.QuizApi.repositories;

import com.backend.QuizApi.entities.QuizTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizTestRepository extends JpaRepository<QuizTest, Long> {
}
