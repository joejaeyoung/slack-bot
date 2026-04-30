package com.custom.bot.service;

import com.custom.bot.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public record Briefing(
        LocalDate date,
        Map<User, List<CalendarEvent>> personalSchedules,
        List<JiraIssueSummary> jiraDueIssues
) {
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.KOREAN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public String toBlocksJson() {
        ArrayNode blocks = MAPPER.createArrayNode();

        // 날짜 헤더
        blocks.add(section(":calendar: *" + date.format(DATE_FMT) + " 팀 브리핑*"));
        blocks.add(divider());

        // 팀원별 일정
        for (var entry : personalSchedules.entrySet()) {
            User user = entry.getKey();
            List<CalendarEvent> events = entry.getValue();

            blocks.add(section(":bust_in_silhouette: *" + user.getDisplayName() + "*"));

            if (events.isEmpty()) {
                blocks.add(section("> 오늘 일정 없음"));
            } else {
                for (CalendarEvent e : events) {
                    String time = e.start() != null
                            ? e.start().format(TIME_FMT) + " ~ " + e.end().format(TIME_FMT)
                            : "종일";
                    String loc = (e.location() != null && !e.location().isBlank())
                            ? " | " + e.location() : "";
                    blocks.add(section("> `" + time + "` " + e.title() + loc));
                }
            }
            blocks.add(divider());
        }

        // Jira 마감 이슈
        blocks.add(section(":jira: *오늘 Jira 마감*"));
        if (jiraDueIssues.isEmpty()) {
            blocks.add(section("> 오늘 마감 이슈 없음"));
        } else {
            for (JiraIssueSummary issue : jiraDueIssues) {
                blocks.add(section("> `" + issue.key() + "` " + issue.summary() + " — " + issue.assignee()));
            }
        }

        try {
            return MAPPER.writeValueAsString(blocks);
        } catch (Exception e) {
            return "[]";
        }
    }

    private ObjectNode section(String text) {
        ObjectNode block = MAPPER.createObjectNode();
        block.put("type", "section");
        ObjectNode textNode = MAPPER.createObjectNode();
        textNode.put("type", "mrkdwn");
        textNode.put("text", text);
        block.set("text", textNode);
        return block;
    }

    private ObjectNode divider() {
        ObjectNode block = MAPPER.createObjectNode();
        block.put("type", "divider");
        return block;
    }
}
