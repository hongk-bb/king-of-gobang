package com.gobang.backend.service.impl.user.bot;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gobang.backend.mapper.BotMapper;
import com.gobang.backend.pojo.Bot;
import com.gobang.backend.pojo.User;
import com.gobang.backend.service.impl.utils.UserDetailsImpl;
import com.gobang.backend.service.user.bot.UserBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserBotServiceImpl implements UserBotService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
        // 获取添加bot的用户信息
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = loginUser.getUser();

        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();
        if(title == null || title.length() == 0) {
            map.put("error_msg", "标题不能为空");
            return map;
        }
        if(title.length() > 100) {
            map.put("error_msg", "标题长度不能大于100");
            return map;
        }
        if(description == null && description.length() == 0) {
            description = "这个用户很懒，什么也没留下";
        }
        if(description.length() > 300) {
            map.put("error_msg", "Bot描述的chang不能大于300");
            return map;
        }
        if(content == null || content.length() == 0) {
            map.put("error_msg", "Bot代码不能为空");
            return map;
        }
//        if(content.length() > 1000000) {
//            map.put("error_msg", "Bot代码长度不能大于1000000");
//            return map;
//        }
        Date now = new Date();
        Bot bot = new Bot(null, user.getId(), title, description, content, now, now);

        botMapper.insert(bot);
        map.put("error_msg", "success");
        return map;
    }

    @Override
    public Map<String, String> remove(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = userDetails.getUser();

        Map<String, String> map = new HashMap<>();

        Integer bot_id = Integer.parseInt(data.get("bot_id"));
        Bot bot = botMapper.selectById(bot_id);
        if(bot == null) {
            map.put("error_msg", "Bot不存在或已被删除");
            return map;
        }
        if(!bot.getUserId().equals(user.getId())){
            map.put("error_msg", "没有权限删除");
            return map;
        }
        botMapper.deleteById(bot_id);
        map.put("error_msg", "success");
        return map;
    }

    @Override
    public Map<String, String> update(Map<String, String> data) {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = userDetails.getUser();

        Map<String, String> map = new HashMap<>();

        Integer bot_id = Integer.parseInt(data.get("bot_id"));
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        if(title == null || title.length() == 0) {
            map.put("error_msg", "标题不能为空");
            return map;
        }
        if(title.length() > 100) {
            map.put("error_msg", "标题长度不能大于100");
            return map;
        }
        if(description == null && description.length() == 0) {
            description = "这个用户很懒，什么也没留下";
        }
        if(description.length() > 300) {
            map.put("error_msg", "Bot描述的chang不能大于300");
            return map;
        }
        if(content == null || content.length() == 0) {
            map.put("error_msg", "Bot代码不能为空");
            return map;
        }
//        if(content.length() > 10000) {
//            map.put("error_msg", "Bot代码长度不能大于10000");
//            return map;
//        }

        Bot bot = botMapper.selectById(bot_id);

        if(bot == null) {
            map.put("error_msg", "Bot不存在或已被删除");
            return map;
        }
        if(!bot.getUserId().equals(user.getId())) {
            map.put("error_msg", "没有权限修改");
            return map;
        }
        Bot newBot = new Bot(
                bot.getId(),
                user.getId(),
                title,
                description,
                content,
                bot.getCreateTime(),
                new Date()
        );
        botMapper.updateById(newBot);
        map.put("error_msg", "success");
        return map;
    }

    @Override
    public List<Bot> getList() {
        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authenticationToken.getPrincipal();
        User user = userDetails.getUser();

        List<Map<String, String>> lists = new ArrayList<>();

        QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());

        return botMapper.selectList(queryWrapper);
    }
}
