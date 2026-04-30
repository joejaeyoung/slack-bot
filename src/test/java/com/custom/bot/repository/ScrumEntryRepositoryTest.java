package com.custom.bot.repository;

import com.custom.bot.domain.ScrumEntry;
import com.custom.bot.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ScrumEntryRepositoryTest {

    @Autowired
    ScrumEntryRepository scrumEntryRepository;

    @Autowired
    UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User("안수빈", "U_SLACK_ASB", "JIRA_ASB", "asb@gmail.com", "token"));
    }

    @Test
    void existsBySlackTs_중복_저장_방지() {
        scrumEntryRepository.save(new ScrumEntry(user, "ts_001", "오늘 한 일", List.of("PROJ-1"), LocalDate.now()));

        assertThat(scrumEntryRepository.existsBySlackTs("ts_001")).isTrue();
        assertThat(scrumEntryRepository.existsBySlackTs("ts_999")).isFalse();
    }

    @Test
    void findByPostedDateBetween_날짜_범위_조회() {
        LocalDate today = LocalDate.now();
        scrumEntryRepository.save(new ScrumEntry(user, "ts_001", "월요일", List.of(), today.minusDays(1)));
        scrumEntryRepository.save(new ScrumEntry(user, "ts_002", "화요일", List.of(), today));

        List<ScrumEntry> result = scrumEntryRepository.findByPostedDateBetween(today.minusDays(1), today);

        assertThat(result).hasSize(2);
    }
}
