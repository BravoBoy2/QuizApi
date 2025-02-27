package com.backend.QuizApi.services.Quiz;

import com.backend.QuizApi.DTO.QuizTestDTO;
import com.backend.QuizApi.entities.QuizTest;
import com.backend.QuizApi.repositories.OptionRepository;
import com.backend.QuizApi.repositories.QuestionRepository;
import com.backend.QuizApi.repositories.QuizTestRepository;
import org.springframework.stereotype.Service;



@Service
public class QuizTestServiceImpl implements QuizTestService {

    private final QuizTestRepository quizTestRepository;
    private QuestionRepository questionRepository;
    private OptionRepository optionRepository;


    public QuizTestServiceImpl(QuizTestRepository quizTestRepository,
                               QuestionRepository questionRepository,
                               OptionRepository optionRepository) {
        this.quizTestRepository = quizTestRepository;
        this.questionRepository = questionRepository;
    }

    // Create a new quiz
    public QuizTestDTO createQuizTest(QuizTestDTO quizTestDTO) {
        QuizTest quizTest = new QuizTest();
        quizTest.setTitle(quizTestDTO.getTitle());
        quizTest.setDescription(quizTestDTO.getDescription());
        quizTest.setTime(quizTestDTO.getTime());

        return quizTestRepository.save(quizTest).getDTO();
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
