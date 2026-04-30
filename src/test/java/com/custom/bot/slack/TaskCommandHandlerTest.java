package com.custom.bot.slack;

import com.custom.bot.domain.User;
import com.custom.bot.integration.jira.JiraClient;
import com.custom.bot.repository.UserRepository;
import com.custom.bot.service.JiraIssueSummary;
import com.custom.bot.slack.command.TaskCommandHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskCommandHandlerTest {

    @Mock JiraClient jiraClient;
    @Mock UserRepository userRepository;

    @InjectMocks
    TaskCommandHandler handler;

    @Test
    void buildIssueText_이슈_없을때_완료_메시지() throws Exception {
        var method = TaskCommandHandler.class.getDeclaredMethod("buildIssueText", String.class, List.class);
        method.setAccessible(true);
        String result = (String) method.invoke(handler, "팀 전체", List.of());

        assertThat(result).contains("이슈 없음");
    }

    @Test
    void buildIssueText_이슈_있을때_목록_포함() throws Exception {
        JiraIssueSummary issue = new JiraIssueSummary("PROJ-1", "버그 수정", "조재영", "In Progress", LocalDate.now());

        var method = TaskCommandHandler.class.getDeclaredMethod("buildIssueText", String.class, List.class);
        method.setAccessible(true);
        String result = (String) method.invoke(handler, "조재영", List.of(issue));

        assertThat(result).contains("PROJ-1");
        assertThat(result).contains("버그 수정");
        assertThat(result).contains("조재영");
    }
}
