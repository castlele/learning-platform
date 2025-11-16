package com.castlelecs.platform.repository;

import com.castlelecs.platform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
