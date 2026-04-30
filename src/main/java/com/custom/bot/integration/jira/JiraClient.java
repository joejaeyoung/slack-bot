package com.custom.bot.integration.jira;

import com.custom.bot.service.JiraIssueSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JiraClient {

    private static final String ISSUE_FIELDS = "summary,duedate,status,assignee";

    private final JiraApi jiraApi;

    public List<JiraIssueSummary> findIssuesDueOn(LocalDate date) {
        String jql = String.format("duedate = \"%s\" AND assignee is not EMPTY", date);
        return search(jql);
    }

    public List<JiraIssueSummary> findIssuesAssignedTo(String accountId, LocalDate date) {
        String jql = String.format(
                "assignee = \"%s\" AND duedate >= \"%s\" ORDER BY duedate ASC", accountId, date);
        return search(jql);
    }

    private List<JiraIssueSummary> search(String jql) {
        try {
            var response = jiraApi.searchIssues(jql, ISSUE_FIELDS).execute();
            if (!response.isSuccessful() || response.body() == null) {
                log.error("Jira API 오류 - code: {}", response.code());
                return List.of();
            }
            var issues = response.body().issues();
            if (issues == null) return List.of();
            return issues.stream().map(this::toSummary).toList();
        } catch (Exception e) {
            log.error("Jira 검색 실패 - jql: {}", jql, e);
            return List.of();
        }
    }

    private JiraIssueSummary toSummary(JiraApi.Issue issue) {
        var fields = issue.fields();
        LocalDate dueDate = fields.duedate() != null ? LocalDate.parse(fields.duedate()) : null;
        String assignee = fields.assignee() != null ? fields.assignee().displayName() : "미배정";
        String status = fields.status() != null ? fields.status().name() : "";
        return new JiraIssueSummary(issue.key(), fields.summary(), assignee, status, dueDate);
    }
}
