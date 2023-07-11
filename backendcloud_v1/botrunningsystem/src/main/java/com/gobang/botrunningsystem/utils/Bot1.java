package com.gobang.botrunningsystem.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
    傻瓜式bot
    基于"(自己横坐标操作)-(自己的纵坐标操作)-(对手的横坐标操作)-(对手的纵坐标操作)"字符串处理
    例如：(1#2#3#4)-(5#6#7#8)-(9#10#11#12)-(13#14#15#16)
*/
public class Bot1 implements com.gobang.botrunningsystem.utils.BotInterface {
    @Override
    public List<Integer> nextMove(String input) {
        List<Integer> res = new ArrayList<>();
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(15);
            y = random.nextInt(15);
        } while (isOccupied(x, y, input));
        res.add(x);
        res.add(y);
        return res;
    }

    private boolean isOccupied(int x, int y, String input) {
        int[][] board = parseInput(input);
        return board[x][y] != 0;
    }

    private int[][] parseInput(String input) {
        String[] parts = input.split("-");
        int[][] board = new int[15][15];
        int[] myX = new int[0], myY = new int[0], oppX = new int[0], oppY = new int[0];
        if (!parts[0].equals("()")) myX = parseSteps(parts[0]);
        if (!parts[1].equals("()")) myY = parseSteps(parts[1]);
        if (!parts[2].equals("()")) oppX = parseSteps(parts[2]);
        if (!parts[3].equals("()")) oppY = parseSteps(parts[3]);
        for (int i = 0; i < myX.length; i++) {
            board[myX[i]][myY[i]] = 1;
        }
        for (int i = 0; i < oppX.length; i++) {
            board[oppX[i]][oppY[i]] = 2;
        }
        return board;
    }

    private int[] parseSteps(String s) {
        s = s.substring(1, s.length() - 1);
        String[] parts = s.split("#");
        System.out.println(parts[0]);

        int[] steps = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            steps[i] = Integer.parseInt(parts[i]);
        }
        return steps;
    }
}
