package com.castlelecs.platform.repository;

import com.castlelecs.platform.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByModuleId(Long moduleId);
}
