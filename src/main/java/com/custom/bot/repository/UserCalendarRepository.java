package com.custom.bot.repository;

import com.custom.bot.domain.User;
import com.custom.bot.domain.UserCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCalendarRepository extends JpaRepository<UserCalendar, Long> {
    List<UserCalendar> findByUser(User user);
    List<UserCalendar> findByUserIn(List<User> users);
}
