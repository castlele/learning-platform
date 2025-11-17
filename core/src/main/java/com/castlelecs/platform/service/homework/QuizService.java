package com.castlelecs.platform.service.homework;

import com.castlelecs.platform.dto.QuizAnswerOptionCreationRequest;
import com.castlelecs.platform.dto.QuizCreationRequest;
import com.castlelecs.platform.dto.QuizQuestionCreationRequest;
import com.castlelecs.platform.entity.*;
import com.castlelecs.platform.entity.Module;
import com.castlelecs.platform.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final ModuleRepository moduleRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerOptionRepository quizAnswerOptionRepository;
    private final UserRepository userRepository;

    public Quiz createQuiz(Long professorId, Long moduleId, QuizCreationRequest request) {
        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor not found: " + professorId));
        if (professor.getRole() != UserRole.PROFESSOR) {
            throw new IllegalStateException("User " + professorId + " is not a professor");
        }

        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId));

        Course course = module.getCourse();
        if (course == null || !course.getProfessor().getId().equals(professorId)) {
            throw new IllegalStateException("Professor " + professorId + " does not own this module");
        }

        Quiz quiz = Quiz.builder()
                .module(module)
                .title(request.getTitle())
                .build();

        return quizRepository.save(quiz);
    }

    public QuizQuestion addQuestion(Long quizId,
                                    QuizQuestionCreationRequest questionRequest,
                                    Set<QuizAnswerOptionCreationRequest> optionRequests) {

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));

        QuizQuestion question = QuizQuestion.builder()
                .quiz(quiz)
                .text(questionRequest.getText())
                .type(questionRequest.getType())
                .build();
        quizQuestionRepository.save(question);

        for (QuizAnswerOptionCreationRequest optReq : optionRequests) {
            QuizAnswerOption option = QuizAnswerOption.builder()
                    .question(question)
                    .text(optReq.getText())
                    .correct(optReq.getCorrect())
                    .build();
            quizAnswerOptionRepository.save(option);
        }

        return question;
    }
}
