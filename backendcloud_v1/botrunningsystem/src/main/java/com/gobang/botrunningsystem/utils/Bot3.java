package com.gobang.botrunningsystem.utils;

import java.util.ArrayList;
import java.util.List;

/*
    该段代码使用的算法是一种启发式搜索算法具体来说，是一种基于评估函数的最优化算法。
    该算法将棋盘上的每个空位视为一个状态节点，通过评估函数来评估每个状态的价值，从而找到最优解。
    评估函数的设计是这种算法的关键，该算法中使用的评估函数是基于五子棋的规则和策略，能够比较准确地评估每个状态的价值。
    在计算每个状态的价值时，算法考虑了多个方面，如进攻、防守、连子数、空位数等，能够比较好地平衡棋局，使得 AI 的表现更加出色。
 */

public class Bot3 implements com.gobang.botrunningsystem.utils.BotInterface {
    private static final int SIZE = 15;
    private static final int[][] DIRECTIONS = {{0, 1}, {1, 0}, {1, 1}, {1, -1}}; // 只需考虑四个方向，其余方向可以通过转换得到

    @Override
    public List<Integer> nextMove(String input) {
        Board board = parseInput(input);

        int highestScore = -1;
        List<Integer> bestPositions = new ArrayList<>();

        List<Position> vacantPositions = board.getVacantPositions();

        for (Position position : vacantPositions) {
            int score = calculateScore(board, position);

            if (score > highestScore) {
                highestScore = score;
                bestPositions.clear();
                bestPositions.add(position.getX());
                bestPositions.add(position.getY());
            } else if (score == highestScore) {
                bestPositions.add(position.getX());
                bestPositions.add(position.getY());
            }
        }

        int randomIndex = (int) (Math.random() * bestPositions.size() / 2);
        List<Integer> res = new ArrayList<>();
        res.add(bestPositions.get(randomIndex * 2));
        res.add(bestPositions.get(randomIndex * 2 + 1));
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
        s = s.substring(1, s.length() - 1);
        String[] parts = s.split("#");
        int[] steps = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            steps[i] = Integer.parseInt(parts[i]);
        }
        return steps;
    }

    private int calculateScore(Board board, Position pos) {
        int score = 0;
        for (int[] direction : DIRECTIONS) {
            score += calculateScoreInDirection(board, pos, direction);
        }
        return score;
    }

    private int calculateScoreInDirection(Board board, Position pos, int[] direction) {
        int aiCount = 0;
        int oppCount = 0;
        int spaceCount = 0;
        boolean aiBlocked = false;
        boolean oppBlocked = false;

        for (int i = -4; i <= 4; i++) {
            int r = pos.getX() + i * direction[0];
            int c = pos.getY() + i * direction[1];

            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) {
                continue;
            }

            int value = board.get(r, c);

            if (value == 0) {
                if (aiBlocked || oppBlocked) {
                    break;
                }
                spaceCount++;
                if (spaceCount == 1) {
                    aiCount = countStonesInLine(board, pos, direction, 1);
                    oppCount = countStonesInLine(board, pos, direction, 2);
                }
            } else if (value == 1) {
                if (oppBlocked) {
                    break;
                }
                aiCount++;
                if (aiCount == 5) {
                    return 100000;
                }
                if (oppCount > 0) {
                    aiBlocked = true;
                    spaceCount = 1;
                }
            } else {
                if (aiBlocked) {
                    break;
                }
                oppCount++;
                if (oppCount == 5) {
                    return 50000;
                }
                if (aiCount > 0) {
                    oppBlocked = true;
                    spaceCount = 1;
                }
            }
        }
        return calculateScoreFromCounts(aiCount, oppCount, spaceCount);
    }

    private int countStonesInLine(Board board, Position pos, int[] direction, int stone) {
        int count = 0;
        for (int i = 1; i <= 4; i++) {
            int r = pos.getX() + i * direction[0];
            int c = pos.getY() + i * direction[1];
            if (r < 0 || r >= SIZE || c < 0 || c >= SIZE) {
                break;
            }
            if (board.get(r, c) == stone) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    private int calculateScoreFromCounts(int aiCount, int oppCount, int spaceCount) {
        if (aiCount == 4) {
            return 10000;
        } else if (oppCount == 4) {
            return 1000;
        } else if (aiCount == 3 && spaceCount == 1) {
            return 1000;
        } else if (oppCount == 3 && spaceCount == 1) {
            return 100;
        } else if (aiCount == 2 && spaceCount == 2) {
            return 100;
        } else if (oppCount == 2 && spaceCount == 2) {
            return 10;
        } else {
            return 1;
        }
    }

    private static class Board {
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

        public List<Position> getVacantPositions() {
            List<Position> positions = new ArrayList<>();
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == 0) {
                        positions.add(new Position(i, j));
                    }
                }
            }
            return positions;
        }
    }

    private static class Position {
        private int x;
        private int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}