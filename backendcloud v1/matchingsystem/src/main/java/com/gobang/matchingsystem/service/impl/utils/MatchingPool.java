package com.gobang.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread{
    private static List<Player> players = new ArrayList<>();
    private static ConcurrentSkipListSet<Integer> playersSet = new ConcurrentSkipListSet<>();
    private ReentrantLock lock = new ReentrantLock();

    private static RestTemplate restTemplate;
//    private final static String startGameUrl = "http://127.0.0.1:3000/pk/game/start/";
    private final static String startGameUrl = "http://127.0.0.1:3000/pk/game/start/";


    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating, Integer botId){
        lock.lock();
        try {
            players.add(new Player(userId, rating, botId, 0));
        }finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            List<Player> newPlayers = new ArrayList<>();
            for (Player player : players) {
                if (!player.getUserId().equals(userId)) {
                    newPlayers.add(player);
                }
            }
            players = newPlayers;
        }finally {
            lock.unlock();
        }
    }

    /**
     * 将所有玩家的等待时间加1
     */
    private void increaseWaitingTime() {
        for(Player player : players ) {
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    /**
     * 判断两名玩家是否匹配
     * @param a 玩家a
     * @param b 玩家b
     * @return true 已匹配，false未匹配
     */
    private boolean checkMatch(Player a, Player b) {
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        // 分差需要能被a和b都接收，因此需要保证分差小于a和b的等待时间*10的最小值
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDelta <= waitingTime * 20;
    }

    /**
     * 返回a和b的匹配结果
     * @param a
     * @param b
     */
    private void sendResult(Player a, Player b) {
        System.out.println("send result: " + a + " " + b);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", a.getUserId().toString());
        data.add("b_id", b.getUserId().toString());
        data.add("a_bot_id", a.getBotId().toString());
        data.add("b_bot_id", b.getBotId().toString());
        restTemplate.postForObject(startGameUrl, data, String.class);
    }

    /**
     * 尝试匹配所有玩家，优先匹配等待时间久的玩家，也就是下标小的玩家
     */
    private void matchPlayers() {
//        System.out.println("match players: " + players.toString());
        boolean[] used = new boolean[players.size()];
        for (int i = 0; i < players.size(); i++) {
            if (used[i]) continue;
            for (int j = i + 1; j < players.size(); j++) {
                if(used[j]) continue;
                Player a = players.get(i), b = players.get(j);
                if(checkMatch(a, b)) {
                    used[i] = used[j] = true;
                    sendResult(a, b);
                    break;
                }
            }
        }
        // 将匹配成功的玩家删除
        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if(!used[i])
                newPlayers.add(players.get(i));
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(1000);
                lock.lock();
                try {
                    increaseWaitingTime();
                    matchPlayers();
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
