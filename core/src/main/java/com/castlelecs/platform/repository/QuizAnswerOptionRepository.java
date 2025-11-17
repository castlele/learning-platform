package com.castlelecs.platform.repository;

import com.castlelecs.platform.entity.QuizAnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizAnswerOptionRepository extends JpaRepository<QuizAnswerOption, Long> {
}
