package com.gobang.backend.service.impl.pk;

import com.gobang.backend.consumer.WebSocketServer;
import com.gobang.backend.consumer.utils.Game;
import com.gobang.backend.service.pk.PkService;
import org.springframework.stereotype.Service;


@Service
public class PkServiceImpl implements PkService {
    @Override
    public String startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId) {
        System.out.println("start game: " + aId + " " + aBotId + " " + bId + " " + bBotId);
        WebSocketServer.startGame(aId, aBotId, bId, bBotId);
        return "start game success";
    }

    @Override
    public String receiveBotMove(int userId, int nextStepX, int nextStepY) {
        System.out.println("receive bot move: " + userId + " " + nextStepX + " " + nextStepY);
        if(WebSocketServer.users.get(userId) != null) {
            Game game = WebSocketServer.users.get(userId).game;
            if(game != null) {
                if (game.getPlayerA().getStep() <= game.getPlayerB().getStep()) {  // 当前是A的回合
                    if (game.getPlayerA().getId().equals(userId)) {  // 如果当前的操作是A的, 并且是亲自出马
                        System.out.println("A");
                        game.setNextStepX(nextStepX);
                        game.setNextStepY(nextStepY);
                    }
                } else { // 当前是B的回合
                    if (game.getPlayerB().getId().equals(userId)) {
                        System.out.println("B");
                        game.setNextStepX(nextStepX);
                        game.setNextStepY(nextStepY);
                    }
                }
            }

        }
        return null;
    }
}
