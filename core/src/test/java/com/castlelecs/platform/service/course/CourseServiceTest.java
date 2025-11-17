package com.castlelecs.platform.service.course;

import com.castlelecs.platform.dto.CourseCreationRequest;
import com.castlelecs.platform.entity.Course;
import com.castlelecs.platform.entity.Profile;
import com.castlelecs.platform.entity.User;
import com.castlelecs.platform.entity.UserRole;
import com.castlelecs.platform.repository.CourseRepository;
import com.castlelecs.platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class CourseServiceTest {

    private static String PROFESSOR_EMAIL = "someemail@somemail.do";
    private static String PROFESSOR_FIRST_NAME = "Name";
    private static String PROFESSOR_LAST_NAME = "NotName";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService sut;

    @BeforeEach
    public void createProfessor() {
        Profile profile = Profile.builder()
                .email(PROFESSOR_EMAIL)
                .firstName(PROFESSOR_FIRST_NAME)
                .lastName(PROFESSOR_LAST_NAME)
                .build();

        User user = User.builder()
                .role(UserRole.PROFESSOR)
                .profile(profile)
                .build();

        profile.setUser(user);
        userRepository.save(user);
    }

    @Test
    public void testCourseCreation() {
        assertEquals(1, userRepository.findAll().size());

        Long professorId = userRepository.findAll().getFirst().getProfile().getId();
        CourseCreationRequest request = new CourseCreationRequest(
                "ORM",
                "Nothing here",
                null,
                LocalDate.now(),
                LocalDate.now()
        );

        Course course = sut.createCourse(professorId, request);

        assertEquals("ORM", course.getTitle());
        assertEquals(1, courseRepository.findAll().size());
        assertEquals(course.getTitle(), courseRepository.findAll().getFirst().getTitle());
    }

    @Test
    public void testCourseUpdate() {
        assertEquals(1, userRepository.findAll().size());

        Long professorId = userRepository.findAll().getFirst().getProfile().getId();
        User professor = userRepository.findById(professorId).get();
        Course oldCourse = Course.builder()
                .professor(professor)
                .title("NONE")
                .description("NONE")
                .startDate(LocalDate.now())
                .dueDate(LocalDate.now())
                .build();
        courseRepository.save(oldCourse);
        CourseCreationRequest updateRequest = new CourseCreationRequest(
                "ORM",
                "Nothing here",
                null,
                LocalDate.now(),
                LocalDate.now()
        );

        Course course = sut.updateCourse(professorId, updateRequest);

        assertEquals("ORM", course.getTitle());
        assertEquals(1, courseRepository.findAll().size());
        assertEquals(course.getTitle(), courseRepository.findAll().getFirst().getTitle());
    }
}
