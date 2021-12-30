package com.example.btcexchange.controller;

import com.example.btcexchange.service.WalletService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api("/")
public record BtcRequestEndPoint(WalletService walletService) {

    @GetMapping("home")
    public String getTemp() {

        return walletService.payToWallet();

    }
}
