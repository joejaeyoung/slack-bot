package com.custom.bot.scheduler;

import com.custom.bot.domain.ScrumEntry;
import com.custom.bot.domain.User;
import com.custom.bot.integration.slack.SlackClient;
import com.custom.bot.integration.slack.SlackMessage;
import com.custom.bot.repository.ScrumEntryRepository;
import com.custom.bot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScrumCollectorSchedulerTest {

    @Mock SlackClient slackClient;
    @Mock UserRepository userRepository;
    @Mock ScrumEntryRepository scrumEntryRepository;

    @InjectMocks ScrumCollectorScheduler scheduler;

    @BeforeEach
    void setUp() throws Exception {
        var f = ScrumCollectorScheduler.class.getDeclaredField("scrumChannel");
        f.setAccessible(true);
        f.set(scheduler, "C_TEST_SCRUM");
    }

    @Test
    void 신규_스크럼_메시지_저장() {
        SlackMessage msg = new SlackMessage("ts-1", "U001", "PROJ-1 작업 완료");
        User user = new User("조재영", "U001", "jira001", "cal001", "refresh001");
        when(slackClient.fetchMessages(anyString(), any())).thenReturn(List.of(msg));
        when(scrumEntryRepository.existsBySlackTs("ts-1")).thenReturn(false);
        when(userRepository.findBySlackUserId("U001")).thenReturn(Optional.of(user));

        scheduler.collectScrumEntries();

        verify(scrumEntryRepository).save(any(ScrumEntry.class));
    }

    @Test
    void 이미_저장된_메시지는_스킵() {
        SlackMessage msg = new SlackMessage("ts-1", "U001", "PROJ-1 작업");
        when(slackClient.fetchMessages(anyString(), any())).thenReturn(List.of(msg));
        when(scrumEntryRepository.existsBySlackTs("ts-1")).thenReturn(true);

        scheduler.collectScrumEntries();

        verify(scrumEntryRepository, never()).save(any());
    }

    @Test
    void 알수없는_사용자_메시지는_스킵() {
        SlackMessage msg = new SlackMessage("ts-2", "U999", "모르는 사람");
        when(slackClient.fetchMessages(anyString(), any())).thenReturn(List.of(msg));
        when(scrumEntryRepository.existsBySlackTs("ts-2")).thenReturn(false);
        when(userRepository.findBySlackUserId("U999")).thenReturn(Optional.empty());

        scheduler.collectScrumEntries();

        verify(scrumEntryRepository, never()).save(any());
    }
}
