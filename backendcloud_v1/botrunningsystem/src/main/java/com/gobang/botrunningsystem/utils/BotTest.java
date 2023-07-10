package com.gobang.botrunningsystem.utils;

import java.util.List;

public class BotTest {
    public static void main(String[] args) {
        Bot6 bot = new Bot6();
        String input = "()-()-()-()";
//        测试多次
//        for (int i = 0; i < 10; i++) {
//            List<Integer> res = bot.nextMove(input);
//            System.out.println("Next move: (" + res.get(0) + ", " + res.get(1) + ")");
//        }

        List<Integer> res = bot.nextMove(input);
        System.out.println("Next move: (" + res.get(0) + ", " + res.get(1) + ")");
    }
}