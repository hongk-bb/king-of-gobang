package com.gobang.botrunningsystem.service.impl.utils;

import com.gobang.botrunningsystem.utils.BotInterface;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;


@Component
public class Consumer extends Thread {
    private Bot bot;
    private static RestTemplate restTemplate;
//    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";
    private final static String receiveBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Consumer.restTemplate = restTemplate;
    }

    public void startTimeout(int timeout, Bot bot) {
        this.bot = bot;
        this.start();

        try {
            this.join(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.interrupt(); // 中断当前线程
        }
    }

    /**
     * 在code中的Bot类名后面加上uid
     *
     * @param code
     * @param uid
     * @return
     */
    private String addUid(String code, String uid) {
        // indexOf 在code中查找目标字符串
        int k = code.indexOf(" implements java.util.function.Supplier<List<Integer>>");
        return code.substring(0, k) + uid + code.substring(k);
    }

    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();  // 随机一个字符串
        String uid = uuid.toString().substring(0, 8); // 取前8位

        Supplier<List<Integer>> botInterface = Reflect.compile(
                "com.gobang.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

        File file = new File("input.txt");
        try (PrintWriter fout = new PrintWriter(file)) {
            fout.println(bot.getInput());
            fout.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        List<Integer> nextStep = botInterface.get();

//        System.out.println("move-direction: " + bot.getUserId() + " " + nextStep + " " + bot.getInput());
//        System.out.println("nextStep0: " + nextStep.get(0));
//        System.out.println("nextStep1: " + nextStep.get(1));

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("nextStepX", nextStep.get(0).toString());
        data.add("nextStepY", nextStep.get(1).toString());
        restTemplate.postForObject(receiveBotMoveUrl, data, String.class);
    }
}
