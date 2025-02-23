package org.example.worktest;

import org.example.worktest.controller.CoindeskController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorktestApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CoindeskController coindeskController;

    @Test
    void testCoindeskApiCall() {
        // 確認 API 返回的資料包含 "updatedTime" 和 "currencies" 鍵
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/coindesk", Map.class);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("updatedTime"));
        assertTrue(response.getBody().containsKey("currencies"));
    }

    @Test
    void testDataTransformation() {
        // 模擬 Coindesk API 返回的資料
        Map<String, Object> timeMap = new HashMap<>();
        timeMap.put("updated", "Sep 2, 2024 07:07:20 UTC");

        Map<String, Object> usdMap = new HashMap<>();
        usdMap.put("code", "USD");
        usdMap.put("rate", "57,756.298");

        Map<String, Object> gbpMap = new HashMap<>();
        gbpMap.put("code", "GBP");
        gbpMap.put("rate", "43,984.02");

        Map<String, Object> bpiMap = new HashMap<>();
        bpiMap.put("USD", usdMap);
        bpiMap.put("GBP", gbpMap);

        Map<String, Object> mockData = new HashMap<>();
        mockData.put("time", timeMap);
        mockData.put("bpi", bpiMap);

        // 假設 transformData 方法進行資料轉換
        Map<String, Object> transformed = coindeskController.transformData(mockData);

        // 驗證轉換後資料中的欄位是否正確
        assertNotNull(transformed.get("updatedTime"));

        // 進行類型轉換來確保取值正確
        Map<String, Object> currencies = (Map<String, Object>) transformed.get("currencies");
        assertNotNull(currencies);
        Map<String, Object> usd = (Map<String, Object>) currencies.get("USD");
        assertNotNull(usd);
        assertEquals("USD", usd.get("currency"));
        assertNotNull(usd.get("rate"));
    }
}
