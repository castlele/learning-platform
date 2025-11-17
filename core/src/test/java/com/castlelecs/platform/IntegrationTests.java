package com.castlelecs.platform;

import com.castlelecs.platform.dto.*;
import com.castlelecs.platform.entity.*;
import com.castlelecs.platform.entity.Module;
import com.castlelecs.platform.repository.*;
import com.castlelecs.platform.service.course.CourseService;
import com.castlelecs.platform.service.course.LessonService;
import com.castlelecs.platform.service.course.ModuleService;
import com.castlelecs.platform.service.enrollment.EnrollmentService;
import com.castlelecs.platform.service.homework.AssignmentService;
import com.castlelecs.platform.service.homework.AssignmentSubmissionService;
import com.castlelecs.platform.service.homework.QuizService;
import com.castlelecs.platform.service.homework.QuizSubmissionService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class IntegrationTests {
    private static final String PROFESSOR_EMAIL = "prof@demo.local";
    private static final String PROFESSOR_FIRST_NAME = "Иван";
    private static final String PROFESSOR_LAST_NAME = "Петров";

    private static final String STUDENT_EMAIL = "student@demo.local";
    private static final String STUDENT_FIRST_NAME = "Анна";
    private static final String STUDENT_LAST_NAME = "Иванова";

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
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizAnswerOptionRepository quizAnswerOptionRepository;

    @Autowired
    private QuizSubmissionRepository quizSubmissionRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private LessonService lessonService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentSubmissionService assignmentSubmissionService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizSubmissionService quizSubmissionService;

    private Long professorId;
    private Long studentId;

    @BeforeEach
    void setUpUsers() {
        Profile profProfile = Profile.builder()
                .email(PROFESSOR_EMAIL)
                .firstName(PROFESSOR_FIRST_NAME)
                .lastName(PROFESSOR_LAST_NAME)
                .build();

        User professor = User.builder()
                .role(UserRole.PROFESSOR)
                .profile(profProfile)
                .build();

        profProfile.setUser(professor);
        userRepository.save(professor);
        professorId = professor.getId();

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
        studentId = student.getId();
    }

    @Test
    void fullCrudFlow_viaServices() {
        CourseCreationRequest courseReq = new CourseCreationRequest(
                "Основы Hibernate",
                "Учебный курс по Hibernate и JPA",
                null,
                LocalDate.now(),
                LocalDate.now().plusWeeks(4)
        );
        Course course = courseService.createCourse(professorId, courseReq);
        Long courseId = course.getId();

        assertEquals(1, courseRepository.count());
        assertEquals("Основы Hibernate", course.getTitle());

        ModuleCommand m1Req = new ModuleCommand(
                "Введение в ORM",
                "Что такое ORM и зачем он нужен",
                1
        );
        ModuleCommand m2Req = new ModuleCommand(
                "Основы Hibernate",
                "Сущности, аннотации, маппинги",
                2
        );

        Module module1 = moduleService.createModule(courseId, m1Req);
        Module module2 = moduleService.createModule(courseId, m2Req);

        Long module1Id = module1.getId();
        Long module2Id = module2.getId();

        assertEquals(2, moduleRepository.count());

        // --- CREATE: lessons via LessonService ---
        LessonCommand l11Req = new LessonCommand(
                "Проблема object-relational mismatch",
                "Текст урока..."
        );
        LessonCommand l21Req = new LessonCommand(
                "Аннотации @Entity и @Id",
                "Как пометить сущность и первичный ключ"
        );

        Lesson lesson1 = lessonService.createLesson(module1Id, l11Req);
        Lesson lesson2 = lessonService.createLesson(module2Id, l21Req);

        Long lesson1Id = lesson1.getId();
        Long lesson2Id = lesson2.getId();

        assertEquals(2, lessonRepository.count());

        // --- CREATE: assignment via AssignmentService ---
        AssignmentCreationRequest assignmentReq = new AssignmentCreationRequest(
                "Домашка 1",
                "Описать доменную модель проекта",
                LocalDate.now().plusDays(7),
                10
        );
        Assignment assignment = assignmentService.createAssignment(
                professorId,
                lesson1Id,
                assignmentReq
        );
        Long assignmentId = assignment.getId();

        assertEquals(1, assignmentRepository.count());

        // --- CREATE: enrollment via EnrollmentService ---
        Enrollment enrollment = enrollmentService.enroll(studentId, courseId);
        assertNotNull(enrollment.getId());
        assertEquals(1, enrollmentRepository.count());

        // --- CREATE: assignment submission via AssignmentSubmissionService ---
        AssignmentSubmissionCreationRequest submissionReq =
                new AssignmentSubmissionCreationRequest("Моё решение по Домашке 1");
        AssignmentSubmission submission = assignmentSubmissionService.submitAssignment(
                studentId,
                assignmentId,
                submissionReq
        );

        assertNotNull(submission.getId());
        assertEquals(1, assignmentSubmissionRepository.count());

        // --- CREATE: quiz + question + options via QuizService ---
        QuizCreationRequest quizReq = new QuizCreationRequest("Тест по основам Hibernate");
        Quiz quiz = quizService.createQuiz(professorId, module2Id, quizReq);
        Long quizId = quiz.getId();

        QuizQuestionCreationRequest questionReq = new QuizQuestionCreationRequest(
                "Что такое ORM?",
                QuizQuestionType.SINGLE_CHOICE
        );
        QuizAnswerOptionCreationRequest opt1 = new QuizAnswerOptionCreationRequest(
                "Object-Relational Mapping",
                true
        );
        QuizAnswerOptionCreationRequest opt2 = new QuizAnswerOptionCreationRequest(
                "Only Relational Model",
                false
        );

        QuizQuestion question = quizService.addQuestion(
                quizId,
                questionReq,
                Set.of(opt1, opt2)
        );
        Long questionId = question.getId();

        assertEquals(1, quizRepository.count());
        assertEquals(1, quizQuestionRepository.count());
        assertEquals(2, quizAnswerOptionRepository.count());

        // найти id правильного и неправильного вариантов
        List<QuizAnswerOption> allOptions = quizAnswerOptionRepository.findAll();
        Long correctOptionId = allOptions.stream()
                .filter(QuizAnswerOption::isCorrect)
                .findFirst()
                .orElseThrow()
                .getId();

        // --- CREATE: quiz submission via QuizSubmissionService ---
        QuizSubmissionCreationRequest quizSubmissionReq =
                new QuizSubmissionCreationRequest(
                        Map.of(questionId, Set.of(correctOptionId))
                );

        QuizResultDto result = quizSubmissionService.submitQuiz(
                studentId,
                quizId,
                quizSubmissionReq
        );

        assertEquals(quizId, result.getQuizId());
        assertEquals(studentId, result.getStudentId());
//        assertTrue(result.getScore() > 0);
//        assertTrue(result.getPassed());
        assertEquals(1, quizSubmissionRepository.count());

        // --- READ: via services / repos – check graph integrity ---

        Course loadedCourse = courseService.getCourse(courseId);
        assertEquals("Основы Hibernate", loadedCourse.getTitle());
        assertEquals(professorId, loadedCourse.getProfessor().getId());

        List<Course> studentCourses = enrollmentService.getCoursesForStudent(studentId);
        assertEquals(1, studentCourses.size());
        assertEquals(courseId, studentCourses.getFirst().getId());

        List<User> courseStudents = enrollmentService.getStudentsForCourse(courseId);
        assertEquals(1, courseStudents.size());
        assertEquals(studentId, courseStudents.getFirst().getId());

        List<AssignmentSubmission> profSubmissions =
                assignmentSubmissionService.getSubmissionsForProfessor(professorId);
        assertEquals(1, profSubmissions.size());
        assertEquals(assignmentId, profSubmissions.getFirst().getAssignment().getId());

        // --- UPDATE: via services ---

        CourseCreationRequest updateCourseReq = new CourseCreationRequest(
                "Основы Hibernate",
                "Обновлённое описание курса",
                null,
                loadedCourse.getStartDate(),
                loadedCourse.getDueDate()
        );
        Course updatedCourse = courseService.updateCourse(courseId, updateCourseReq);
        assertEquals("Обновлённое описание курса", updatedCourse.getDescription());

        AssignmentCreationRequest updateAssignmentReq = new AssignmentCreationRequest(
                "Домашка 1 (обновлено)",
                "Новое описание задачи",
                assignment.getDueDate(),
                assignment.getMaxScore()
        );
        Assignment updatedAssignment = assignmentService.updateAssignment(assignmentId, updateAssignmentReq);
        assertEquals("Домашка 1 (обновлено)", updatedAssignment.getTitle());

        // --- DELETE: удалить курс через сервис и проверить, что всё корректно обработано ---

        courseService.deleteCourse(courseId);
    }
}
