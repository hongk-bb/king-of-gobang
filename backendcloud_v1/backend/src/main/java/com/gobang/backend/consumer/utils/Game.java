package com.gobang.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.gobang.backend.consumer.WebSocketServer;
import com.gobang.backend.pojo.Bot;
import com.gobang.backend.pojo.Record;
import com.gobang.backend.pojo.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生成地图
 */
public class Game extends Thread {
    private final Integer rows;
    private final Integer cols;
    //    private static int[][] g;  // 保存一份地图, 方便判断是否赢, 0 没有棋子，1黑子，-1白子
    private final Player playerA, playerB;
    private Integer nextStepX = null;  // 两个玩家共用一个
    private Integer nextStepY = null;

    private String status = "playing"; // 游戏状态， playing正在游戏， finished游戏结束
    private String winer = ""; // all 平局, A:A赢，B赢
    //    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";
    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";

    private ReentrantLock lock = new ReentrantLock();

    public Game(Integer rows, Integer cols, Integer idA, Bot botA, Integer idB, Bot botB) {
        this.rows = rows;
        this.cols = cols;

        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";  // 因为如果前面传过来的botId是-1，则BotA是null，所以需要加判断。
        if (botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if (botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }

        playerA = new Player(idA, botIdA, botCodeA, 0, new ArrayList<>(), new ArrayList<>());
        playerB = new Player(idB, botIdB, botCodeB, 0, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * 设置下一步的X坐标
     *
     * @param nextStepX
     */
    public void setNextStepX(Integer nextStepX) {
        lock.lock();
        try {
            this.nextStepX = nextStepX;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 设置下一步的Y坐标
     *
     * @param nextStepY
     */
    public void setNextStepY(Integer nextStepY) {
        lock.lock();
        try {
            this.nextStepY = nextStepY;
        } finally {
            lock.unlock();
        }
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    /**
     * 新线程的入口函数
     */
    @Override
    public void run() {
        // 这里先让线程睡2s，因为匹配成功后前端要等2s才能跳转到对战页面，如果这里不睡2s，直接开始获取下一步操作，则两个AI在2s内可以做出多步操作，不能保证同步了。
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 执行每回合的操作
        for (int i = 0; i < 1000; i++) {
            if (nextStep()) {  // 判断是否获取到下一步操作 并且下一步操作是否合法
                judge();
                if ("playing".equals(status)) {
                    sendMove();
                } else {
                    sendMove();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    sendResult();
                    break;
                }
            } else {  // 没有获取到当前用户的操作
                status = "finished";
                lock.lock();
                try {
                    if (playerA.getStep() <= playerB.getStep()) { // 当前是playerA的回合
                        winer = "B";
                    } else {
                        winer = "A";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }

    /**
     * 判断下一步操作是否合法
     *
     * @return
     */
    private boolean nextIsLegal() {
        lock.lock();
        try {
            int x = nextStepX;
            int y = nextStepY;
            List<Piece> piecesA = playerA.getPieces();
            List<Piece> piecesB = playerB.getPieces();
            for (Piece piece : piecesA) {   // 是否和A重合
                if (piece.getX() == x && piece.getY() == y)
                    return false;
            }
            for (Piece piece : piecesB) {  // 是否和B重合
                if (piece.getX() == x && piece.getY() == y)
                    return false;
            }
        } finally {
            lock.unlock();
        }
        return true;
    }

    /**
     * 游戏结束，向前端发送结果信息
     */
    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("winer", winer);
        saveToDatabase();
        sendAllMessage(resp.toJSONString());
    }

    /**
     * 向两个玩家发送比赛结果信息
     *
     * @param message
     */
    private void sendAllMessage(String message) {
        if (WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if (WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    /**
     * 向前端发送移动信息
     */
    private void sendMove() {
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("nextStepX", nextStepX);
            resp.put("nextStepY", nextStepY);
            if (playerA.getStep() <= playerB.getStep()) {
                resp.put("player", "A");
                playerA.setStep(playerA.getStep() + 1);
            } else {
                resp.put("player", "B");
                playerB.setStep(playerB.getStep() + 1);
            }
            sendAllMessage(resp.toJSONString());  // 将移动信息返回给前端
            nextStepX = nextStepY = null;  // 将两个玩家的下一步清空
        } finally {
            lock.unlock();
        }
    }

    /**
     * 判断下了当前位置后，能否连成5个
     */
    private boolean judgeSuccess(List<Piece> pieces) {
        if (pieces.size() < 5) return false;  // 不够五个棋子
        int[][] g = new int[this.rows][this.cols];
        for (Piece piece : pieces) {
            int x = piece.getX();
            int y = piece.getY();
            g[x][y] = 1;
        }
        Piece currentPiece = pieces.get(pieces.size() - 1);
        int x = currentPiece.getX();
        int y = currentPiece.getY();
        int[] dx = {-1, -1, -1, 0}, dy = {1, 0, -1, -1};
        for (int i = 0; i < 4; i++) { // 8个方向都需要判断
            int newX = x - 4 * dx[i], newY = y - 4 * dy[i];
            int num = 0;
            for (int j = 0; j < 9; j++, newX += dx[i], newY += dy[i]) {
                if (newX < 0 || newX >= 15 || newY < 0 || newY >= 15) continue;
                if (g[newX][newY] == 0) num = 0;
                else if (++num == 5) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否能够赢，结束游戏，有没有5个棋子连在一起。
     */
    private void judge() {
        List<Piece> piecesA = playerA.getPieces();
        List<Piece> piecesB = playerB.getPieces();

        if (playerA.getStep() <= playerB.getStep()) {
            if (judgeSuccess(piecesA)) {  // A已经连成5个
                status = "finished";
                winer = "A";
                return;
            }
        } else {
            if (judgeSuccess(piecesB)) {
                status = "finished";
                winer = "B";
                return;
            }
        }

//        如果是平局
        if (piecesA.size() + piecesB.size() == 225) {
            status = "finished";
            winer = "平局";
            return;
        }
    }

    /**
     * 是否获取到下一步操作
     *
     * @return
     */
    private boolean nextStep() {
        if (playerA.getStep() <= playerB.getStep()) {
            sendBotCode(playerA);
        } else {
            sendBotCode(playerB);
        }
        for (int i = 0; i < 300; i++) {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepX != null && nextStepY != null && nextIsLegal()) {  // 已经获取到下一步操作了 并且当前步骤合法
                        if (playerA.getStep() <= playerB.getStep()) {  // 当前步骤合法
                            List<Integer> stepsX = playerA.getStepsX();
                            stepsX.add(nextStepX);
                            playerA.setStepsX(stepsX);
                            List<Integer> stepsY = playerA.getStepsY();
                            stepsY.add(nextStepY);
                            playerA.setStepsY(stepsY);
                        } else {
                            List<Integer> stepsX = playerB.getStepsX();
                            stepsX.add(nextStepX);
                            playerB.setStepsX(stepsX);
                            List<Integer> stepsY = playerB.getStepsY();
                            stepsY.add(nextStepY);
                            playerB.setStepsY(stepsY);
                        }
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 判断用户是否亲自上阵，如果不是，则需要向botrunningsystem微服务发送请求。
     *
     * @param player 判断的玩家
     */
    private void sendBotCode(Player player) {
        if (player.getBotId().equals(-1)) return; // 人亲自出马，不需要执行代码
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    /**
     * 将steps转为String
     */
    public String getStepsString(List<Integer> steps) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < steps.size(); i++) {
            res.append(steps.get(i));
            if (i < steps.size() - 1) res.append("#");
        }  // "1#2#3#15#16#"
        return res.toString();
    }

    /**
     * 获取当前的局面，将当前的局面信息编码成字符串：
     * "(自己横坐标操作)-(自己的纵坐标操作)-(对手的横坐标操作)-(对手的纵坐标操作)"
     *
     * @param player
     * @return
     */
    private String getInput(Player player) {
        Player me, you;
        if (playerA.getId().equals(player.getId())) {
            me = playerA;
            you = playerB;
        } else {
            me = playerB;
            you = playerA;
        }
        return "(" +
                getStepsString(me.getStepsX()) +
                ")-(" +
                getStepsString(me.getStepsY()) +
                ")-(" +
                getStepsString(you.getStepsX()) +
                ")-(" +
                getStepsString(you.getStepsY()) +
                ")";
    }

    /**
     * 将对战记录保存到数据库中。在对战结束后先更新两个玩家的对战积分。
     */
    private void saveToDatabase() {
        Integer ratingA = WebSocketServer.userMapper.selectById(playerA.getId()).getRating();
        Integer ratingB = WebSocketServer.userMapper.selectById(playerB.getId()).getRating();

        if ("A".equals(winer)) {
            ratingA += 5;
            ratingB -= 5;
        } else {
            ratingB += 5;
            ratingA -= 5;
        }

        updateUserRating(playerA, ratingA);
        updateUserRating(playerB, ratingB);


        Record record = new Record(
                null,
                playerA.getId(),
                playerB.getId(),
                getStepsString(playerA.getStepsX()),
                getStepsString(playerA.getStepsY()),
                getStepsString(playerB.getStepsX()),
                getStepsString(playerB.getStepsY()),
                winer,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }

    /**
     * 对局结束后，更新玩家的对战积分
     *
     * @param player 玩家
     * @param rating 对战积分
     */
    private void updateUserRating(Player player, Integer rating) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(rating);
        WebSocketServer.userMapper.updateById(user);
    }
}
