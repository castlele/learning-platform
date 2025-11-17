package com.castlelecs.platform.service.enrollment;

import com.castlelecs.platform.dto.CourseCreationRequest;
import com.castlelecs.platform.entity.*;
import com.castlelecs.platform.repository.CourseRepository;
import com.castlelecs.platform.repository.EnrollmentRepository;
import com.castlelecs.platform.repository.UserRepository;
import com.castlelecs.platform.service.course.CourseService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class EnrollmentServiceTest {


    private static final String PROFESSOR_EMAIL = "prof@platform.test";
    private static final String PROFESSOR_FIRST_NAME = "Prof";
    private static final String PROFESSOR_LAST_NAME = "Essor";

    private static final String STUDENT_EMAIL = "student@platform.test";
    private static final String STUDENT_FIRST_NAME = "Stud";
    private static final String STUDENT_LAST_NAME = "Ent";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EnrollmentService sut;

    private Long studentId;
    private Long courseId;

    @BeforeEach
    public void setUp() {
        Profile professorProfile = Profile.builder()
                .email(PROFESSOR_EMAIL)
                .firstName(PROFESSOR_FIRST_NAME)
                .lastName(PROFESSOR_LAST_NAME)
                .build();

        User professor = User.builder()
                .role(UserRole.PROFESSOR)
                .profile(professorProfile)
                .build();

        professorProfile.setUser(professor);
        userRepository.save(professor);

        CourseCreationRequest courseRequest = new CourseCreationRequest(
                "ORM",
                "Nothing here",
                null,
                LocalDate.now(),
                LocalDate.now()
        );

        Course course = courseService.createCourse(professor.getId(), courseRequest);
        this.courseId = course.getId();

        Profile studentProfile = Profile.builder()
                .email(STUDENT_EMAIL)
                .firstName(STUDENT_FIRST_NAME)
                .lastName(STUDENT_LAST_NAME)
                .build();

        User student = User.builder()
                .role(UserRole.STUDENT)
                .profile(studentProfile)
                .build();

        studentProfile.setUser(student);
        userRepository.save(student);
        this.studentId = student.getId();
    }

    @Test
    public void testEnrollStudentToCourse() {
        assertEquals(0, enrollmentRepository.findAll().size());

        Enrollment enrollment = sut.enroll(studentId, courseId);

        assertNotNull(enrollment.getId());
        assertEquals(studentId, enrollment.getStudent().getId());
        assertEquals(courseId, enrollment.getCourse().getId());
        assertEquals(EnrollmentStatus.ACTIVE, enrollment.getStatus());

        List<Enrollment> all = enrollmentRepository.findAll();
        assertEquals(1, all.size());
        assertEquals(studentId, all.getFirst().getStudent().getId());
    }

    @Test
    public void testUnenrollStudentFromCourse() {
        sut.enroll(studentId, courseId);
        assertEquals(1, enrollmentRepository.findAll().size());

        sut.unenroll(studentId, courseId);

        assertEquals(0, enrollmentRepository.findAll().size());
    }

    @Test
    public void testGetCoursesForStudent() {
        sut.enroll(studentId, courseId);

        List<Course> courses = sut.getCoursesForStudent(studentId);

        assertEquals(1, courses.size());
        assertEquals(courseId, courses.getFirst().getId());
    }

    @Test
    public void testGetStudentsForCourse() {
        sut.enroll(studentId, courseId);

        List<User> students = sut.getStudentsForCourse(courseId);

        assertEquals(1, students.size());
        assertEquals(studentId, students.getFirst().getId());
    }
}