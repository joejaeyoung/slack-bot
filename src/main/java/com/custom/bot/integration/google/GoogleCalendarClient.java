package com.custom.bot.integration.google;

import com.custom.bot.config.GoogleCalendarConfig;
import com.custom.bot.service.CalendarEvent;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.auth.http.HttpCredentialsAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleCalendarClient {

    private final GoogleCalendarConfig calendarConfig;

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public List<CalendarEvent> fetchEvents(String calendarId, String refreshToken, LocalDate date) {
        try {
            var credentials = calendarConfig.buildCredentials(refreshToken);
            var calendar = new Calendar.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            ).setApplicationName("scheduler-bot").build();

            var dayStart = date.atStartOfDay(KST);
            var dayEnd = date.plusDays(1).atStartOfDay(KST);

            var events = calendar.events().list(calendarId)
                    .setTimeMin(new DateTime(dayStart.toInstant().toEpochMilli()))
                    .setTimeMax(new DateTime(dayEnd.toInstant().toEpochMilli()))
                    .setSingleEvents(true)
                    .setOrderBy("startTime")
                    .execute();

            return events.getItems().stream()
                    .map(this::toCalendarEvent)
                    .toList();

        } catch (Exception e) {
            log.error("Google Calendar fetch 실패 - calendarId: {}", calendarId, e);
            return List.of();
        }
    }

    private CalendarEvent toCalendarEvent(Event event) {
        OffsetDateTime start = parseDateTime(event.getStart().getDateTime(), event.getStart().getDate());
        OffsetDateTime end = parseDateTime(event.getEnd().getDateTime(), event.getEnd().getDate());
        return new CalendarEvent(event.getSummary(), start, end, event.getLocation());
    }

    private OffsetDateTime parseDateTime(DateTime dateTime, DateTime date) {
        if (dateTime != null) {
            return OffsetDateTime.ofInstant(
                    java.time.Instant.ofEpochMilli(dateTime.getValue()), KST);
        }
        // 종일 이벤트
        return OffsetDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(date.getValue()), KST);
    }
}
