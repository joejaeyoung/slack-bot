package com.custom.bot.service;

import com.custom.bot.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.slack.api.model.block.DividerBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.SectionBlock;
import com.slack.api.model.block.composition.MarkdownTextObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    public List<LayoutBlock> toBlocks() {
        List<LayoutBlock> blocks = new ArrayList<>();
        blocks.add(sectionBlock(":calendar: *" + date.format(DATE_FMT) + " 팀 브리핑*"));
        blocks.add(DividerBlock.builder().build());

        for (var entry : personalSchedules.entrySet()) {
            User user = entry.getKey();
            List<CalendarEvent> events = entry.getValue();
            blocks.add(sectionBlock(":bust_in_silhouette: *" + user.getDisplayName() + "*"));
            if (events.isEmpty()) {
                blocks.add(sectionBlock("> 오늘 일정 없음"));
            } else {
                for (CalendarEvent e : events) {
                    String time = e.start() != null
                            ? e.start().format(TIME_FMT) + " ~ " + e.end().format(TIME_FMT)
                            : "종일";
                    String loc = (e.location() != null && !e.location().isBlank())
                            ? " | " + e.location() : "";
                    blocks.add(sectionBlock("> `" + time + "` " + e.title() + loc));
                }
            }
            blocks.add(DividerBlock.builder().build());
        }

        blocks.add(sectionBlock(":jira: *오늘 Jira 마감*"));
        if (jiraDueIssues.isEmpty()) {
            blocks.add(sectionBlock("> 오늘 마감 이슈 없음"));
        } else {
            for (JiraIssueSummary issue : jiraDueIssues) {
                blocks.add(sectionBlock("> `" + issue.key() + "` " + issue.summary() + " — " + issue.assignee()));
            }
        }
        return blocks;
    }

    private SectionBlock sectionBlock(String text) {
        return SectionBlock.builder()
                .text(MarkdownTextObject.builder().text(text).build())
                .build();
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
