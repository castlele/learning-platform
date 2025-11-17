package com.castlelecs.platform.repository;

import com.castlelecs.platform.entity.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    List<QuizSubmission> findByStudentId(Long studentId);

    List<QuizSubmission> findByQuizId(Long quizId);
}
