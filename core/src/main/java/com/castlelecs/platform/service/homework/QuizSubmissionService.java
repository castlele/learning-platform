package com.castlelecs.platform.service.homework;

import com.castlelecs.platform.dto.QuizResultDto;
import com.castlelecs.platform.dto.QuizSubmissionCreationRequest;
import com.castlelecs.platform.entity.Quiz;
import com.castlelecs.platform.entity.QuizSubmission;
import com.castlelecs.platform.entity.User;
import com.castlelecs.platform.entity.UserRole;
import com.castlelecs.platform.interactor.homework.quiz.QuizScoringInteractor;
import com.castlelecs.platform.repository.QuizRepository;
import com.castlelecs.platform.repository.QuizSubmissionRepository;
import com.castlelecs.platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class QuizSubmissionService {
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuizScoringInteractor quizScoringInteractor;

    public QuizResultDto submitQuiz(Long studentId,
                                    Long quizId,
                                    QuizSubmissionCreationRequest request) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("User " + studentId + " is not a student");
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));

        Integer score = quizScoringInteractor.calculateScore(quiz, request);
        boolean passed = quizScoringInteractor.isPassed(quiz, score);

        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setStudent(student);
        submission.setScore(score);
        submission.setSubmittedAt(LocalDateTime.now());

        quizSubmissionRepository.save(submission);

        return new QuizResultDto(quizId, studentId, score, passed);
    }

    public List<QuizSubmission> getResultsForStudent(Long studentId) {
        return quizSubmissionRepository.findByStudentId(studentId);
    }

    public List<QuizSubmission> getResultsForQuiz(Long quizId) {
        return quizSubmissionRepository.findByQuizId(quizId);
    }
}
