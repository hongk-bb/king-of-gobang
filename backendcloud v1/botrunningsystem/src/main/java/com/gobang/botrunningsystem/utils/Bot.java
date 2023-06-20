package com.gobang.botrunningsystem.utils;

import java.io.*;
import java.util.*;

/*
    1.解析输入字符串，将当前棋盘状态转化为二维数组表示。
    2.遍历所有空位置，计算每个位置的得分。
    3.选择得分最高的位置，如果有多个位置得分相同，则随机选择一个位置。
    4.返回选择的位置作为下一步落子位置。
    5.在计算每个位置的得分时，对于每个位置，
    算法会按照八个方向（水平、竖直、对角线）分别计算周围的棋子情况，
    然后根据不同的情况给该位置打分。具体的打分规则为：
    (1)如果该位置周围没有己方棋子，得分为0。
    (2)如果该位置周围存在己方棋子，以该位置为中心按照八个方向分别计算周围棋子情况，得分为不同情况下的分数之和：
    (3)如果该方向上存在对手棋子，则不计分。
    (4)如果该方向上存在空位，则以该方向上己方棋子的数量计分，得分规则为：1个棋子得1分，2个棋子得10分，3个棋子得100分，4个棋子得1000分，5个棋子得10000分。
    (5)如果该方向上存在己方棋子，则不计分。
    (6)最后，选择得分最高的位置，如果有多个位置得分相同，则随机选择一个位置，并将选择的位置作为下一步落子位置返回。
*/
public class Bot implements java.util.function.Supplier<List<Integer>> {
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

    private static final int SIZE = 15;

    public List<Integer> nextMove(String input) {
        // Parse the input to get the current board state
        Board board = parseInput(input);

        // Initialize the highest score to be -1
        int highestScore = -1;

        // Initialize the list of positions with the highest score
        List<Integer> bestPositions = new ArrayList<>();

        // Iterate over all empty positions on the board
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board.get(i, j) == 0) {
                    // Calculate the score for this position
                    Score score = calculateScore(board, i, j);

                    // Update the highest score and the list of positions with the highest score
                    if (score.getScore() > highestScore) {
                        highestScore = score.getScore();
                        bestPositions.clear();
                        bestPositions.add(i);
                        bestPositions.add(j);
                    } else if (score.getScore() == highestScore) {
                        bestPositions.add(i);
                        bestPositions.add(j);
                    }
                }
            }
        }

        // Randomly select one of the positions with the highest score
        int index = (int) (Math.random() * bestPositions.size() / 2) * 2;
        List<Integer> res = new ArrayList<>();
        res.add(bestPositions.get(index));
        res.add(bestPositions.get(index + 1));
        return res;
    }

    private Board parseInput(String input) {
        String[] parts = input.split("-");
        int[] myX = new int[0], myY = new int[0], oppX = new int[0], oppY = new int[0];
        if (!parts[0].equals("()")) myX = parseSteps(parts[0]);
        if (!parts[1].equals("()")) myY = parseSteps(parts[1]);
        if (!parts[2].equals("()")) oppX = parseSteps(parts[2]);
        if (!parts[3].equals("()")) oppY = parseSteps(parts[3]);
        Board board = new Board();
        for (int i = 0; i < myX.length; i++) {
            board.set(myX[i], myY[i], 1);
        }
        for (int i = 0; i < oppX.length; i++) {
            board.set(oppX[i], oppY[i], 2);
        }
        return board;
    }

    private int[] parseSteps(String s) {
//        System.out.println("括号处理前: " + s);
        s = s.substring(1, s.length() - 1);
//        System.out.println("括号处理后: " + s);
        String[] parts = s.split("#");
        System.out.println(parts[0]);
//        遍历parts
//        for (int i = 0; i < parts.length; i++) {
//            System.out.println("parts[" + i + "] = " + parts[i]);
//        }
        int[] steps = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            steps[i] = Integer.parseInt(parts[i]);
        }
//        遍历steps
//        for (int i = 0; i < steps.length; i++) {
//            System.out.println("steps[" + i + "] = " + steps[i]);
//        }
        return steps;
    }

    private Score calculateScore(Board board, int x, int y) {
        int[] dx = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] dy = {1, 1, 0, -1, -1, -1, 0, 1};
        int[] scores = {0, 1, 10, 100, 1000, 10000};

        int myScore = 0;
        int oppScore = 0;
        List<List<Integer>> mySteps = new ArrayList<>();
        List<List<Integer>> oppSteps = new ArrayList<>();

        for (int k = 1; k <= 4; k++) {
            for (int i = 0; i < dx.length; i++) {
                int myCount = 0;
                int oppCount = 0;
                List<Integer> myStep = new ArrayList<>();
                List<Integer> oppStep = new ArrayList<>();
                for (int j = -k + 1; j <= k - 1; j++) {
                    int r = x + j * dx[i];
                    int c = y + j * dy[i];
                    if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) {
                        continue;
                    }
                    int value = board.get(r, c);
                    if (value == 0) {
                        myStep.add(r);
                        myStep.add(c);
                        oppStep.add(r);
                        oppStep.add(c);
                    } else if (value == 1) {
                        myCount++;
                        myStep.add(r);
                        myStep.add(c);
                    } else {
                        oppCount++;
                        oppStep.add(r);
                        oppStep.add(c);
                    }
                }
                if (myCount > 0 && oppCount == 0) {
                    myScore += scores[myCount];
                    mySteps.add(myStep);
                } else if (oppCount > 0 && myCount == 0) {
                    oppScore += scores[oppCount];
                    oppSteps.add(oppStep);
                }
            }
        }

        int score = myScore - oppScore;
        List<List<Integer>> steps = mySteps;
        if (score < 0) {
            score = oppScore - myScore;
            steps = oppSteps;
        }

        return new Score(score, steps);
    }

    private class Board {
        private int[][] board;

        public Board() {
            board = new int[SIZE][SIZE];
        }

        public void set(int x, int y, int value) {
            board[x][y] = value;
        }

        public int get(int x, int y) {
            return board[x][y];
        }
    }

    private class Score {
        private int score;
        private List<List<Integer>> steps;

        public Score(int score, List<List<Integer>> steps) {
            this.score = score;
            this.steps = steps;
        }

        public int getScore() {
            return score;
        }

        public List<List<Integer>> getSteps() {
            return steps;
        }
    }
}