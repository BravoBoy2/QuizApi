package com.backend.QuizApi.services.Quiz;

import com.backend.QuizApi.DTO.QuizTestDTO;
import com.backend.QuizApi.entities.QuizTest;
import com.backend.QuizApi.repositories.QuizTestRepository;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class QuizTestServiceImpl implements QuizTestService {

    private final QuizTestRepository quizTestRepository;


    public QuizTestServiceImpl(QuizTestRepository quizTestRepository) {
        this.quizTestRepository = quizTestRepository;
    }

    // Create a new quiz
    public QuizTestDTO createQuizTest(QuizTestDTO quizTestDTO) {
        QuizTest quizTest = new QuizTest();
        quizTest.setTitle(quizTestDTO.getTitle());
        quizTest.setDescription(quizTestDTO.getDescription());
        quizTest.setTime(quizTestDTO.getTime());

        return quizTestRepository.save(quizTest).getDTO();
    }

    @Override
    public List<QuizTestDTO> getAllQuizzes() {
        List<QuizTest> quizzes = quizTestRepository.findAll(); // Fetch all quizzes

        return quizzes.stream()
                .map(quizTest -> {
                    long totalMillis = (quizTest.getTime() != null)
                            ? quizTest.getTime().getTime() : 600_000; // Default 10 minutes in milliseconds

                    int numQuestions = quizTest.getQuestionList().size();
                    long timePerQuestionMillis = (numQuestions == 0) ? 0 : totalMillis /  numQuestions;

                    quizTest.setTime(new Time(timePerQuestionMillis)); // Adjust time per question

                    return quizTest.getDTO(); // Convert to DTO
                })
                .collect(Collectors.toList()); // Return List<QuizTestDTO>
    }








//    public QuestionDTO createQuestionInQuiz(QuestionDTO questionDTO) {
//        Optional<QuizTest> optionalQuizTest = quizTestRepository.findById(questionDTO.getId());
//
//        if (optionalQuizTest.isPresent()) {
//            Question question = new Question();
//
//            question.setQuiz(optionalQuizTest.get());
//            question.setQuestion(questionDTO.getQuestion());
//            question.setOptions(questionDTO.getOptions());
//            question.setType(questionDTO.getType());
//            question.setCorrectAnswer(questionDTO.getCorrectAnswer());
//
//            return questionRepository.save(question).getQuestionDTO();
//
//        }
//
//        throw new EntityNotFoundException("Quiz set was not found");
//    }



}
