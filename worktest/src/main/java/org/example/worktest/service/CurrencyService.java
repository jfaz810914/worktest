package org.example.worktest.service;

import java.util.List;
import java.util.Optional;

import org.example.worktest.entity.Currency;
import org.example.worktest.repo.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    // 查詢所有幣別
    public List<Currency> findAll() {
        return currencyRepository.findAll();
    }

    // 根據 code 查詢單一幣別
    public Optional<Currency> findByCode(String code) {
        return currencyRepository.findById(code);
    }

    // 新增或儲存幣別資料
    public Currency save(Currency currency) {
        return currencyRepository.save(currency);
    }

    // 更新幣別資料
    public Currency update(String code, Currency newCurrency) {
        return currencyRepository.findById(code).map(currency -> {
            currency.setName(newCurrency.getName());
            return currencyRepository.save(currency);
        }).orElseThrow(() -> new RuntimeException("Currency not found"));
    }

    // 刪除幣別資料
    public void delete(String code) {
        currencyRepository.deleteById(code);
    }

    // 根據幣種代碼查詢幣種名稱
    public String getCurrencyNameByCode(String code) {
        Optional<Currency> currency = currencyRepository.findById(code);
        return currency.map(Currency::getName).orElse("未知幣別");
    }
}
