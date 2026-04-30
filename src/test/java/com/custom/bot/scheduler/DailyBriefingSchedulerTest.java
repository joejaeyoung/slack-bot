package com.custom.bot.scheduler;

import com.custom.bot.integration.slack.SlackClient;
import com.custom.bot.service.Briefing;
import com.custom.bot.service.BriefingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyBriefingSchedulerTest {

    @Mock BriefingService briefingService;
    @Mock SlackClient slackClient;

    @InjectMocks DailyBriefingScheduler scheduler;

    @BeforeEach
    void setUp() throws Exception {
        var f = DailyBriefingScheduler.class.getDeclaredField("teamChannel");
        f.setAccessible(true);
        f.set(scheduler, "C_TEST_TEAM");
    }

    @Test
    void 브리핑_정상_전송() {
        Briefing briefing = mock(Briefing.class);
        when(briefing.toBlocksJson()).thenReturn("[]");
        when(briefingService.getTeamBriefing()).thenReturn(briefing);

        scheduler.sendDailyBriefing();

        verify(slackClient).postMessage("C_TEST_TEAM", "[]");
    }

    @Test
    void 브리핑_예외_발생시_정상_종료() {
        when(briefingService.getTeamBriefing()).thenThrow(new RuntimeException("API 오류"));

        assertThatNoException().isThrownBy(() -> scheduler.sendDailyBriefing());
        verifyNoInteractions(slackClient);
    }
}
