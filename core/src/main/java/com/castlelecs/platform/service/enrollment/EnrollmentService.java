package com.castlelecs.platform.service.enrollment;

import com.castlelecs.platform.entity.*;
import com.castlelecs.platform.repository.CourseRepository;
import com.castlelecs.platform.repository.EnrollmentRepository;
import com.castlelecs.platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;


    @Transactional
    public Enrollment enroll(Long studentId, Long courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        if (student.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("User " + studentId + " is not a student");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new IllegalStateException("Student is already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .enrollDate(LocalDate.now())
                .status(EnrollmentStatus.ACTIVE)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public void unenroll(Long studentId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found"));

        enrollmentRepository.delete(enrollment);
    }

    @Transactional
    public List<Course> getCoursesForStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(Enrollment::getCourse)
                .toList();
    }

    @Transactional
    public List<User> getStudentsForCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(Enrollment::getStudent)
                .toList();
    }
}
