package com.gobang.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer id;  // 玩家的id

    private Integer botId;
    private String botCode;
    private Integer step = 0;
    private List<Integer> stepsX;  // 玩家所走的步骤的x坐标
    private List<Integer> stepsY;  // 玩家所走的步骤的y坐标

    /**
     * 获取玩家的所有棋子
     * @return
     */
    public List<Piece> getPieces() {
        List<Piece> res = new ArrayList<>();
        for (int i = 0; i < stepsX.size(); i++) {
            int x = stepsX.get(i);
            int y = stepsY.get(i);
            res.add(new Piece(x, y));
        }
        return res;
    }
}
