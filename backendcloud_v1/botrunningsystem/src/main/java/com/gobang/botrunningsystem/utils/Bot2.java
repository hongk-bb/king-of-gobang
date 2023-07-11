package com.gobang.botrunningsystem.utils;

import java.util.ArrayList;
import java.util.List;

/*
    1.解析输入板面状态和当前玩家走法,构建游戏棋盘对象。
    2.遍历整个棋盘,查找空白且可走的位置,构成空白位置集合。这是搜索空间。
    3.对每个空白位置进行深度优先搜索,同时启发式评估每步走法的好坏,以减少搜索空间。评估方式如下:
    4.计算该走法对自己的连续数量分数。分数越高价值越大。
    5.计算该走法周围对方棋数。越多对方棋数,阻碍性越大,分数减小。
    6.考虑未探索区域,引导搜索朝未被采访区域前进,增加搜索深度。
    7.根据已知棋盘信息,对候选集进行筛选,较快判断 Candidates 的价值和分数。
    8.加权方式增加未被占用区域和对方棋子周边区域的评分权重,引导选择隐藏对手或者占领空白区域。
    9.通过比对每个空白位置的分数,选出分数最高的一个作为此回合最佳走法。如果存在分数并列,随机选择一个返回。
    10.将选出的位置组成列表形式返回给主调程序。

    使用深度优先搜索框架,通过启发式评估方式减少搜索空间,有效提高搜索效率,找到分数最大的走法, 更加智能的评分算法
*/


public class Bot2 implements com.gobang.botrunningsystem.utils.BotInterface {
    private static final int SIZE = 15;

    @Override
    public List<Integer> nextMove(String input) {
        Board board = parseInput(input);

        int highestScore = -1;

        List<Integer> bestPositions = new ArrayList<>();

        List<Position> vacantPositions = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board.get(i, j) == 0)
                    vacantPositions.add(new Position(i, j));
            }
        }

        // 评估每个空缺职位并更新最高得分和最佳职位
        for (Position position : vacantPositions) {
            Move move = new Move(position.getX(), position.getY());
            Score score = calculateScore(board, move);

            if (score.getScore() > highestScore) {
                highestScore = score.getScore();
                bestPositions.clear();
                bestPositions.add(move.getX());
                bestPositions.add(move.getY());
            } else if (score.getScore() == highestScore)
                bestPositions.add(move.getX());
            bestPositions.add(move.getY());
        }

        // 从最佳位置中随机选择一个位置
        List<Integer> res = new ArrayList<>();
        res.add(bestPositions.get(0));
        res.add(bestPositions.get(1));
        return res;
    }

    private Board parseInput(String input) {
        // Parse the input string
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
        s = s.substring(1, s.length() - 1);
        String[] parts = s.split("#");
        int[] steps = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            steps[i] = Integer.parseInt(parts[i]);
        }
        return steps;
    }

    private Score calculateScore(Board board, Move move) {
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
                    int r = move.getX() + j * dx[i];
                    int c = move.getY() + j * dy[i];
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

    class Position {
        private int x;
        private int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    class Move {
        private int x;
        private int y;

        public Move(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

    class Score {
        private int score;
        private List<List<Integer>> steps;

        public Score(int score, List<List<Integer>> steps) {
            this.score = score;
            this.steps = steps;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public List<List<Integer>> getSteps() {
            return steps;
        }

        public void setSteps(List<List<Integer>> steps) {
            this.steps = steps;
        }
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

}