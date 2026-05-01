package com.custom.bot.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "user_calendars")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "calendar_id", nullable = false)
    private String calendarId;

    @Column(name = "name_filter")
    private String nameFilter;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public UserCalendar(User user, String calendarId, String nameFilter) {
        this.user = user;
        this.calendarId = calendarId;
        this.nameFilter = nameFilter;
    }
}
