package com.custom.bot.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "slack_user_id", nullable = false, unique = true)
    private String slackUserId;

    @Column(name = "jira_account_id", nullable = false, unique = true)
    private String jiraAccountId;

    @Column(name = "google_calendar_id", nullable = false)
    private String googleCalendarId;

    @Column(name = "google_refresh_token", nullable = false, columnDefinition = "TEXT")
    private String googleRefreshToken;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public User(String displayName, String slackUserId, String jiraAccountId,
                String googleCalendarId, String googleRefreshToken) {
        this.displayName = displayName;
        this.slackUserId = slackUserId;
        this.jiraAccountId = jiraAccountId;
        this.googleCalendarId = googleCalendarId;
        this.googleRefreshToken = googleRefreshToken;
    }
}
