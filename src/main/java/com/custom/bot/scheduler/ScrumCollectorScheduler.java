package com.custom.bot.scheduler;

import com.custom.bot.domain.ScrumEntry;
import com.custom.bot.integration.slack.SlackClient;
import com.custom.bot.integration.slack.SlackMessage;
import com.custom.bot.repository.ScrumEntryRepository;
import com.custom.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScrumCollectorScheduler {

    private final SlackClient slackClient;
    private final UserRepository userRepository;
    private final ScrumEntryRepository scrumEntryRepository;

    @Value("${slack.channel.scrum}")
    private String scrumChannel;

    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")
    public void collectScrumEntries() {
        log.info("스크럼 수집 시작");
        LocalDate today = LocalDate.now();
        List<SlackMessage> messages = slackClient.fetchMessages(scrumChannel, today);
        int saved = 0;
        for (SlackMessage msg : messages) {
            if (scrumEntryRepository.existsBySlackTs(msg.ts())) continue;
            var userOpt = userRepository.findBySlackUserId(msg.userId());
            if (userOpt.isEmpty()) continue;
            List<String> jiraKeys = extractJiraKeys(msg.text());
            scrumEntryRepository.save(new ScrumEntry(userOpt.get(), msg.ts(), msg.text(), jiraKeys, today));
            saved++;
        }
        log.info("스크럼 수집 완료: {}건", saved);
    }

    private List<String> extractJiraKeys(String text) {
        Matcher matcher = Pattern.compile("[A-Z]+-\\d+").matcher(text);
        List<String> keys = new ArrayList<>();
        while (matcher.find()) keys.add(matcher.group());
        return keys;
    }
}
