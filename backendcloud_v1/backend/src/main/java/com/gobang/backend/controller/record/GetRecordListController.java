package com.gobang.backend.controller.record;

import com.alibaba.fastjson2.JSONObject;
import com.gobang.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/record")
public class GetRecordListController {
    @Autowired
    private GetRecordListService getRecordListService;

    @GetMapping("/getlist")
    JSONObject getList(@RequestParam Map<String, String> data) {
        Integer pageNum = Integer.parseInt(data.get("pageNum"));
        Integer pageSize = Integer.parseInt(data.get("pageSize"));

        return getRecordListService.getList(pageNum, pageSize);
    }
}
