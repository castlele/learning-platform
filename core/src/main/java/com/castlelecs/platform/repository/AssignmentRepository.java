package com.castlelecs.platform.repository;

import com.castlelecs.platform.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByLessonId(Long lessonId);
}
