package com.castlelecs.platform.service.homework;

import com.castlelecs.platform.dto.AssignmentCreationRequest;
import com.castlelecs.platform.entity.*;
import com.castlelecs.platform.repository.AssignmentRepository;
import com.castlelecs.platform.repository.LessonRepository;
import com.castlelecs.platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Transactional
    public Assignment createAssignment(Long professorId, Long lessonId, AssignmentCreationRequest request) {
        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor not found: " + professorId));
        if (professor.getRole() != UserRole.PROFESSOR) {
            throw new IllegalStateException("User " + professorId + " is not a professor");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found: " + lessonId));

        Course course = lesson.getModule().getCourse();
        if (!course.getProfessor().getId().equals(professorId)) {
            throw new IllegalStateException("Professor " + professorId + " does not own this lesson's course");
        }

        Assignment assignment = new Assignment();
        assignment.setLesson(lesson);
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setDueDate(request.getDueDate());
        assignment.setMaxScore(request.getMaxScore());

        return assignmentRepository.save(assignment);
    }

    @Transactional
    public Assignment updateAssignment(Long assignmentId, AssignmentCreationRequest request) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        if (request.getTitle() != null) {
            assignment.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            assignment.setDescription(request.getDescription());
        }
        if (request.getDueDate() != null) {
            assignment.setDueDate(request.getDueDate());
        }
        if (request.getMaxScore() != null) {
            assignment.setMaxScore(request.getMaxScore());
        }

        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void deleteAssignment(Long assignmentId) {
        if (!assignmentRepository.existsById(assignmentId)) {
            throw new IllegalArgumentException("Assignment not found: " + assignmentId);
        }
        assignmentRepository.deleteById(assignmentId);
    }

    @Transactional
    public Assignment getAssignment(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));
    }

    @Transactional
    public List<Assignment> getAssignmentsForLesson(Long lessonId) {
        return assignmentRepository.findByLessonId(lessonId);
    }
}
