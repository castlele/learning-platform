package com.castlelecs.platform.service.auth;

import com.castlelecs.platform.dto.ProfessorRegistrationForm;
import com.castlelecs.platform.dto.StudentRegistrationForm;
import com.castlelecs.platform.entity.User;
import com.castlelecs.platform.entity.UserRole;
import com.castlelecs.platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class AuthServiceTest {
    @Autowired
    private AuthService sut;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testNewStudentRegistration() {
        StudentRegistrationForm form = new StudentRegistrationForm(
                "richardcastle@somemail.io",
                "Richard",
                "Castle"
        );

        User user = sut.registerStudent(form);

        assertEquals(1, userRepository.findAll().size());
        assertEquals(UserRole.STUDENT, user.getRole());
        assertEquals(
                user.getProfile().getEmail(),
                userRepository.findAll().getFirst().getProfile().getEmail()
        );
    }

    @Test
    public void testNewProfessorRegistration() {
        ProfessorRegistrationForm form = new ProfessorRegistrationForm(
                "richardcastle@somemail.io",
                "Richard",
                "Castle"
        );

        User user = sut.registerProfessor(form);

        assertEquals(1, userRepository.findAll().size());
        assertEquals(UserRole.PROFESSOR, user.getRole());
        assertEquals(
                user.getProfile().getEmail(),
                userRepository.findAll().getFirst().getProfile().getEmail()
        );
    }
}
