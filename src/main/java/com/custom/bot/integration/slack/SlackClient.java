package com.custom.bot.integration.slack;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
public class SlackClient {

    @Value("${slack.bot-token}")
    private String botToken;

    public void postMessage(String channel, String blocksJson) {
        try {
            var response = Slack.getInstance().methods(botToken)
                    .chatPostMessage(req -> req
                            .channel(channel)
                            .blocksAsString(blocksJson));
            if (!response.isOk()) {
                log.error("Slack 메시지 전송 실패 - channel: {}, error: {}", channel, response.getError());
            }
        } catch (IOException | SlackApiException e) {
            log.error("Slack 메시지 전송 예외 - channel: {}", channel, e);
        }
    }

    public List<SlackMessage> fetchMessages(String channel, LocalDate date) {
        try {
            String oldest = String.valueOf(date.atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().getEpochSecond());
            String latest = String.valueOf(date.plusDays(1).atStartOfDay(ZoneId.of("Asia/Seoul")).toInstant().getEpochSecond());
            var response = Slack.getInstance().methods(botToken)
                    .conversationsHistory(req -> req
                            .channel(channel)
                            .oldest(oldest)
                            .latest(latest)
                            .limit(200));
            if (!response.isOk()) {
                log.error("Slack 메시지 조회 실패 - channel: {}, error: {}", channel, response.getError());
                return List.of();
            }
            return response.getMessages().stream()
                    .filter(m -> m.getUser() != null && m.getText() != null)
                    .map(m -> new SlackMessage(m.getTs(), m.getUser(), m.getText()))
                    .toList();
        } catch (IOException | SlackApiException e) {
            log.error("Slack 메시지 조회 예외 - channel: {}", channel, e);
            return List.of();
        }
    }

    public void postText(String channel, String text) {
        try {
            var response = Slack.getInstance().methods(botToken)
                    .chatPostMessage(req -> req
                            .channel(channel)
                            .text(text));
            if (!response.isOk()) {
                log.error("Slack 텍스트 전송 실패 - channel: {}, error: {}", channel, response.getError());
            }
        } catch (IOException | SlackApiException e) {
            log.error("Slack 텍스트 전송 예외 - channel: {}", channel, e);
        }
    }
}
