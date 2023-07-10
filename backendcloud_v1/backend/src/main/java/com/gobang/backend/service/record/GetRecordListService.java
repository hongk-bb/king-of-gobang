package com.gobang.backend.service.record;

import com.alibaba.fastjson2.JSONObject;


public interface GetRecordListService {
    JSONObject getList(Integer pageNum, Integer pageSize);
}
