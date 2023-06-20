package com.gobang.botrunningsystem.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
    基于极大极小值搜索和Alpha-Beta剪枝的五子棋AI
 */
public class Bot4 implements BotInterface {
    private static final int EMPTY = 0;
    private static final int MAX_PLAYER = 1;
    private static final int MIN_PLAYER = 2;
    private static final int INFINITY = 100000000;
    @Override
    public List<Integer> nextMove(String input) {
        int[][] board = parseBoard(input);
        List<Integer> bestMove = new ArrayList<>();
        int bestScore = -INFINITY;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board[i][j] == EMPTY) {
                    int row = 7;
                    int col = 7;
                    if (board[row][col] == EMPTY) {
                        bestMove.add(row);
                        bestMove.add(col);
                        return bestMove;
                    }
                    if (board[row - 1][col] == EMPTY) {
                        bestMove.add(row);
                        bestMove.add(col);
                        return bestMove;
                    }
                    if (board[row][col - 1] == EMPTY) {
                        bestMove.add(row);
                        bestMove.add(col);
                        return bestMove;
                    }
                    if (board[row + 1][col] == EMPTY) {
                        bestMove.add(row);
                        bestMove.add(col);
                        return bestMove;
                    }
                    if (board[row][col + 1] == EMPTY) {
                        bestMove.add(row);
                        bestMove.add(col);
                        return bestMove;
                    }
                } else {
                    int score = alphaBetaPruning(board, i, j, 5, -INFINITY, INFINITY, MAX_PLAYER);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove.clear();
                        bestMove.add(i);
                        bestMove.add(j);
                    }
                }
            }
        }
        return bestMove;
    }

    private int alphaBetaPruning(int[][] board, int row, int col, int depth, int alpha, int beta, int player) {
        board[row][col] = player;
        int score = evaluate(board, player);
        if (depth == 0 || score == INFINITY || score == -INFINITY) {
            board[row][col] = EMPTY;
            return score;
        }
        if (player == MAX_PLAYER) {
            int bestScore = -INFINITY;
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    if (board[i][j] == EMPTY) {
                        int value = alphaBetaPruning(board, i, j, depth - 1, alpha, beta, MIN_PLAYER);
                        bestScore = Math.max(bestScore, value);
                        alpha = Math.max(alpha, bestScore);
                        if (alpha >= beta) {
                            board[row][col] = EMPTY;
                            return bestScore;
                        }
                    }
                }
            }
            board[row][col] = EMPTY;
            return bestScore;
        } else {
            int bestScore = INFINITY;
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 15; j++) {
                    if (board[i][j] == EMPTY) {
                        int value = alphaBetaPruning(board, i, j, depth - 1, alpha, beta, MAX_PLAYER);
                        bestScore = Math.min(bestScore, value);
                        beta = Math.min(beta, bestScore);
                        if (alpha >= beta) {
                            board[row][col] = EMPTY;
                            return bestScore;
                        }
                    }
                }
            }
            board[row][col] = EMPTY;
            return bestScore;
        }
    }

    private int evaluate(int[][] board, int player) {
        int opponent = (player == MAX_PLAYER) ? MIN_PLAYER : MAX_PLAYER;
        if (findFiveInARow(board, player)) {
            return INFINITY;
        } else if (findFiveInARow(board, opponent)) {
            return -INFINITY;
        } else {
            int score = calculateScore(board, player) - calculateScore(board, opponent);
            return score;
        }
    }

    private int calculateScore(int[][] board, int player) {
        int score = 0;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board[i][j] == player) {
                    score += calculateScoreForPosition(board, i, j);
                }
            }
        }
        return score;
    }

    private int calculateScoreForPosition(int[][] board, int row, int col) {
        int score = 0;
        score += calculateScoreForDirection(board, row, col, -1, -1, 1, 1);   // 左上到右下
        score += calculateScoreForDirection(board, row, col, -1, 0, 1, 0);    // 左到右
        score += calculateScoreForDirection(board, row, col, -1, 1, 1, -1);   // 右上到左下
        score += calculateScoreForDirection(board, row, col, 0, -1, 0, 1);    // 上到下
        return score;
    }

    private int calculateScoreForDirection(int[][] board, int row, int col, int rowDir, int colDir, int rowDir2, int colDir2) {
        int player = board[row][col];
        int opponent = (player == MAX_PLAYER) ? MIN_PLAYER : MAX_PLAYER;
        int score = 0;
        int count = 0;
        int empty = 0;
        int r = row + rowDir;
        int c = col + colDir;
        while (r >= 0 && r < 15 && c >= 0 && c < 15 && board[r][c] != opponent && count < 4) {
            if (board[r][c] == EMPTY) {
                empty++;
            } else {
                count++;
            }
            r += rowDir;
            c += colDir;
        }
        if (count == 4) {
            score += INFINITY;
        } else if (count == 3 && empty == 1) {
            score += 10000;
        } else if (count == 2 && empty == 2) {
            score += 100;
        }
        count = 0;
        empty = 0;
        r = row + rowDir2;
        c = col + colDir2;
        while (r >= 0 && r < 15 && c >= 0 && c < 15 && board[r][c] != opponent && count < 4) {
            if (board[r][c] == EMPTY) {
                empty++;
            } else {
                count++;
            }
            r += rowDir2;
            c += colDir2;
        }
        if (count == 4) {
            score += INFINITY;
        } else if (count == 3 && empty == 1) {
            score += 10000;
        } else if (count == 2 && empty == 2) {
            score += 100;
        }
        return score;
    }

    private boolean findFiveInARow(int[][] board, int player) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board[i][j] == player) {
                    if (findFiveInARowFromPosition(board, i, j)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean findFiveInARowFromPosition(int[][] board, int row, int col) {
        int player = board[row][col];
        int count = 1;
        int r = row - 1;
        int c = col;
        while (r >= 0 && board[r][c] == player) {
            count++;
            r--;
        }
        r = row + 1;
        while (r < 15 && board[r][c] == player) {
            count++;
            r++;
        }
        if (count >= 5) {
            return true;
        }
        count = 1;
        r = row;
        c = col - 1;
        while (c >= 0 && board[r][c] == player) {
            count++;
            c--;
        }
        c = col + 1;
        while (c < 15 && board[r][c] == player) {
            count++;
            c++;
        }
        if (count >= 5) {
            return true;
        }
        count = 1;
        r = row - 1;
        c = col - 1;
        while (r >= 0 && c >= 0 && board[r][c] == player) {
            count++;
            r--;
            c--;
        }
        r = row + 1;
        c = col + 1;
        while (r < 15 && c < 15 && board[r][c] == player) {
            count++;
            r++;
            c++;
        }
        if (count >= 5) {
            return true;
        }
        count = 1;
        r = row - 1;
        c = col + 1;
        while (r >= 0 && c < 15 && board[r][c] == player) {
            count++;
            r--;
            c++;
        }
        r = row + 1;
        c = col - 1;
        while (r < 15 && c >= 0 && board[r][c] == player) {
            count++;
            r++;
            c--;
        }
        if (count >= 5) {
            return true;
        }
        return false;
    }

    private int[][] parseBoard(String input) {
        int[][] board = new int[15][15];
        String[] parts = input.split("-");
        int[] myX = parseSteps(parts[0]);
        int[] myY = parseSteps(parts[1]);
        int[] oppX = parseSteps(parts[2]);
        int[] oppY = parseSteps(parts[3]);

        for (int i = 0; i < myX.length; i++) {
            board[myX[i]][myY[i]] = MAX_PLAYER;
        }
        for (int i = 0; i < oppX.length; i++) {
            board[oppX[i]][oppY[i]] = MIN_PLAYER;
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
