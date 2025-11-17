package com.castlelecs.platform.service.homework;

import com.castlelecs.platform.entity.Module;
import com.castlelecs.platform.dto.AssignmentCreationRequest;
import com.castlelecs.platform.dto.CourseCreationRequest;
import com.castlelecs.platform.dto.LessonCommand;
import com.castlelecs.platform.dto.ModuleCommand;
import com.castlelecs.platform.entity.*;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class AssignmentServiceTest {

    private static final String PROFESSOR_EMAIL = "prof@platform.test";
    private static final String PROFESSOR_FIRST_NAME = "Prof";
    private static final String PROFESSOR_LAST_NAME = "Essor";

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
    private CourseService courseService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private AssignmentService sut;

    private Long professorId;
    private Long lessonId;

    @BeforeEach
    public void setUp() {
        Profile profile = Profile.builder()
                .email(PROFESSOR_EMAIL)
                .firstName(PROFESSOR_FIRST_NAME)
                .lastName(PROFESSOR_LAST_NAME)
                .build();

        User professor = User.builder()
                .role(UserRole.PROFESSOR)
                .profile(profile)
                .build();

        profile.setUser(professor);
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
        this.lessonId = lesson.getId();
    }

    @Test
    public void testAssignmentCreation() {
        assertEquals(0, assignmentRepository.findAll().size());

        AssignmentCreationRequest request = new AssignmentCreationRequest(
                "HW 1",
                "Do something",
                LocalDate.now().plusDays(7),
                100
        );

        Assignment assignment = sut.createAssignment(professorId, lessonId, request);

        assertNotNull(assignment.getId());
        assertEquals("HW 1", assignment.getTitle());
        assertEquals(1, assignmentRepository.findAll().size());
        assertEquals(assignment.getTitle(), assignmentRepository.findAll().getFirst().getTitle());
    }

    @Test
    public void testAssignmentUpdate() {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();

        Assignment oldAssignment = Assignment.builder()
                .lesson(lesson)
                .title("OLD")
                .description("OLD")
                .dueDate(LocalDate.now().plusDays(3))
                .maxScore(50)
                .build();
        assignmentRepository.save(oldAssignment);

        AssignmentCreationRequest updateRequest = new AssignmentCreationRequest(
                "Updated HW",
                "Updated desc",
                LocalDate.now().plusDays(10),
                75
        );

        Assignment assignment = sut.updateAssignment(oldAssignment.getId(), updateRequest);

        assertEquals("Updated HW", assignment.getTitle());
        assertEquals(1, assignmentRepository.findAll().size());
        assertEquals(assignment.getTitle(), assignmentRepository.findAll().getFirst().getTitle());
    }
}
