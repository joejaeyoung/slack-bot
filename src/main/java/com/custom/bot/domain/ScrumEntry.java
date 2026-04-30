package com.custom.bot.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "scrum_entries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrumEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "slack_ts", unique = true, nullable = false)
    private String slackTs;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "jira_keys")
    private List<String> jiraKeys;

    @Column(name = "posted_date", nullable = false)
    private LocalDate postedDate;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public ScrumEntry(User user, String slackTs, String text, List<String> jiraKeys, LocalDate postedDate) {
        this.user = user;
        this.slackTs = slackTs;
        this.text = text;
        this.jiraKeys = jiraKeys;
        this.postedDate = postedDate;
    }
}
