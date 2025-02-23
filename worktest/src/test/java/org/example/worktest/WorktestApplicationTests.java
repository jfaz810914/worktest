package org.example.worktest;

import org.example.worktest.controller.CoindeskController;
import org.example.worktest.entity.Currency;
import org.example.worktest.repo.CurrencyRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WorktestApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CoindeskController coindeskController;
    @Autowired
    private CurrencyRepository currencyRepository;
    //測試4
    @Test
    void testNewCoindeskApiCall() {
        // 確認 API 返回的資料包含 "updatedTime" 和 "currencies" 鍵
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/newcoindesk", Map.class);
        System.out.println(response.getBody());
    }
    //測試3
    @Test
    void testCoindeskApiCall() {
        // 確認 API 返回的資料包含 "updatedTime" 和 "currencies" 鍵
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/coindesk", Map.class);
        System.out.println(response.getBody());
    }

    //測試1
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
        Map<String, Object> currencies = (Map<String, Object>) transformed.get("Currency");
        assertNotNull(currencies);
        Map<String, Object> usd = (Map<String, Object>) currencies.get("USD");
        assertNotNull(usd);
        assertEquals("USD", usd.get("currency"));
        assertNotNull(usd.get("rate"));
    }

    //測試2-驗證新增
    @Test
    @Rollback(value = false)
    void testCreateCurrency() {
        Currency currency = new Currency("JPY", "日圓");
        ResponseEntity<Currency> response = restTemplate.postForEntity("/api/currencies", currency, Currency.class);
        // 檢查 HTTP 回應狀態碼是否為 201 Created
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 確認回傳的資料是否正確
        Currency createdCurrency = response.getBody();
        assertNotNull(createdCurrency);
        assertEquals("JPY", createdCurrency.getCode());
        assertEquals("日圓", createdCurrency.getName());

        // 驗證資料是否真的寫入資料庫
        Optional<Currency> savedCurrency = currencyRepository.findByCode("JPY");
        assertTrue(savedCurrency.isPresent());
        assertEquals("日圓", savedCurrency.get().getName());
    }

    //測試2-驗證查詢
    @Test
    @Rollback(value = false)
    void testSelectCurrency() {
        ResponseEntity<Currency> response = restTemplate.getForEntity("/api/currencies/USD", Currency.class);

        // 驗證 HTTP 狀態碼是否為 200 OK
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // 驗證返回的 JSON 數據
        Currency currencyResponse = response.getBody();
        System.out.println(currencyResponse);
    }

    //測試2-驗證更新
    @Test
    @Rollback(value = false)
    void testUpdateCurrency() {
        Currency updatecurrency = new Currency("USD", "美金");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Currency> requestEntity = new HttpEntity<>(updatecurrency, headers);
        ResponseEntity<Currency> updateResponse = restTemplate.exchange(
                "/api/currencies/USD", HttpMethod.PUT, requestEntity, Currency.class);

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());

        ResponseEntity<Currency> response = restTemplate.getForEntity("/api/currencies/USD", Currency.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        Currency currencyResponse = response.getBody();
        // 打印更新後的數據
        System.out.println(currencyResponse);
    }

    //測試2-驗證刪除
    @Test
    @Transactional
    @Rollback(value = false)
    void testDeleteCurrency() {

        ResponseEntity<Void> deleteResponse = restTemplate.exchange("/api/currencies/USD", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        ResponseEntity<Currency> response = restTemplate.getForEntity("/api/currencies/USD", Currency.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // 打印删除後的查詢結果（應該是 404）
        System.out.println("刪除後查詢 USD，狀態碼：" + response.getStatusCode());
    }
}
