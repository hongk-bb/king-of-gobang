package com.gobang.backend.service.user.bot;

import com.gobang.backend.pojo.Bot;

import java.util.List;
import java.util.Map;


public interface UserBotService {
    /**
     * 新增一个Bot
     * @param data Bot数据
     * @return
     */
    Map<String,String> add(Map<String, String> data);

    /**
     * 删除一个Bot
     * @param data Bot数据
     * @return
     */
    Map<String,String> remove(Map<String, String> data);

    /**
     * 更新一个Bot
     * @param data Bot数据
     * @return
     */
    Map<String, String> update(Map<String, String> data);

    /**
     * 获取当前用户的所有Bot
     * @return 所有Bot
     */
    List<Bot> getList();
}
