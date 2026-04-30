package com.custom.bot.repository;

import com.custom.bot.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void findByDisplayName_존재하는_이름_반환() {
        userRepository.save(new User("조재영", "U_SLACK_JJY", "JIRA_JJY", "jjy@gmail.com", "token"));

        assertThat(userRepository.findByDisplayName("조재영")).isPresent();
    }

    @Test
    void findByDisplayName_없는_이름_빈값_반환() {
        assertThat(userRepository.findByDisplayName("없는사람")).isEmpty();
    }

    @Test
    void findBySlackUserId_존재하는_ID_반환() {
        userRepository.save(new User("권태화", "U_SLACK_KTH", "JIRA_KTH", "kth@gmail.com", "token"));

        assertThat(userRepository.findBySlackUserId("U_SLACK_KTH")).isPresent();
    }
}
