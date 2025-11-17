package com.castlelecs.platform.service.homework;

import com.castlelecs.platform.dto.AssignmentSubmissionCreationRequest;
import com.castlelecs.platform.entity.Assignment;
import com.castlelecs.platform.entity.AssignmentSubmission;
import com.castlelecs.platform.entity.User;
import com.castlelecs.platform.entity.UserRole;
import com.castlelecs.platform.repository.AssignmentRepository;
import com.castlelecs.platform.repository.AssignmentSubmissionRepository;
import com.castlelecs.platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentSubmissionService {
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public AssignmentSubmission submitAssignment(Long studentId,
                                                 Long assignmentId,
                                                 AssignmentSubmissionCreationRequest request) {

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("User " + studentId + " is not a student");
        }

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setContent(request.getContent());
        submission.setScore(null);
        submission.setFeedback(null);

        return assignmentSubmissionRepository.save(submission);
    }

    @Transactional
    public List<AssignmentSubmission> getSubmissionsForProfessor(Long professorId) {
        return assignmentSubmissionRepository
                .findByAssignment_Lesson_Module_Course_Professor_Id(professorId);
    }
}
