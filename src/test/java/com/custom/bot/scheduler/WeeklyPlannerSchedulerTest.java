package com.custom.bot.scheduler;

import com.custom.bot.integration.slack.SlackClient;
import com.custom.bot.repository.ScrumEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeeklyPlannerSchedulerTest {

    @Mock ScrumEntryRepository scrumEntryRepository;
    @Mock SlackClient slackClient;

    @InjectMocks WeeklyPlannerScheduler scheduler;

    @BeforeEach
    void setUp() throws Exception {
        var f = WeeklyPlannerScheduler.class.getDeclaredField("teamChannel");
        f.setAccessible(true);
        f.set(scheduler, "C_TEST_TEAM");
    }

    @Test
    void 이번주_스크럼_기록_없으면_빈_요약_전송() {
        when(scrumEntryRepository.findByPostedDateBetween(any(), any())).thenReturn(List.of());

        scheduler.sendWeeklySummary();

        verify(slackClient).postText(eq("C_TEST_TEAM"), anyString());
    }

    @Test
    void 예외_발생시_정상_종료() {
        when(scrumEntryRepository.findByPostedDateBetween(any(), any())).thenThrow(new RuntimeException("DB 오류"));

        assertThatNoException().isThrownBy(() -> scheduler.sendWeeklySummary());
        verifyNoInteractions(slackClient);
    }
}
