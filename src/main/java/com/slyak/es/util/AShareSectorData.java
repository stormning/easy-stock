package com.slyak.es.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class AShareSectorData {
    public static void main(String[] args) {
        // 设置API请求URL
        String url = "https://api.waditu.com";

        // 设置API参数
        Map<String, Object> params = new HashMap<>();
        params.put("api_name", "sector");
        params.put("token", "7affbc2d5984bb63bc9bbaa0dd8ecf1acd9c02710c68d7c1c7c4c4ce");
        params.put("params", new HashMap<String, Object>() {{
            put("fields", "date,sector,pe_ttm");
            put("start_date", "2022-01-01");
            put("end_date", "2022-12-31");
        }});

        // 发送API请求并解析响应
        String json = HttpUtil.sendJsonPost(url, params);
        log.info(json);
        ASectorData sectorData = JSON.parseObject(json, ASectorData.class);

        // 输出结果
        System.out.println("板块名称\t日期\t估值");
        for (AData data : sectorData.getData()) {
            System.out.println(data.getSector() + "\t" + data.getDate() + "\t" + data.getPeTtm());
        }
    }


    public static class ASectorData {
        private String code;
        private String msg;
        private List<AData> data;

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public List<AData> getData() {
            return data;
        }
    }

    public static class AData {
        private String date;
        private String sector;
        private double pe_ttm;

        public String getDate() {
            return date;
        }

        public String getSector() {
            return sector;
        }

        public double getPeTtm() {
            return pe_ttm;
        }
    }
}
