package com.backend.QuizApi.services.user;

import com.backend.QuizApi.entities.User;

public interface UserService {
    //creating user (abstract method)
    User createUser(User user);

    //user check with email
    Boolean hasUserWithEmail(String email);

}
