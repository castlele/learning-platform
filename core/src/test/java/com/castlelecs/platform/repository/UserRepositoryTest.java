package com.castlelecs.platform.repository;

import com.castlelecs.platform.entity.Profile;
import com.castlelecs.platform.entity.User;
import com.castlelecs.platform.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository sut;

    @Test
    public void testCreationOfNewUser() {
        User user = User.builder()
                .role(UserRole.ADMIN)
                .build();

        sut.save(user);
        final List<User> users = sut.findAll();

        assertEquals(1, users.size());
        assertEquals(UserRole.ADMIN, users.getFirst().getRole());
    }

    @Test
    public void testCreateStudentUser() {
        User user = User.builder()
                .role(UserRole.STUDENT)
                .build();
        Profile profile = Profile.builder()
                .bio("Test bio")
                .build();

        user.setProfile(profile);
        profile.setUser(user);
        sut.save(user);

        final List<User> users = sut.findAll();

        assertEquals(1, users.size());
    }
}
