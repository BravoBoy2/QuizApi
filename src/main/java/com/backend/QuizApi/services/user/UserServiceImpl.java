package com.backend.QuizApi.services.user;

import com.backend.QuizApi.entities.User;
import com.backend.QuizApi.enums.UserRole;
import com.backend.QuizApi.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //method for automatically creating an admin account
    @PostConstruct
    private void createAdminUser(){
        User optionalUser = userRepository.findByRole(UserRole.ADMIN);
        if(optionalUser != null){
            User user = new User();

            user.setName("admin");
            user.setEmail("admin@gmail.com");
            user.setPassword("admin");
            user.setRole(UserRole.ADMIN);

            userRepository.save(user);

        }
    }

    //finding user with email
    public Boolean hasUserWithEmail(String email){
        return userRepository.findFirstByEmail(email) != null;
    }


    public User createUser(User user){
        user.setRole(UserRole.USER);

        return userRepository.save(user);
    }
}
