package com.custom.bot.service;

import com.custom.bot.domain.User;
import com.custom.bot.integration.google.GoogleCalendarClient;
import com.custom.bot.integration.jira.JiraClient;
import com.custom.bot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BriefingService {

    private final UserRepository userRepository;
    private final GoogleCalendarClient calendarClient;
    private final JiraClient jiraClient;
    @Qualifier("apiExecutor")
    private final Executor apiExecutor;

    public Briefing getTeamBriefing() {
        List<User> users = userRepository.findAll();
        LocalDate today = LocalDate.now();

        List<CompletableFuture<Map.Entry<User, List<CalendarEvent>>>> calFutures = users.stream()
                .map(user -> CompletableFuture.supplyAsync(
                        () -> Map.entry(user, calendarClient.fetchEvents(
                                user.getGoogleCalendarId(), user.getGoogleRefreshToken(), today)),
                        apiExecutor))
                .toList();

        CompletableFuture<List<JiraIssueSummary>> jiraFuture =
                CompletableFuture.supplyAsync(() -> jiraClient.findIssuesDueOn(today), apiExecutor);

        CompletableFuture.allOf(
                CompletableFuture.allOf(calFutures.toArray(new CompletableFuture[0])),
                jiraFuture
        ).join();

        Map<User, List<CalendarEvent>> schedules = calFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new Briefing(today, schedules, jiraFuture.join());
    }

    public Briefing getPersonalBriefing(User user) {
        LocalDate today = LocalDate.now();

        CompletableFuture<List<CalendarEvent>> calFuture = CompletableFuture.supplyAsync(
                () -> calendarClient.fetchEvents(user.getGoogleCalendarId(), user.getGoogleRefreshToken(), today),
                apiExecutor);

        CompletableFuture<List<JiraIssueSummary>> jiraFuture = CompletableFuture.supplyAsync(
                () -> jiraClient.findIssuesAssignedTo(user.getJiraAccountId(), today),
                apiExecutor);

        CompletableFuture.allOf(calFuture, jiraFuture).join();

        return new Briefing(today, Map.of(user, calFuture.join()), jiraFuture.join());
    }
}
