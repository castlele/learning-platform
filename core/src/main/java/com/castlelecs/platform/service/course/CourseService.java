package com.castlelecs.platform.service.course;

import com.castlelecs.platform.dto.CourseCreationRequest;
import com.castlelecs.platform.entity.Course;
import com.castlelecs.platform.entity.CourseCategory;
import com.castlelecs.platform.entity.User;
import com.castlelecs.platform.entity.UserRole;
import com.castlelecs.platform.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseCategoryRepository courseCategoryRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional
    public Course createCourse(Long professorId, CourseCreationRequest request) {
        User professor = userRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor not found: " + professorId));

        if (professor.getRole() != UserRole.PROFESSOR) {
            throw new IllegalStateException("User " + professorId + " is not a professor");
        }

        CourseCategory category = null;

        if (request.getCategoryId() != null) {
            category = courseCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + request.getCategoryId()));
        }

        Course course = Course.builder()
                .professor(professor)
                .title(request.getTitle())
                .category(category)
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .build();

        return courseRepository.save(course);
    }

    @Transactional
    public Course updateCourse(Long courseId, CourseCreationRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        if (request.getTitle() != null) {
            course.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }

        if (request.getStartDate() != null) {
            course.setStartDate(request.getStartDate());
        }

        if (request.getDueDate() != null) {
            course.setDueDate(request.getDueDate());
        }

        if (request.getCategoryId() != null) {
            CourseCategory category = courseCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + request.getCategoryId()));
            course.setCategory(category);
        }

        return courseRepository.save(course);
    }


    @Transactional
    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new IllegalArgumentException("Course not found: " + courseId);
        }

        enrollmentRepository.deleteByCourseId(courseId);
        courseRepository.deleteById(courseId);
    }

    @Transactional
    public Course getCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));
    }

    @Transactional
    public Set<Course> getCoursesForProfessor(Long professorId) {
        List<Course> courses = courseRepository.findByProfessorId(professorId);
        return new HashSet<>(courses);
    }
}
