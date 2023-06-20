package com.gobang.botrunningsystem.utils;

import java.util.*;

public class Bot5 implements com.gobang.botrunningsystem.utils.BotInterface {
    public int[][] board; // 棋盘棋子的摆放情况：0无子，1我方，2地方
    public int size = 15;

    @Override
    public List<Integer> nextMove(String input) {
        board = parseBoard(input);
        List<Integer> res = new ArrayList<>();

        int[] move = new int[2];
        int maxDepth = 3;
        long startTime = System.currentTimeMillis();

        // 极大值极小值搜索算法
        while (System.currentTimeMillis() - startTime < 1000) {
            int maxScore = Integer.MIN_VALUE;
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if (board[i][j] == 0) {
                        board[i][j] = 1;
                        int score = minValue(board, i, j, 1, Integer.MIN_VALUE, Integer.MAX_VALUE, maxDepth);
                        board[i][j] = 0;
                        if (score > maxScore) {
                            maxScore = score;
                            move[0] = i;
                            move[1] = j;
                        }
                    }
                }
            }
            maxDepth++;
        }


        res.add(move[0]);
        res.add(move[1]);
        return res;
    }

    // 极小值节点
    private int minValue(int[][] board, int x, int y, int player, int alpha, int beta, int depth) {
        if (depth == 0 || isGameOver(board, x, y, player)) {
            return evaluate(board, player);
        }

        int minScore = Integer.MAX_VALUE;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = 3 - player;
                    int score = maxValue(board, i, j, player, alpha, beta, depth - 1);
                    board[i][j] = 0;
                    minScore = Math.min(minScore, score);
                    beta = Math.min(beta, score);
                    if (beta <= alpha) {
                        return minScore;
                    }
                }
            }
        }
        return minScore;
    }

    // 极大值节点
    private int maxValue(int[][] board, int x, int y, int player, int alpha, int beta, int depth) {
        if (depth == 0 || isGameOver(board, x, y, player)) {
            return evaluate(board, player);
        }

        int maxScore = Integer.MIN_VALUE;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = player;
                    int score = minValue(board, i, j, player, alpha, beta, depth - 1);
                    board[i][j] = 0;
                    maxScore = Math.max(maxScore, score);
                    alpha = Math.max(alpha, score);
                    if (beta <= alpha) {
                        return maxScore;
                    }
                }
            }
        }
        return maxScore;
    }

    // 评估当前棋局得分
    private int evaluate(int[][] board, int player) {
        int score = 0;

        // 计算当前位置周围的得分
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == player) {
                    // 水平方向
                    if (j >= 4 && j <= 10) {
                        int count = 1;
                        for (int k = 1; k <= 4; k++) {
                            if (board[i][j - k] == player) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        for (int k = 1; k <= 4; k++) {
                            if (board[i][j + k] == player) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        score += count * count * count;
                    }
                    // 垂直方向
                    if (i >= 4 && i <= 10) {
                        int count = 1;
                        for (int k = 1; k <= 4; k++) {
                            if (board[i - k][j] == player) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        for (int k = 1; k <= 4; k++) {
                            if (board[i + k][j] == player) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        score += count * count * count;
                    }
                    // 左上到右下方向
                    if (i >= 4 && i <= 10 && j >= 4 && j <= 10) {
                        int count = 1;
                        for (int k = 1; k <= 4; k++) {
                            if (board[i - k][j - k] == player) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        for (int k = 1; k <= 4; k++) {
                            if (board[i + k][j + k] == player) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        score += count * count * count;
                    }
                    // 右上到左下方向
                    if (i >= 4 && i <= 10 && j >= 4 && j <= 10) {
                        int count = 1;
                        for (int k = 1; k <= 4; k++) {
                            if (board[i - k][j + k] == player) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        for (int k = 1; k <= 4; k++) {
                            if (board[i + k][j - k] == player) {
                                count++;
                            } else {
                                break;
                            }
                        }
                        score += count * count * count;
                    }
                }
            }
        }

        return score;
    }

    // 判断游戏是否结束
    private boolean isGameOver(int[][] board, int x, int y, int player) {
        int count = 1;

        // 水平方向
        for (int i = y - 1; i >= 0; i--) {
            if (board[x][i] == player) {
                count++;
            } else {
                break;
            }
        }
        for (int i = y + 1; i < board[x].length; i++) {
            if (board[x][i] == player) {
                count++;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }

        // 垂直方向
        count = 1;
        for (int i = x - 1; i >= 0; i--) {
            if (board[i][y] == player) {
                count++;
            } else {
                break;
            }
        }
        for (int i = x + 1; i < board.length; i++) {
            if (board[i][y] == player) {
                count++;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }

        // 左上到右下方向
        count = 1;
        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j] == player) {
                count++;
            } else {
                break;
            }
        }
        for (int i = x + 1, j = y + 1; i < board.length && j < board[i].length; i++, j++) {
            if (board[i][j] == player) {
                count++;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }

        // 右上到左下方向
        count = 1;
        for (int i = x - 1, j = y + 1; i >= 0 && j < board[i].length; i--, j++) {
            if (board[i][j] == player) {
                count++;
            } else {
                break;
            }
        }
        for (int i = x + 1, j = y - 1; i < board.length && j >= 0; i++, j--) {
            if (board[i][j] == player) {
                count++;
            } else {
                break;
            }
        }
        if (count >= 5) {
            return true;
        }

        // 判断是否平局
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] parseBoard(String input) {
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

    public int[] parseSteps(String s) {
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