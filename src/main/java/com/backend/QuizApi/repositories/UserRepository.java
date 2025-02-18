package com.backend.QuizApi.repositories;

import com.backend.QuizApi.entities.User;
import com.backend.QuizApi.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByRole(UserRole role);

    User findFirstByEmail(String email);
}
