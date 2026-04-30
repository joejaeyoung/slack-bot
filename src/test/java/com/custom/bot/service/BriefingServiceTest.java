package com.custom.bot.service;

import com.custom.bot.domain.User;
import com.custom.bot.integration.google.GoogleCalendarClient;
import com.custom.bot.integration.jira.JiraClient;
import com.custom.bot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BriefingServiceTest {

    @Mock UserRepository userRepository;
    @Mock GoogleCalendarClient calendarClient;
    @Mock JiraClient jiraClient;

    @InjectMocks
    BriefingService briefingService;

    @BeforeEach
    void injectExecutor() throws Exception {
        Field field = BriefingService.class.getDeclaredField("apiExecutor");
        field.setAccessible(true);
        field.set(briefingService, Executors.newSingleThreadExecutor());
    }

    private User makeUser(String name) {
        return new User(name, "U_" + name, "JIRA_" + name, name + "@gmail.com", "token");
    }

    @Test
    void getTeamBriefing_팀원_일정과_Jira_병렬_fetch() {
        User user = makeUser("조재영");
        CalendarEvent event = new CalendarEvent("스탠드업", OffsetDateTime.now(), OffsetDateTime.now().plusHours(1), null);
        JiraIssueSummary issue = new JiraIssueSummary("PROJ-1", "버그 수정", "조재영", "In Progress", LocalDate.now());

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(calendarClient.fetchEvents(anyString(), anyString(), any())).thenReturn(List.of(event));
        when(jiraClient.findIssuesDueOn(any())).thenReturn(List.of(issue));

        Briefing briefing = briefingService.getTeamBriefing();

        assertThat(briefing.personalSchedules()).containsKey(user);
        assertThat(briefing.jiraDueIssues()).hasSize(1);
    }

    @Test
    void getPersonalBriefing_개인_일정과_Jira_병렬_fetch() {
        User user = makeUser("권태화");
        when(calendarClient.fetchEvents(anyString(), anyString(), any())).thenReturn(List.of());
        when(jiraClient.findIssuesAssignedTo(anyString(), any())).thenReturn(List.of());

        Briefing briefing = briefingService.getPersonalBriefing(user);

        assertThat(briefing.personalSchedules()).containsKey(user);
        assertThat(briefing.jiraDueIssues()).isEmpty();
    }

    @Test
    void toBlocksJson_유효한_JSON_반환() {
        User user = makeUser("안수빈");
        Briefing briefing = new Briefing(LocalDate.now(), Map.of(user, List.of()), List.of());

        String json = briefing.toBlocksJson();

        assertThat(json).startsWith("[").endsWith("]");
        assertThat(json).contains("팀 브리핑");
        assertThat(json).contains("안수빈");
    }
}
