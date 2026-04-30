package com.custom.bot.slack.controller;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.request.RequestHeaders;
import com.slack.api.bolt.response.Response;
import com.slack.api.bolt.util.SlackRequestParser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/slack")
@RequiredArgsConstructor
public class SlackEventController {

    private final App slackApp;
    private final AppConfig appConfig;

    @PostMapping("/events")
    public ResponseEntity<String> handle(
            @RequestBody String body,
            HttpServletRequest httpReq
    ) throws Exception {
        Map<String, java.util.List<String>> headers = new HashMap<>();
        java.util.Enumeration<String> names = httpReq.getHeaderNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            headers.put(name, Collections.list(httpReq.getHeaders(name)));
        }

        SlackRequestParser parser = new SlackRequestParser(appConfig);
        SlackRequestParser.HttpRequest request = SlackRequestParser.HttpRequest.builder()
                .requestUri(httpReq.getRequestURI())
                .requestBody(body)
                .headers(new RequestHeaders(headers))
                .queryString(Collections.emptyMap())
                .build();

        Response response = slackApp.run(parser.parse(request));
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
