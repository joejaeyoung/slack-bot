package com.custom.bot.service;

import java.time.LocalDate;

public record JiraIssueSummary(
        String key,
        String summary,
        String assignee,
        String status,
        LocalDate dueDate
) {}
