package com.custom.bot.slack.command;

import com.slack.api.bolt.App;
import com.custom.bot.domain.User;
import com.custom.bot.repository.UserRepository;
import com.custom.bot.service.Briefing;
import com.custom.bot.service.BriefingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalCommandHandler {

    private final BriefingService briefingService;
    private final UserRepository userRepository;
    @Qualifier("apiExecutor")
    private final Executor apiExecutor;

    private static final List<String> MEMBERS = List.of("조재영", "권태화", "안수빈");

    public void register(App app) {
        app.command("/cal-team", (req, ctx) -> {
            apiExecutor.execute(() -> {
                try {
                    Briefing briefing = briefingService.getTeamBriefing();
                    ctx.respond(briefing.toBlocks());
                } catch (Exception e) {
                    log.error("/cal-team 응답 실패", e);
                }
            });
            return ctx.ack();
        });

        for (String name : MEMBERS) {
            String memberName = name;
            app.command("/cal-" + memberName, (req, ctx) -> {
                User user = userRepository.findByDisplayName(memberName).orElse(null);
                if (user == null) {
                    return ctx.ack(":x: `" + memberName + "` 을(를) 찾을 수 없습니다.");
                }
                apiExecutor.execute(() -> {
                    try {
                        Briefing briefing = briefingService.getPersonalBriefing(user);
                        ctx.respond(briefing.toBlocks());
                    } catch (Exception e) {
                        log.error("/cal-{} 응답 실패", memberName, e);
                    }
                });
                return ctx.ack();
            });
        }
    }
}
