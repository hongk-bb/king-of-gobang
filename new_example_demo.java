package com.gobang.botrunningsystem.utils;
import java.util.*;
import java.io.*;

/*
    类名必须为Bot
    package和implements后面一行必须完全一样
    基于"(自己横坐标操作)-(自己的纵坐标操作)-(对手的横坐标操作)-(对手的纵坐标操作)"字符串处理
    例如：(1#2#3#4)-(5#6#7#8)-(9#10#6#7)-(13#14#4#3)
 */

public class Bot implements java.util.function.Supplier<List<Integer>> {
    private int[][] board; // 棋盘棋子的摆放情况：0无子，1我方，2地方
    
    @Override
    public List<Integer> get() {
        File file = new File("input.txt");
        try {
            Scanner sc = new Scanner(file);
            return nextMove(sc.next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<Integer> nextMove(String input) {
        board = parseBoard(input);
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
        return board[x][y] != 0;
    }

    private int[][] parseBoard(String input) {
        int[][] board = new int[15][15];
        String[] parts = input.split("-");
        int[] myX = parseSteps(parts[0]);
        int[] myY = parseSteps(parts[1]);
        int[] oppX = parseSteps(parts[2]);
        int[] oppY = parseSteps(parts[3]);

        for (int i = 0; i < myX.length; i++) {
            board[myX[i]][myY[i]] = 1;
        }
        for (int i = 0; i < oppX.length; i++) {
            board[oppX[i]][oppY[i]] = 2;
        }

        return board;
    }
    private int[] parseSteps(String s) {
        if (s.equals("()")) {
            return new int[0];
        }
        s = s.substring(1, s.length() - 1);
        String[] parts = s.split("#");
        int[] steps = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            steps[i] = Integer.parseInt(parts[i]);
        }
        return steps;
    }
}
