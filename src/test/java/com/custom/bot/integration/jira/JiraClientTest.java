package com.custom.bot.integration.jira;

import com.custom.bot.service.JiraIssueSummary;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JiraClientTest {

    @Mock
    JiraApi jiraApi;

    @InjectMocks
    JiraClient jiraClient;

    @Test
    void findIssuesDueOn_정상_응답_변환() throws IOException {
        var assignee = new JiraApi.Assignee("조재영", "account-123");
        var status = new JiraApi.Status("In Progress");
        var fields = new JiraApi.Fields("테스트 이슈", "2026-05-01", status, assignee);
        var issue = new JiraApi.Issue("PROJ-1", fields);
        var result = new JiraApi.SearchResult(List.of(issue));

        Call<JiraApi.SearchResult> call = mock(Call.class);
        when(call.execute()).thenReturn(Response.success(result));
        when(jiraApi.searchIssues(any(), any())).thenReturn(call);

        List<JiraIssueSummary> summaries = jiraClient.findIssuesDueOn(LocalDate.of(2026, 5, 1));

        assertThat(summaries).hasSize(1);
        assertThat(summaries.get(0).key()).isEqualTo("PROJ-1");
        assertThat(summaries.get(0).assignee()).isEqualTo("조재영");
        assertThat(summaries.get(0).status()).isEqualTo("In Progress");
    }

    @Test
    void findIssuesDueOn_API_실패시_빈_리스트_반환() throws IOException {
        Call<JiraApi.SearchResult> call = mock(Call.class);
        when(call.execute()).thenReturn(Response.error(401,
                ResponseBody.create("Unauthorized", MediaType.get("text/plain"))));
        when(jiraApi.searchIssues(any(), any())).thenReturn(call);

        List<JiraIssueSummary> summaries = jiraClient.findIssuesDueOn(LocalDate.now());

        assertThat(summaries).isEmpty();
    }

    @Test
    void findIssuesDueOn_예외_발생시_빈_리스트_반환() throws IOException {
        Call<JiraApi.SearchResult> call = mock(Call.class);
        when(call.execute()).thenThrow(new IOException("network error"));
        when(jiraApi.searchIssues(any(), any())).thenReturn(call);

        List<JiraIssueSummary> summaries = jiraClient.findIssuesDueOn(LocalDate.now());

        assertThat(summaries).isEmpty();
    }
}
