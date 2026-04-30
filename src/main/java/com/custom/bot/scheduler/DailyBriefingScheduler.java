package com.custom.bot.scheduler;

import com.custom.bot.integration.slack.SlackClient;
import com.custom.bot.service.Briefing;
import com.custom.bot.service.BriefingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyBriefingScheduler {

    private final BriefingService briefingService;
    private final SlackClient slackClient;

    @Value("${slack.channel.team}")
    private String teamChannel;

    @Scheduled(cron = "0 0 9 * * MON-FRI", zone = "Asia/Seoul")
    public void sendDailyBriefing() {
        log.info("일일 브리핑 전송 시작");
        try {
            Briefing briefing = briefingService.getTeamBriefing();
            slackClient.postMessage(teamChannel, briefing.toBlocksJson());
            log.info("일일 브리핑 전송 완료");
        } catch (Exception e) {
            log.error("일일 브리핑 전송 실패", e);
        }
    }
}
