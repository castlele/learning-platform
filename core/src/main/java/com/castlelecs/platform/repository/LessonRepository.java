package com.castlelecs.platform.repository;

import com.castlelecs.platform.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    Set<Lesson> findByModuleId(Long moduleId);
}
