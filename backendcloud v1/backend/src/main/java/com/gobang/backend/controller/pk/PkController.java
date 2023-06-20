package com.gobang.backend.controller.pk;

import com.gobang.backend.service.pk.PkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;


@RestController
@RequestMapping("/pk")
public class PkController {
    @Autowired
    private PkService pkService;

    @PostMapping("/game/start")
    public String startGame(@RequestParam MultiValueMap<String, String> data) {
        int aId = Integer.parseInt(Objects.requireNonNull(data.getFirst("a_id")));
        int aBotId = Integer.parseInt(Objects.requireNonNull(data.getFirst("a_bot_id")));
        int bId = Integer.parseInt(Objects.requireNonNull(data.getFirst("b_id")));
        int bBotId = Integer.parseInt(Objects.requireNonNull(data.getFirst("b_bot_id")));
        return pkService.startGame(aId, aBotId, bId, bBotId);
    }

    @PostMapping("/receive/bot/move")
    public String receiveBotMove(@RequestParam MultiValueMap<String, String> data) {
        int userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        int nextStepX = Integer.parseInt(Objects.requireNonNull(data.getFirst("nextStepX")));
        int nextStepY = Integer.parseInt(Objects.requireNonNull(data.getFirst("nextStepY")));
        return pkService.receiveBotMove(userId, nextStepX, nextStepY);
    }
}
