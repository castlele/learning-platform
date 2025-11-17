package com.castlelecs.platform.service.homework;

import com.castlelecs.platform.dto.*;
import com.castlelecs.platform.entity.*;
import com.castlelecs.platform.entity.Module;
import com.castlelecs.platform.repository.*;
import com.castlelecs.platform.service.course.CourseService;
import com.castlelecs.platform.service.course.LessonService;
import com.castlelecs.platform.service.course.ModuleService;
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
public class AssignmentSubmissionServiceTest {
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
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentSubmissionService sut;

    private Long professorId;
    private Long studentId;
    private Long assignmentId;

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
        this.professorId = professor.getId();

        CourseCreationRequest courseRequest = new CourseCreationRequest(
                "ORM",
                "Nothing here",
                null,
                LocalDate.now(),
                LocalDate.now()
        );
        Course course = courseService.createCourse(professorId, courseRequest);

        ModuleCommand moduleRequest = new ModuleCommand(
                "Module 1",
                "Intro",
                1
        );
        Module module = moduleService.createModule(course.getId(), moduleRequest);

        LessonCommand lessonRequest = new LessonCommand(
                "Lesson 1",
                "Lesson content"
        );
        Lesson lesson = lessonService.createLesson(module.getId(), lessonRequest);

        AssignmentCreationRequest assignmentRequest = new AssignmentCreationRequest(
                "HW 1",
                "Do something",
                LocalDate.now().plusDays(7),
                100
        );
        Assignment assignment = assignmentService.createAssignment(professorId, lesson.getId(), assignmentRequest);
        this.assignmentId = assignment.getId();

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
    public void testSubmitAssignment() {
        assertEquals(0, assignmentSubmissionRepository.findAll().size());

        AssignmentSubmissionCreationRequest request = new AssignmentSubmissionCreationRequest(
                "My awesome solution"
        );

        AssignmentSubmission submission = sut.submitAssignment(studentId, assignmentId, request);

        assertNotNull(submission.getId());
        assertEquals("My awesome solution", submission.getContent());
        assertEquals(studentId, submission.getStudent().getId());
        assertEquals(assignmentId, submission.getAssignment().getId());

        assertEquals(1, assignmentSubmissionRepository.findAll().size());
    }

    @Test
    public void testGetSubmissionsForProfessor() {
        AssignmentSubmissionCreationRequest request = new AssignmentSubmissionCreationRequest(
                "My awesome solution"
        );
        sut.submitAssignment(studentId, assignmentId, request);

        List<AssignmentSubmission> submissions = sut.getSubmissionsForProfessor(professorId);

        assertEquals(1, submissions.size());
        AssignmentSubmission s = submissions.getFirst();
        assertEquals(studentId, s.getStudent().getId());
        assertEquals(assignmentId, s.getAssignment().getId());
        assertEquals("My awesome solution", s.getContent());
    }
}
