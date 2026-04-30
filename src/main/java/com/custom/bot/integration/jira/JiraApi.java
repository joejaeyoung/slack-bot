package com.custom.bot.integration.jira;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

public interface JiraApi {

    @GET("search")
    Call<SearchResult> searchIssues(
            @Query("jql") String jql,
            @Query("fields") String fields
    );

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SearchResult(List<Issue> issues) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Issue(String key, Fields fields) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Fields(
            String summary,
            String duedate,
            Status status,
            Assignee assignee
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Status(String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Assignee(String displayName, String accountId) {}
}
