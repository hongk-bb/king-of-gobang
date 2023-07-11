package com.gobang.botrunningsystem.utils;

import java.util.*;

/*
五子棋的博弈程序，主要使用了 α-β剪枝算法进行搜索。
在每一层搜索中，计算每个空位置的得分（使用各种评估函数），并按照得分从高到低排序。
为了加速搜索，程序还引入了一些启发式方法和特殊情况处理。
Tricks来判断黑白棋形态, 当对手即将获胜时，程序会选择阻止对手获胜的位置进行落子。
实验功能: VCF 的绝杀策略，即在没有危险或者对方将形成33/34/44/4时尝试VCF绝杀。
 */

public class Bot6 implements com.gobang.botrunningsystem.utils.BotInterface {
    private int size = 15;
    private int[][] board; // 棋盘棋子的摆放情况：0无子，1我方，2地方
    private Computer computer = new Computer();
    private int fromX, fromY, toX, toY;

    @Override
    public List<Integer> nextMove(String input) {
        board = parseBoard(input);
        List<Integer> res = new ArrayList<>();

        fromX = fromY = size - 1;
        toX = toY = 0;

//        尝试在中心落子
        if (ifemptyDo(7, 7)) {
            res.add(7);
            res.add(7);
            return res;
        }

        int x0 = 5, x1 = 9, y0 = 5, y1 = 9;
        // 初始化可行落子区域
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board[i][j] != 0) {
                    x0 = Math.max(x0, j - 2);
                    x1 = Math.min(x1, j + 2);
                    y0 = Math.max(y0, i - 2);
                    y1 = Math.min(y1, i + 2);
                }
            }
        }
        computer.set(x0, x1, y0, y1, board);

        computer.alphabeta(1, -1000000000, 1000000000, 0);

        int bestX = computer.cx;
        int bestY = computer.cy;

        res.add(bestY);
        res.add(bestX);
        return res;
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

    boolean ifemptyDo(int xth, int yth) {
        if (board[yth][xth] != 0) return false;
        board[yth][xth] = 1;
        //System.out.println(board[7][7]);
        fromX = Math.min(fromX, xth);
        fromY = Math.min(fromY, yth);
        toX = Math.max(toX, xth);
        toY = Math.max(toY, yth);
        computer.set(fromX, toX, fromY, toY, board);
        return true;
    }

    static class Computer {
        int x0, x1, y0, y1;
        int c[][];
        public int cx, cy;
        static final int VCF_depth = 10;
        static final int chess_line_number = 15;
        static final int d[][] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

        public void set(int _x0, int _x1, int _y0, int _y1, int[][] chess) {
            x0 = Math.max(Math.min(x0, _x0 - 2), 0);
            x1 = Math.min(Math.max(x1, _x1 + 2), chess_line_number - 1);
            y0 = Math.max(Math.min(y0, _y0 - 2), 0);
            y1 = Math.min(Math.max(y1, _y1 + 2), chess_line_number - 1);
            c = chess;
            cx = cy = 0;
        }

        boolean inTwoStep(int x, int y, int[][] c) {
            for (int i = 0; i < 8; i++) {
                int x1 = x + 1 * d[i][0], y1 = y + 1 * d[i][1];
                int x2 = x + 2 * d[i][0], y2 = y + 2 * d[i][1];
                if (Tricks.valid(x1, y1) && c[y1][x1] != 0) return true;
                if (Tricks.valid(x2, y2) && c[y2][x2] != 0) return true;
            }
            return false;
        }

        int getProbability(int[][] c, int x, int y, int player, int depth) {
            c[y][x] = player;
            if (Tricks.fiveConsecutive(c, x, y))
                return Valuation.C5probability;
            c[y][x] = 3 - player;
            if (Tricks.fiveConsecutive(c, x, y))
                return Valuation._C5probability;

            c[y][x] = player;
            if (Tricks.AliveFour(c, x, y)) return Valuation.A4probability;
            c[y][x] = 3 - player;
            if (Tricks.AliveFour(c, x, y)) return Valuation._A4probability;

            c[y][x] = player;
            int a3 = Tricks.AliveThree(c, x, y);
            int d4 = Tricks.deathsFour(c, x, y);
            if (d4 >= 2 || (d4 > 1 && a3 > 1)) return Valuation.DD4probability;
            c[y][x] = 3 - player;
            int _a3 = Tricks.AliveThree(c, x, y);
            int _d4 = Tricks.deathsFour(c, x, y);
            if (_d4 >= 2 || (_d4 > 1 && _a3 > 1)) return Valuation._DD4probability;

            if (a3 >= 2) return Valuation.DA3probability;
            else if (_a3 >= 2) return Valuation._DA3probability;

            c[y][x] = player;
            int a2 = Tricks.AliveTwo(c, x, y);
            c[y][x] = 3 - player;
            int _a2 = Tricks.AliveTwo(c, x, y);
            c[y][x] = 0;
            int cur4 = d4 * Valuation.D4probability;
            int cur3 = a3 * Valuation.A3probability;
            int cur2 = a2 * Valuation.A2probability;
            int _cur4 = _d4 * Valuation.D4probability;
            int _cur3 = _a3 * Valuation.A3probability;
            int _cur2 = _a2 * Valuation.A2probability;
            return cur4 + cur3 + cur2 + _cur4 + _cur3 + _cur2;
        }

        public int alphabeta(int player, int alpha, int beta, int depth) {
            ArrayList<Score> s = new ArrayList<Score>();
            for (int x = 0; x < chess_line_number; x++) {
                for (int y = 0; y < chess_line_number; y++) {
                    if (c[y][x] == 0) {
                        if (!inTwoStep(x, y, c)) continue;
                        //if(inTwoStep(x,y,c))System.out.println();
                        int cur = getProbability(c, x, y, player, depth);
                        c[y][x] = 0;
                        s.add(new Score(x, y, cur));
                    }
                }
            }
            Collections.sort(s);
            for (int i = 0; i < Math.min(15, s.size()); i++) {
//	    	if(player == 1 && s.get(0).score <= Valuation._A4probability && depth == 0)
//	        {
//        		int cur = VCF(0,depth);
//        		if(cur != 0)System.out.println("VCF!!!!!");
//    			if(cur == 1)return Valuation.INF[player];
//    			else if(cur == 2)return Valuation.INF[player];
//	        }////在没有危险或者对方将形成33/34/44/4时尝试VCF绝杀
                if (depth == 0) {
                    System.out.println("----------------------------------------");
                    System.out.println("x: " + s.get(i).x + " y: " + s.get(i).y);
                    System.out.println("----------------------------------------");
                }
                if (s.get(0).score == Valuation.DA3probability ||
                        s.get(0).score == Valuation.DD4probability ||
                        s.get(0).score == Valuation.C5probability) {
                    set(s.get(i).x, s.get(i).y, depth);
                    return Valuation.INF[player];
                }
                /////我方能够比对方先制胜！，返回。
                if (s.get(i).score < Valuation._DA3probability &&
                        s.get(0).score >= Valuation._DA3probability) continue;
                if (s.get(i).score < Valuation._A4probability &&
                        s.get(0).score >= Valuation._A4probability) continue;
                if (s.get(i).score < Valuation._C5probability &&
                        s.get(0).score >= Valuation._C5probability) continue;
                ////必须选择走在对方快赢的步位上
                int v;
                if (depth < 5) {
                    c[s.get(i).y][s.get(i).x] = player;
                    v = alphabeta(3 - player, alpha, beta, depth + 1);
                    c[s.get(i).y][s.get(i).x] = 0;
                } else {
                    c[s.get(i).y][s.get(i).x] = player;
                    v = Valuation.valBlack(x0, x1, y0, y1, c, player);
                    c[s.get(i).y][s.get(i).x] = 0;
                }
                if (player == 1) {
                    if (v > alpha) {
                        alpha = v;
                        set(s.get(i).x, s.get(i).y, depth);
                    }
                } else {
                    if (v < beta) {
                        beta = v;
                        set(s.get(i).x, s.get(i).y, depth);
                    }
                }
                if (beta <= alpha) break;
            }
            return (player == 1) ? alpha : beta;
        }

        void set(int x, int y, int depth) {
            if (depth == 0) {
                cx = x;
                cy = y;
            }
        }

        //	int VCF(int depth,int alphabetaD)
//	{
//		if(depth > VCF_depth)return 0;
//		for(int x = x0;x <= x1;x++)
//		{
//			for(int y = y0;y <= y1;y++)
//			{
//				if(c[y][x] == 0)
//				{
//					c[y][x] = 1;
//					int cur = 0;
//					if(Tricks.fiveConsecutive(c, x, y))
//						cur = 2;
//					else if(Tricks.AliveFour(c, x, y))
//						cur = 1;
//					else if(Tricks.deathsFour(c, x, y) > 1)
//						cur = 1;
//					else if(Tricks.deathsFour(c, x, y) > 0 && Tricks.AliveThree(c, x, y) > 0)
//						cur = 1;
//					if(Tricks.deathsFour(c, x, y) > 0)
//					{
//						Tricks.blockDeathsFour(c, x, y);
//						cur = VCF(depth + 1,alphabetaD);
//						c[Tricks.cury][Tricks.curx] = 0;
//					}
//					c[y][x] = 0;
//					if(cur > 0)
//					{
//						if(depth == 0 && alphabetaD == 0)set(x,y,alphabetaD);
//						return cur;
//					}
//				}
//			}
//		}
//		return 0;
//	}
        public class Score implements Comparable<Score> {
            public int x, y, score;

            public Score(int _x, int _y, int _score) {
                x = _x;
                y = _y;
                score = _score;
            }

            public int compareTo(Score s) {
                if (score < s.score) return 1;
                else if (score == s.score) return 0;
                else return -1;
            }
        }
    }

    static class Valuation {
        static final int d[][] = {{1, 0}, {0, 1}, {1, 1}, {1, -1}};
        static final int c_line_number = 15;
        static final int deathsThreeScore[] = {0, 10, -10};
        static final int AliveTwoScore[] = {0, 10, -10};
        static final int AliveThreeScore[] = {0, 200, -200};
        static final int deathsFourScore[] = {0, 200, -200};//1e2,1e2
        static final int doubleAliveThreeScore[] = {0, 10000, -10000};//1e4,1e4
        static final int doubleDeathsFourScore[] = {0, 1000000, -1000000};//1e6,1e6
        static final int AliveThreeAndDeathsFourScore[] = {0, 1000000, -1000000};//1e6,1e6
        static final int AliveFourScore[] = {0, 1000000, -1000000}; //1e6,1e6
        static final int fiveConsecutiveScore[] = {0, 100000000, -100000000}; //1e8
        static final int INF[] = {0, 100000001, -100000001}; //1e8
        static final int valuable = 1000000;

        /////last score
/////////////////////////////////////////////////////////////////////////////////////
/////probability to search this node
        static final int C5probability = 300000000;
        static final int _C5probability = 100000000;//1e8

        static final int A3D4probability = 30000000;//3 * 1e7
        static final int A4probability = 30000000;
        static final int DD4probability = 30000000;
        static final int _A3D4probability = 10000000;//1e7
        static final int _A4probability = 10000000;
        static final int _DD4probability = 10000000;

        static final int DA3probability = 3000000;//3 * 1e6
        static final int _DA3probability = 1000000;//1e6

        static final int A3probability = 200;
        static final int D4probability = 200;
        static final int D3probability = 10;
        static final int A2probability = 10;

        static boolean valid(int xth, int yth) {
            return xth >= 0 && yth >= 0 && yth < c_line_number && xth < c_line_number;
        }

        static boolean equal(int xth, int yth, int x, int y, int[][] c) {
            return (valid(x, y) && c[yth][xth] == c[y][x]);
        }

        static boolean equalZero(int x, int y, int[][] c) {
            return (valid(x, y) && c[y][x] == 0);
        }

        static boolean nEqual(int xth, int yth, int x, int y, int[][] c) {
            return !equal(xth, yth, x, y, c);
        }

        static boolean equal(int x, int y, int[][] c, int a) {
            return valid(x, y) && c[y][x] == a;
        }

        static boolean block(int x, int y, int[][] c, int a) {
            return !valid(x, y) || c[y][x] == a;
        }

        static boolean BlockN1(int xth, int yth, int x, int y, int[][] c, int a) {
            if (equal(xth, yth, c, 0) && equal(x, y, c, 3 - a)) return true;
            if (equal(xth, yth, c, 3 - a) && equal(x, y, c, 0)) return true;
            if (equalZero(xth, yth, c) && !valid(x, y)) return true;
            if (equalZero(x, y, c) && !valid(xth, yth)) return true;
            return false;
        }

        static int valBlack(int x0, int x1, int y0, int y1, int[][] c, int player) {
            int ans = 0;
            //int ans1 = 0,ans2 = 0,ans3 = 0,ans4 = 0,ans5 = 0;
            boolean level1[] = {false, false, false};//34,44,4
            boolean level2[] = {false, false, false};//33
            boolean level3[] = {false, false, false};//d4;
            boolean level4[] = {false, false, false};//3;
            for (int x = x0; x <= x1; x++) {
                for (int y = y0; y <= y1; y++) {
                    if (c[y][x] != 0) {
                        ans += AliveThree(x, y, c) * AliveThreeScore[player];
                        ans += deathsFour(x, y, c) * deathsFourScore[player];
                        ans += deathsThreeOrAliveTwo(x, y, c) * AliveTwoScore[player];
                        if (c[y][x] == 1) {
                            if (Tricks.AliveFour(c, x, y))
                                level1[1] = true;
                            int a3 = Tricks.AliveThree(c, x, y);
                            int d4 = Tricks.deathsFour(c, x, y);
                            if (a3 > 0) level4[1] = true;
                            if (d4 > 0) level3[1] = true;
                            if (d4 >= 2 || (d4 > 1 && a3 > 1))
                                level1[1] = true;
                            else if (a3 >= 2)
                                level2[1] = true;
                        } else {
                            if (Tricks.AliveFour(c, x, y))
                                level1[2] = true;
                            int _a3 = Tricks.AliveThree(c, x, y);
                            int _d4 = Tricks.deathsFour(c, x, y);
                            if (_a3 > 0) level4[2] = true;
                            if (_d4 > 0) level3[2] = true;
                            if (_d4 >= 2 || (_d4 > 1 && _a3 > 1))
                                level1[2] = true;
                            else if (_a3 >= 2)
                                level2[2] = true;
                        }
                    }
                }
            }
            //1//34,44,4
            //2//33
            //3//d4;
            //4//3;
            if (level1[3 - player]) return fiveConsecutiveScore[3 - player];
            if (level2[3 - player]) return doubleAliveThreeScore[3 - player];
            if (level3[3 - player]) return fiveConsecutiveScore[3 - player];
            if (level1[player]) return AliveFourScore[player];
            if (level2[player]) return doubleAliveThreeScore[player];
            return ans;
//		if(level4[3 - player] && !level3[player])
//			return doubleAliveThreeScore[3 - player];
//		if(level1[3 - player] || level2[3 - player] || level3[3 - player])
//			return INF[3 - player];
//		else if(level4[3 - player] && !level3[player])
//			return INF[3 - player];
//		else if(level1[player] || level2[player])
//			return INF[player];
//		System.out.println("deathsThreeOrAliveTwo: " + ans1);
//		System.out.println("AliveThree: " + ans2);
//		System.out.println("deathsFour: " + ans3);
//		System.out.println("AliveFour: " + ans4);
//		System.out.println("fiveConsecutive: " + ans5);
        }

        static int deathsThreeOrAliveTwo(int xth, int yth, int[][] c) {
            int ans = 0;
            for (int i = 0; i < 4; i++) {
                int _x1 = xth - d[i][0], _y1 = yth - d[i][1];
                int _x2 = xth - 2 * d[i][0], _y2 = yth - 2 * d[i][1];
                int x1 = xth + d[i][0], y1 = yth + d[i][1];
                int x2 = xth + 2 * d[i][0], y2 = yth + 2 * d[i][1];
                int x3 = xth + 3 * d[i][0], y3 = yth + 3 * d[i][1];
                int x4 = xth + 4 * d[i][0], y4 = yth + 4 * d[i][1];
                int a = c[yth][xth];
                ////////
                //deathsThree
                ///////
                if (equal(xth, yth, x1, y1, c) && equal(xth, yth, x2, y2, c)) {
                    ////211100
                    if ((equal(x3, y3, c, 0) && equal(x4, y4, c, 0) && block(_x1, _y1, c, 3 - a)) ||
                            (equal(_x2, _y2, c, 0) && equal(_x1, _y1, c, 0) && block(x3, y3, c, 3 - a)))
                        ans++;
                        ////2011102
                    else if (equal(x3, y3, c, 0) && equal(_x1, _y1, c, 0) &&
                            block(x4, y4, c, 3 - a) && block(_x2, _y2, c, 3 - a))
                        ans++;
                    continue;
                }
                ///////210110
                else if (equal(x1, y1, c, 0) && equal(xth, yth, x2, y2, c) && equal(xth, yth, x3, y3, c)) {
                    if (BlockN1(_x1, _y1, x4, y4, c, a))
                        ans++;
                    continue;
                }
                //////211010
                else if (equal(xth, yth, x1, y1, c) && equal(x2, y2, c, 0) && equal(xth, yth, x3, y3, c)) {
                    if (BlockN1(_x1, _y1, x4, y4, c, a))
                        ans++;
                    continue;
                }
                //////10101
                else if (equal(xth, yth, x4, y4, c)) {
                    if (c[y1][x1] == a && c[y2][x2] == 0 && c[y3][x3] == 0)
                        ans++;
                    if (c[y1][x1] == 0 && c[y2][x2] == a && c[y3][x3] == 0)
                        ans++;
                    if (c[y1][x1] == 0 && c[y2][x2] == 0 && c[y3][x3] == a)
                        ans++;
                    continue;
                }
                ////////
                //AliveTwo
                ////////
                ////////001100
                if (equal(_x2, _y2, c, 0) && equal(_x1, _y1, c, 0) && equal(xth, yth, x1, y1, c) &&
                        equal(x2, y2, c, 0) && equal(x3, y3, c, 0))
                    ans++;
                    ////////0010100
                else if (equal(_x2, _y2, c, 0) && equal(_x1, _y1, c, 0) && equal(x1, y1, c, 0) &&
                        equal(xth, yth, x2, y2, c) && equal(x3, y3, c, 0) && equal(x4, y4, c, 0))
                    ans++;
                    ////////010010
                else if (equal(_x1, _y1, c, 0) && equal(x1, y1, c, 0) && equal(x2, y2, c, 0) &&
                        equal(xth, yth, x3, y3, c) && equal(x4, y4, c, 0))
                    ans++;
            }
            return ans;
        }

        static int AliveThree(int xth, int yth, int[][] c) {
            int ans = 0;
            for (int i = 0; i < 4; i++) {
                int _x1 = xth - d[i][0], _y1 = yth - d[i][1];
                int _x2 = xth - 2 * d[i][0], _y2 = yth - 2 * d[i][1];
                int x1 = xth + d[i][0], y1 = yth + d[i][1];
                int x2 = xth + 2 * d[i][0], y2 = yth + 2 * d[i][1];
                int x3 = xth + 3 * d[i][0], y3 = yth + 3 * d[i][1];
                int x4 = xth + 4 * d[i][0], y4 = yth + 4 * d[i][1];
                int a = c[yth][xth];
                ///////0011100
                if (equal(_x1, _y1, c, 0) && equal(x1, y1, c, a) && equal(x2, y2, c, a) && equal(x3, y3, c, 0)) {
                    if (equalZero(_x2, _y2, c) && equalZero(x4, y4, c))
                        ans++;
                }
                //////011010
                else if (equal(_x1, _y1, c, 0) && equal(x1, y1, c, a) &&
                        equal(x2, y2, c, 0) && equal(x3, y3, c, a) && equal(x4, y4, c, 0))
                    ans++;
                    //////010110
                else if (equal(_x1, _y1, c, 0) && equal(x1, y1, c, 0) &&
                        equal(x2, y2, c, a) && equal(x3, y3, c, a) && equal(x4, y4, c, 0))
                    ans++;
            }
            return ans;
        }

        static int deathsFour(int xth, int yth, int[][] c) {
            int ans = 0;
            for (int i = 0; i < 4; i++) {
                int _x1 = xth - d[i][0], _y1 = yth - d[i][1];
                int x1 = xth + d[i][0], y1 = yth + d[i][1];
                int x2 = xth + 2 * d[i][0], y2 = yth + 2 * d[i][1];
                int x3 = xth + 3 * d[i][0], y3 = yth + 3 * d[i][1];
                int x4 = xth + 4 * d[i][0], y4 = yth + 4 * d[i][1];
                int a = c[yth][xth];
                ///////011112
                ///////211110
                if (equal(x1, y1, c, a) && equal(x2, y2, c, a) && equal(x3, y3, c, a)) {
                    if ((equal(_x1, _y1, c, 0) && block(x4, y4, c, 3 - a)) ||
                            (equal(x4, y4, c, 0) && block(_x1, _y1, c, 3 - a)))
                        ans++;
                }
                //////11101
                //////11011
                //////10111
                else if (equal(xth, yth, x4, y4, c)) {
                    if (c[y1][x1] == a && c[y2][x2] == a && c[y3][x3] == 0)
                        ans++;
                    if (c[y1][x1] == 0 && c[y2][x2] == a && c[y3][x3] == a)
                        ans++;
                    if (c[y1][x1] == a && c[y2][x2] == 0 && c[y3][x3] == a)
                        ans++;
                }
            }
            return ans;
        }

        static int AliveFour(int xth, int yth, int[][] c) {
            int ans = 0;
            //////011110
            for (int i = 0; i < 4; i++)
                if (equalZero(xth - d[i][0], yth - d[i][1], c)) {
                    int x = xth + d[i][0], y = yth + d[i][1];
                    int cnt = 1;
                    for (; equal(xth, yth, x, y, c); x += d[i][0], y += d[i][1])
                        cnt++;
                    if (cnt == 4) ans++;
                }
            return ans;
        }

        static int fiveConsecutive(int xth, int yth, int[][] c) {
            /////11111
            for (int i = 0; i < 4; i++) {
                int x = xth + d[i][0], y = yth + d[i][1];
                int cnt = 1;
                for (; equal(xth, yth, x, y, c); x += d[i][0], y += d[i][1])
                    cnt++;
                if (cnt >= 5) return 1;
            }
            return 0;
        }
    }

    static class Tricks {
        static final int d[][] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};
        static final int[][] position = {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0},
                {0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 1, 0},
                {0, 1, 2, 3, 4, 4, 4, 4, 4, 4, 4, 3, 2, 1, 0},
                {0, 1, 2, 3, 4, 5, 5, 5, 5, 5, 4, 3, 2, 1, 0},
                {0, 1, 2, 3, 4, 5, 6, 6, 6, 5, 4, 3, 2, 1, 0},
                {0, 1, 2, 3, 4, 5, 6, 7, 6, 5, 4, 3, 2, 1, 0},
                {0, 1, 2, 3, 4, 5, 6, 6, 6, 5, 4, 3, 2, 1, 0},
                {0, 1, 2, 3, 4, 5, 5, 5, 5, 5, 4, 3, 2, 1, 0},
                {0, 1, 2, 3, 4, 4, 4, 4, 4, 4, 4, 3, 2, 1, 0},
                {0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 2, 1, 0},
                {0, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 0},
                {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
        static final int c_line_number = 15;
        static int curx, cury;

        ///which just have be blocked
        static boolean valid(int xth, int yth) {
            return xth >= 0 && yth >= 0 && yth < c_line_number && xth < c_line_number;
        }

        static boolean equ(int xth, int yth, int x, int y, int[][] c) {
            return (valid(x, y) && c[yth][xth] == c[y][x]);
        }

        static boolean equZero(int x, int y, int[][] c) {
            return (valid(x, y) && c[y][x] == 0);
        }

        static boolean equ(int x, int y, int[][] c, int n) {
            return valid(x, y) && c[y][x] == n;
        }

        static boolean increase(int xth, int yth, int ix, int iy, int[] ans, int[][] c) {
            int x = xth + ix, y = yth + iy;
            for (; equ(xth, yth, x, y, c); x += ix, y += iy)
                ans[0]++;
            return valid(x, y) && c[y][x] == 0;
        }

        static boolean fiveConsecutive(int[][] c, int xth, int yth) {
            int ans[] = new int[1];
            for (int i = 0; i < 4; i++) {
                ans[0] = 1;
                increase(xth, yth, d[2 * i][0], d[2 * i][1], ans, c);
                increase(xth, yth, d[2 * i + 1][0], d[2 * i + 1][1], ans, c);
                if (ans[0] >= 5) return true;
            }
            return false;
        }

        static boolean AliveFour(int[][] c, int xth, int yth) {
            int ans[] = new int[1];
            boolean have;
            for (int i = 0; i < 4; i++) {
                ans[0] = 1;
                have = increase(xth, yth, d[2 * i][0], d[2 * i][1], ans, c);
                have = (have && increase(xth, yth, d[2 * i + 1][0], d[2 * i + 1][1], ans, c));
                if (ans[0] == 4 && have) return true;
            }
            return false;
        }

        static int deathsFour(int[][] c, int xth, int yth) {
            if (AliveFour(c, xth, yth)) return 0;
            int cnt = 0;
            for (int i = 0; i < 8; i++) {
                int x = xth + d[i][0], y = yth + d[i][1];
                for (; equ(xth, yth, x, y, c); x += d[i][0], y += d[i][1]) ;
                //until to first empty board
                if (equZero(x, y, c)) {
                    c[y][x] = c[yth][xth];
                    if (fiveConsecutive(c, x, y))
                        cnt++;
                    c[y][x] = 0;
                }
            }
            return cnt;
        }

        static int AliveThree(int[][] c, int xth, int yth) {
            int cnt1 = 0, cnt2 = 0;
            for (int i = 0; i < 8; i++) {
                int x = xth, y = yth;
                for (; equ(xth, yth, x - d[i][0], y - d[i][1], c);
                     x -= d[i][0], y -= d[i][1])
                    ;
                int a = c[yth][xth];
                int _x2 = x - 2 * d[i][0], _y2 = y - 2 * d[i][1];
                int _x1 = x - 1 * d[i][0], _y1 = y - 1 * d[i][1];
                int x1 = x + 1 * d[i][0], y1 = y + 1 * d[i][1];
                int x2 = x + 2 * d[i][0], y2 = y + 2 * d[i][1];
                int x3 = x + 3 * d[i][0], y3 = y + 3 * d[i][1];
                int x4 = x + 4 * d[i][0], y4 = y + 4 * d[i][1];
                //because,above code is sub,so this code is add
                ///////0011100
                if (equ(_x1, _y1, c, 0) && equ(x1, y1, c, a) && equ(x2, y2, c, a) && equ(x3, y3, c, 0)) {
                    if (equZero(_x2, _y2, c) && equZero(x4, y4, c))
                        cnt1++;
                }
                //////011010
                else if (equ(_x1, _y1, c, 0) && equ(x1, y1, c, a) &&
                        equ(x2, y2, c, 0) && equ(x3, y3, c, a) && equ(x4, y4, c, 0))
                    cnt2++;
                    //////010110
                else if (equ(_x1, _y1, c, 0) && equ(x1, y1, c, 0) &&
                        equ(x2, y2, c, a) && equ(x3, y3, c, a) && equ(x4, y4, c, 0))
                    cnt2++;
            }
            return cnt1 / 2 + cnt2;
        }

        static int AliveTwo(int[][] c, int xth, int yth) {
            int ans = 0;
            for (int i = 0; i < 8; i++) {
                int _x1 = xth - d[i][0], _y1 = yth - d[i][1];
                int _x2 = xth - 2 * d[i][0], _y2 = yth - 2 * d[i][1];
                int x1 = xth + d[i][0], y1 = yth + d[i][1];
                int x2 = xth + 2 * d[i][0], y2 = yth + 2 * d[i][1];
                int x3 = xth + 3 * d[i][0], y3 = yth + 3 * d[i][1];
                int x4 = xth + 4 * d[i][0], y4 = yth + 4 * d[i][1];
                ////////
                //AliveTwo
                ////////
                ////////001100
                if (equ(_x2, _y2, c, 0) && equ(_x1, _y1, c, 0) && equ(xth, yth, x1, y1, c) &&
                        equ(x2, y2, c, 0) && equ(x3, y3, c, 0))
                    ans++;
                    ////////0010100
                else if (equ(_x2, _y2, c, 0) && equ(_x1, _y1, c, 0) && equ(x1, y1, c, 0) &&
                        equ(xth, yth, x2, y2, c) && equ(x3, y3, c, 0) && equ(x4, y4, c, 0))
                    ans++;
                    ////////010010
                else if (equ(_x1, _y1, c, 0) && equ(x1, y1, c, 0) && equ(x2, y2, c, 0) &&
                        equ(xth, yth, x3, y3, c) && equ(x4, y4, c, 0))
                    ans++;
            }
            return ans;
        }

        static void blockDeathsFour(int[][] c, int xth, int yth) {
            for (int i = 0; i < 8; i++) {
                int x = xth + d[i][0], y = yth + d[i][1];
                for (; equ(xth, yth, x, y, c); x += d[i][0], y += d[i][1]) ;
                //until to first empty board
                if (equZero(x, y, c)) {
                    c[y][x] = c[yth][xth];
                    if (fiveConsecutive(c, x, y)) {
                        curx = x;
                        cury = y;
                        c[y][x] = 0;
                        return;
                    }
                    c[y][x] = 0;
                }
            }
        }
    }
}