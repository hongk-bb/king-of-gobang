package com.gobang.backend.service.impl.ranklist;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gobang.backend.mapper.UserMapper;
import com.gobang.backend.pojo.User;
import com.gobang.backend.service.ranklist.GetRanklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class GetRanklistServiceImpl implements GetRanklistService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public JSONObject getList(Integer pageNum, Integer pageSize) {
        IPage<User> page = new Page<>(pageNum, pageSize);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("rating");
        List<User> users = userMapper.selectPage(page, queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        for(User user : users) {  // 清空密码
            user.setPassword("");
        }
        resp.put("users", users);
        resp.put("users_count", userMapper.selectCount(null));
        return resp;
    }
}
