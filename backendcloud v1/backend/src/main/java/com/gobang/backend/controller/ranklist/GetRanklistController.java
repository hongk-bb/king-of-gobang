package com.gobang.backend.controller.ranklist;

import com.alibaba.fastjson2.JSONObject;
import com.gobang.backend.service.ranklist.GetRanklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ranklist")
public class GetRanklistController {
    @Autowired
    private GetRanklistService getRanklistService;

    @GetMapping("/getlist")
    public JSONObject getList(@RequestParam Map<String, String> data) {
        int pageNum = Integer.parseInt(data.get("pageNum"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        return getRanklistService.getList(pageNum, pageSize);
    }
}
