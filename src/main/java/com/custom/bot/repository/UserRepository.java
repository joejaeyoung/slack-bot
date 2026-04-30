package com.custom.bot.repository;

import com.custom.bot.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByDisplayName(String displayName);
    Optional<User> findBySlackUserId(String slackUserId);
}
