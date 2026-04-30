package com.custom.bot.integration.slack;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
