package com.example.btcexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BtcExchangeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BtcExchangeApplication.class, args);
    }

//    http://localhost:8080/swagger-ui/#/btc-request-end-point
}
