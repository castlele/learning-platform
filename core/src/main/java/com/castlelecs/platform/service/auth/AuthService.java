package com.castlelecs.platform.service.auth;

import com.castlelecs.platform.dto.ProfessorRegistrationForm;
import com.castlelecs.platform.dto.StudentRegistrationForm;
import com.castlelecs.platform.entity.Profile;
import com.castlelecs.platform.entity.User;
import com.castlelecs.platform.entity.UserRole;
import com.castlelecs.platform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    User registerStudent(StudentRegistrationForm form) {
        User user = createUser(
                form.getEmail(),
                form.getFirstName(),
                form.getLastName(),
                UserRole.STUDENT
        );
        userRepository.save(user);

        return user;
    }

    @Transactional
    User registerProfessor(ProfessorRegistrationForm form) {
        User user = createUser(
                form.getEmail(),
                form.getFirstName(),
                form.getLastName(),
                UserRole.PROFESSOR
        );
        userRepository.save(user);

        return user;
    }

    private User createUser(String email, String firstName, String lastName, UserRole role) {
        Profile profile = Profile.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build();

        User user = User.builder()
                .role(role)
                .profile(profile)
                .build();

        profile.setUser(user);

        return user;
    }
}
