package com.backend.QuizApi.entities;

import com.backend.QuizApi.DTO.QuizTestDTO;
import jakarta.persistence.*;
import lombok.Data;
import java.sql.Time;
import java.util.List;


@Entity
@Data
public class QuizTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Time time;

    @OneToMany(mappedBy = "quiz",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Question> questionList;


    public QuizTestDTO getDTO() {
        QuizTestDTO quizTestDTO = new QuizTestDTO();

        quizTestDTO.setId(id);
        quizTestDTO.setTitle(title);
        quizTestDTO.setDescription(description);
        quizTestDTO.setTime(time);

        return quizTestDTO;
    }


}
