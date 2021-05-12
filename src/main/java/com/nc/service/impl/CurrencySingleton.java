package com.nc.service.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencySingleton {
    private static double result;
    
    public static double getDollarCurrency() {
        log.info("Getting the currency value(" + result + ").");
        return result;
    }

    public static void startSchedule() {
        log.info("Running a timer for currency updates.");
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(CurrencySingleton::updateCurrencyFromNBRBAPI, 12, 12, TimeUnit.HOURS);
    }

    public static void updateCurrencyFromNBRBAPI() {
        log.info("Taking currency value from api nat. Bank of Belarus.");
        RestTemplate restTemplate = new RestTemplate();
        LinkedHashMap currency = restTemplate.getForObject(
                "https://www.nbrb.by/api/exrates/rates/840?parammode=1", LinkedHashMap.class);
        result = Double.parseDouble(currency.get("Cur_OfficialRate").toString());
    }
}

