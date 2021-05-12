package com.nc;

import com.nc.service.impl.CurrencySingleton;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootApplication
public class OnlineStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineStoreApplication.class, args);
    }

    @PostConstruct
    private void init() {
        log.info("Taking currency with API nat. Bank of Belarus.");
        CurrencySingleton.updateCurrencyFromNBRBAPI();
        log.info("Starting a timer to update the currency.");
        CurrencySingleton.startSchedule();
    }
}
