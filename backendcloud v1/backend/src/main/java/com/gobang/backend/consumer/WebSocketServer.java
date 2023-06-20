package com.gobang.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.gobang.backend.consumer.utils.Game;
import com.gobang.backend.mapper.BotMapper;
import com.gobang.backend.mapper.RecordMapper;
import com.gobang.backend.mapper.UserMapper;
import com.gobang.backend.pojo.Bot;
import com.gobang.backend.pojo.User;
import com.gobang.backend.utils.JwtAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 用户每建立一个连接，就会new一个新的实例，不是单例模式
 */
@Component
@ServerEndpoint("/websocket/{token}")
public class WebSocketServer {

    public static ConcurrentHashMap<Integer, WebSocketServer> users = new ConcurrentHashMap<>(); // 静态变量，对所有实例都可见，公共的，只有一个

    private User user;
    private Session session = null;

    private final static CopyOnWriteArrayList<User> matchpool = new CopyOnWriteArrayList<>();  // 开一个线程池,线程安全

    public static UserMapper userMapper;  // 静态变量，访问时需要使用类名进行访问。
    public static RecordMapper recordMapper;

    public static RestTemplate restTemplate;

    private static BotMapper botMapper;

    public Game game = null;

    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    private static ReentrantLock lock = new ReentrantLock();

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }

    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        // 建立连接
        this.session = session;
        System.out.println("connect!");

        Integer userId = JwtAuthentication.getUserId(token);
        this.user = userMapper.selectById(userId);
        if (this.user != null) {
            users.put(userId, this);
        } else {
            this.session.close();
        }
    }

    @OnClose
    public void onClose() {
        // 关闭链接
        System.out.println("disconnect!");

        stopMatching();

        if (this.user != null) {
            users.remove(this.user.getId());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // 从Client接收消息
        System.out.println("receive message!");
        // 从json中取出前端传的event
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startMatching(data.getInteger("bot_id"));
        } else if ("stop-matching".equals(event)) {
            stopMatching();
        } else if ("move".equals(event)) {
            move(data.getInteger("nextStepX"), data.getInteger("nextStepY"));
        }
    }

    /**
     * 设置玩家移动
     *
     * @param nextStepX 玩家下一步移动的X轴方向
     * @param nextStepY 玩家下一步移动的Y轴方向
     */
    private void move(Integer nextStepX, Integer nextStepY) {
        if (game.getPlayerA().getStep() <= game.getPlayerB().getStep()) {  // 当前是A的回合
            if (game.getPlayerA().getId().equals(user.getId()) && game.getPlayerA().getBotId().equals(-1)) {  // 如果当前的操作是A的, 并且是亲自出马
                game.setNextStepX(nextStepX);
                game.setNextStepY(nextStepY);
            }
        } else { // 当前是B的回合
            if (game.getPlayerB().getId().equals(user.getId()) && game.getPlayerB().getBotId().equals(-1)) {
                game.setNextStepX(nextStepX);
                game.setNextStepY(nextStepY);
            }
        }
    }

    /**
     * 停止匹配函数
     */
    private void stopMatching() {
        System.out.println("stop matching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        restTemplate.postForObject(removePlayerUrl, data, String.class);
    }

    /**
     * 匹配成功后，开始游戏前做的内容
     *
     * @param aId
     * @param bId
     */
    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId) {
        User a = userMapper.selectById(aId);
        User b = userMapper.selectById(bId);

        Bot botA = botMapper.selectById(aBotId);
        Bot botB = botMapper.selectById(bBotId);


        // 生成地图
        Game game = new Game(
                15,
                15,
                a.getId(),
                botA,
                b.getId(),
                botB);

        // 启动一个线程
        game.start();

        if (users.get(a.getId()) != null)
            users.get(a.getId()).game = game;
        if (users.get(b.getId()) != null)
            users.get(b.getId()).game = game;

        JSONObject respGame = new JSONObject();
        respGame.put("a_id", game.getPlayerA().getId());
        respGame.put("b_id", game.getPlayerB().getId());

        // 往前端发送匹配成功的消息
        JSONObject respA = new JSONObject();
        respA.put("event", "success-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", respGame);
        // 获取A的webSocket，并向前端发送信息
        if (users.get(a.getId()) != null) {
            WebSocketServer webSocketServerA = users.get(a.getId());
            webSocketServerA.sendMessage(respA.toJSONString());
        }

        JSONObject respB = new JSONObject();
        respB.put("event", "success-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", respGame);
        //获取B的webSocket并向前端发送信息
        if (users.get(b.getId()) != null)
            users.get(b.getId()).sendMessage(respB.toJSONString());
    }

    /**
     * 开始匹配函数
     */
    private void startMatching(Integer botId) {
        System.out.println("start matching");
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", this.user.getId().toString());
        data.add("rating", this.user.getRating().toString());
        data.add("bot_id", botId.toString());
        restTemplate.postForObject(addPlayerUrl, data, String.class);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    public void sendMessage(String message) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
