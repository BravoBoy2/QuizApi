package com.backend.QuizApi.entities;

import com.backend.QuizApi.DTO.QuizTestDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Time;


@Entity
@Data
public class QuizTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    private String description;

    private Time time;


    public QuizTestDTO getDTO() {
        QuizTestDTO quizTestDTO = new QuizTestDTO();

        quizTestDTO.setId(id);
        quizTestDTO.setTitle(title);
        quizTestDTO.setDescription(description);
        quizTestDTO.setTime(time);

        return quizTestDTO;
    }


}
