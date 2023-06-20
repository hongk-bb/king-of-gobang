package com.gobang.backend.controller.user.bot;

import com.gobang.backend.pojo.Bot;
import com.gobang.backend.service.user.bot.UserBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/user/bot/")
public class UserBotController {

    @Autowired
    private UserBotService userBotService;

    @PostMapping("/add")
    public Map<String, String> add(@RequestParam Map<String, String> data) {
        return userBotService.add(data);
    }

    @PostMapping("/remove")
    public Map<String, String> remove(@RequestParam Map<String, String> data) {
        return userBotService.remove(data);
    }

    @PostMapping("/update")
    public Map<String, String> update(@RequestParam Map<String, String> data) {
        return userBotService.update(data);
    }

    @GetMapping("/getlist")
    public List<Bot> getList() {
        return userBotService.getList();
    }
}
