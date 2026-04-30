package com.custom.bot.config;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.custom.bot.slack.command.CalCommandHandler;
import com.custom.bot.slack.command.TaskCommandHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfig {

    @Value("${slack.bot-token}")
    private String botToken;

    @Value("${slack.signing-secret}")
    private String signingSecret;

    @Bean
    public AppConfig slackAppConfig() {
        return AppConfig.builder()
                .singleTeamBotToken(botToken)
                .signingSecret(signingSecret)
                .build();
    }

    @Bean
    public App slackApp(AppConfig config,
                        CalCommandHandler calHandler,
                        TaskCommandHandler taskHandler) {
        App app = new App(config);
        calHandler.register(app);
        taskHandler.register(app);
        return app;
    }
}
