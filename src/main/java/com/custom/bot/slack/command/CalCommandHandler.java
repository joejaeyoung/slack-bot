package com.custom.bot.slack.command;

import com.slack.api.bolt.App;
import com.custom.bot.domain.User;
import com.custom.bot.repository.UserRepository;
import com.custom.bot.service.Briefing;
import com.custom.bot.service.BriefingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CalCommandHandler {

    private final BriefingService briefingService;
    private final UserRepository userRepository;

    private static final List<String> MEMBERS = List.of("조재영", "권태화", "안수빈");

    public void register(App app) {
        app.command("/cal-team", (req, ctx) -> {
            Briefing briefing = briefingService.getTeamBriefing();
            return ctx.ack(briefing.toBlocks());
        });

        for (String name : MEMBERS) {
            String memberName = name;
            app.command("/cal-" + memberName, (req, ctx) -> {
                User user = userRepository.findByDisplayName(memberName).orElse(null);
                if (user == null) {
                    return ctx.ack(":x: `" + memberName + "` 을(를) 찾을 수 없습니다.");
                }
                Briefing briefing = briefingService.getPersonalBriefing(user);
                return ctx.ack(briefing.toBlocks());
            });
        }
    }
}
