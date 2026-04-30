package com.custom.bot.scheduler;

import com.custom.bot.domain.ScrumEntry;
import com.custom.bot.integration.slack.SlackClient;
import com.custom.bot.repository.ScrumEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyPlannerScheduler {

    private final ScrumEntryRepository scrumEntryRepository;
    private final SlackClient slackClient;

    @Value("${slack.channel.team}")
    private String teamChannel;

    @Transactional(readOnly = true)
    @Scheduled(cron = "0 0 17 * * FRI", zone = "Asia/Seoul")
    public void sendWeeklySummary() {
        log.info("주간 요약 전송 시작");
        try {
            LocalDate today = LocalDate.now();
            LocalDate monday = today.with(DayOfWeek.MONDAY);
            List<ScrumEntry> entries = scrumEntryRepository.findByPostedDateBetween(monday, today);
            slackClient.postText(teamChannel, buildWeeklySummary(today, entries));
            log.info("주간 요약 전송 완료");
        } catch (Exception e) {
            log.error("주간 요약 전송 실패", e);
        }
    }

    private String buildWeeklySummary(LocalDate week, List<ScrumEntry> entries) {
        String header = ":memo: *이번 주 스크럼 요약 (" + week.format(DateTimeFormatter.ofPattern("M/d")) + " 기준)*\n";
        if (entries.isEmpty()) {
            return header + "> 이번 주 스크럼 기록 없음";
        }
        StringBuilder sb = new StringBuilder(header);
        entries.stream()
               .collect(Collectors.groupingBy(e -> e.getUser().getDisplayName()))
               .forEach((name, list) -> {
                   sb.append("\n*").append(name).append("*\n");
                   list.forEach(e -> sb.append("> `").append(e.getPostedDate()).append("` ").append(e.getText()).append("\n"));
               });
        return sb.toString();
    }
}
