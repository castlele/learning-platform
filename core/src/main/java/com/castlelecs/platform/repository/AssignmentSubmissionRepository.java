package com.castlelecs.platform.repository;

import com.castlelecs.platform.entity.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByAssignment_Lesson_Module_Course_Professor_Id(Long professorId);
}
