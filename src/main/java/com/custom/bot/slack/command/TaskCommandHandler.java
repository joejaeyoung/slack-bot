package com.custom.bot.slack.command;

import com.slack.api.bolt.App;
import com.custom.bot.domain.User;
import com.custom.bot.integration.jira.JiraClient;
import com.custom.bot.repository.UserRepository;
import com.custom.bot.service.JiraIssueSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskCommandHandler {

    private final JiraClient jiraClient;
    private final UserRepository userRepository;
    @Qualifier("apiExecutor")
    private final Executor apiExecutor;

    private static final List<String> MEMBERS = List.of("조재영", "권태화", "안수빈");

    public void register(App app) {
        app.command("/task-team", (req, ctx) -> {
            apiExecutor.execute(() -> {
                try {
                    List<JiraIssueSummary> issues = jiraClient.findIssuesDueOn(LocalDate.now());
                    ctx.respond(buildIssueText("팀 전체", issues));
                } catch (Exception e) {
                    log.error("/task-team 응답 실패", e);
                }
            });
            return ctx.ack();
        });

        for (String name : MEMBERS) {
            String memberName = name;
            app.command("/task-" + memberName, (req, ctx) -> {
                User user = userRepository.findByDisplayName(memberName).orElse(null);
                if (user == null) {
                    return ctx.ack(":x: `" + memberName + "` 을(를) 찾을 수 없습니다.");
                }
                apiExecutor.execute(() -> {
                    try {
                        List<JiraIssueSummary> issues = jiraClient.findIssuesAssignedTo(
                                user.getJiraAccountId(), LocalDate.now());
                        ctx.respond(buildIssueText(memberName, issues));
                    } catch (Exception e) {
                        log.error("/task-{} 응답 실패", memberName, e);
                    }
                });
                return ctx.ack();
            });
        }
    }

    private String buildIssueText(String target, List<JiraIssueSummary> issues) {
        if (issues.isEmpty()) {
            return ":white_check_mark: *" + target + "* — 진행 중인 이슈 없음";
        }
        StringBuilder sb = new StringBuilder(":jira: *" + target + " Jira 이슈*\n");
        for (JiraIssueSummary issue : issues) {
            sb.append("> `").append(issue.key()).append("` ")
              .append(issue.summary())
              .append(" (").append(issue.status()).append(")")
              .append(" — ").append(issue.assignee()).append("\n");
        }
        return sb.toString();
    }
}
