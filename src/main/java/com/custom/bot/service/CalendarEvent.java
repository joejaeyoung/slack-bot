package com.custom.bot.service;

import java.time.OffsetDateTime;

public record CalendarEvent(
        String title,
        OffsetDateTime start,
        OffsetDateTime end,
        String location
) {}
