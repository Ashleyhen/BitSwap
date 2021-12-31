package com.example.btcexchange.controller;

import com.example.btcexchange.DTO.WalletDto;
import com.example.btcexchange.service.WalletService;
import io.swagger.annotations.Api;
import io.vavr.control.Try;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Api("/")
public record BtcRequestEndPoint(WalletService walletService) {

    @GetMapping("home")
    public Mono<String> getWallet() {
        return Mono.just(walletService.payToWallet());
    }

    @PutMapping("new-wallet")
    public Mono<List<WalletDto>> newWallet(
            @RequestParam("wallet-name") String name,
            @RequestParam("passphrase") String passphrase) {
        return Mono.create((sink) -> {
            Try<List<WalletDto>> walletValidation = walletService.createWallet(name, passphrase);
            walletValidation
                    .peek((wallet) -> sink.success(wallet))
                    .onFailure(err -> sink.error(err));
        });
    }


}
