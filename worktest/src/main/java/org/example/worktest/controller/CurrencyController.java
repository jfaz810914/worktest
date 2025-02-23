package org.example.worktest.controller;

import java.util.List;
import java.util.Optional;

import org.example.worktest.entity.Currency;
import org.example.worktest.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    // 查詢所有幣別
    @GetMapping
    public List<Currency> getAllCurrencies() {
        return currencyService.findAll();
    }

    // 根據 code 查詢單一幣別
    @GetMapping("/{code}")
    public ResponseEntity<Currency> getCurrency(@PathVariable String code) {
        Optional<Currency> currency = currencyService.findByCode(code);
        return currency.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 新增幣別資料
    @PostMapping
    public Currency createCurrency(@RequestBody Currency currency) {
        return currencyService.save(currency);
    }

    // 更新幣別資料
    @PutMapping("/{code}")
    public ResponseEntity<Currency> updateCurrency(@PathVariable String code, @RequestBody Currency currency) {
        try {
            Currency updatedCurrency = currencyService.update(code, currency);
            return ResponseEntity.ok(updatedCurrency);
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    // 刪除幣別資料
    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deleteCurrency(@PathVariable String code) {
        currencyService.delete(code);
        return ResponseEntity.noContent().build();
    }
}
