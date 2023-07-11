package com.gobang.backend.service.impl.record;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gobang.backend.mapper.RecordMapper;
import com.gobang.backend.mapper.UserMapper;
import com.gobang.backend.pojo.Record;
import com.gobang.backend.pojo.User;
import com.gobang.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class GetRecordListServiceImpl implements GetRecordListService {

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Integer pageNum, Integer pageSize) {
        IPage<Record> recordIPage = new Page<>(pageNum, pageSize);

        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");  // 按照id降序排序

        List<Record> records = recordMapper.selectPage(recordIPage, queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new ArrayList<>();
        for(Record record : records) {
            User userA = userMapper.selectById(record.getAId());
            User userB = userMapper.selectById(record.getBId());
            JSONObject item = new JSONObject();
            item.put("a_photo", userA.getPhoto());
            item.put("a_username", userA.getUsername());
            item.put("b_photo", userB.getPhoto());
            item.put("b_username", userB.getUsername());
            String result = "平局";
            if("A".equals(record.getWiner())) result = "A胜";
            else if("B".equals(record.getWiner())) result = "B胜";
            item.put("result", result);
            item.put("record", record);
            items.add(item);
        }

        resp.put("records", items);
        resp.put("records_count", recordMapper.selectCount(null)); // 对战记录总数

        return resp;
    }
}
