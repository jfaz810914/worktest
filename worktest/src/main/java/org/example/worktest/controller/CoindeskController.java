package org.example.worktest.controller;


import org.example.worktest.service.CurrencyService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api")
public class CoindeskController {
    @Autowired
    private CurrencyService currencyService;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String COINDESK_API_URL = "https://kengp3.github.io/blog/coindesk.json";

    @GetMapping("/coindesk")
    public Map<String, Object> getCoindeskData() {
        Map<String, Object> response = restTemplate.getForObject(COINDESK_API_URL, HashMap.class);
        return transformData(response);
    }

    public Map<String, Object> transformData(Map<String, Object> originalData) {
        Map<String, Object> transformed = new HashMap<>();
        Map<String, Object> timeData = (Map<String, Object>) originalData.get("time");
        Map<String, Object> bpiData = (Map<String, Object>) originalData.get("bpi");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        transformed.put("updatedTime", LocalDateTime.now().format(formatter));

        Map<String, Object> currencyData = new HashMap<>();
        for (String key : bpiData.keySet()) {
            Map<String, Object> currencyInfo = (Map<String, Object>) bpiData.get(key);
            Map<String, Object> currencyDetails = new HashMap<>();
            String code = (String) currencyInfo.get("code");
            currencyDetails.put("currency", code);
            currencyDetails.put("rate", currencyInfo.get("rate"));

            // 从数据库中获取中文名称
            String name = currencyService.getCurrencyNameByCode(code);
            currencyDetails.put("name", name != null ? name : "未知幣別");

            currencyData.put(key, currencyDetails);
        }

        transformed.put("Currency", currencyData);
        return transformed;
    }
}
