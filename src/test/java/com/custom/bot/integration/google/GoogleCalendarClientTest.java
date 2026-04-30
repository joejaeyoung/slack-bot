package com.custom.bot.integration.google;

import com.custom.bot.config.GoogleCalendarConfig;
import com.custom.bot.service.CalendarEvent;
import com.google.auth.oauth2.GoogleCredentials;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleCalendarClientTest {

    @Mock
    GoogleCalendarConfig calendarConfig;

    @InjectMocks
    GoogleCalendarClient googleCalendarClient;

    @Test
    void fetchEvents_API_실패시_빈_리스트_반환() {
        when(calendarConfig.buildCredentials(anyString()))
                .thenThrow(new RuntimeException("credential error"));

        List<CalendarEvent> result = googleCalendarClient.fetchEvents(
                "test@gmail.com", "invalid-token", LocalDate.now());

        assertThat(result).isEmpty();
    }
}
