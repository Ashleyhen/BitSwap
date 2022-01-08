package com.example.btcexchange.controller;

import com.example.btcexchange.DTO.TransferToDto;
import com.example.btcexchange.DTO.WalletDto;
import com.example.btcexchange.service.WalletService;
import io.swagger.annotations.Api;
import io.vavr.control.Try;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Api("/")
public record BtcRequestEndPoint(WalletService walletService) {

    @GetMapping("transfer-to")
    public Mono<String> transferTo(
            @RequestParam("from-wallet") @NotNull String fromWalletId,
            @RequestParam("to-wallet") @NotNull String toWalletId,
            @RequestParam("amount") @NotNull Double amount
    ) {
        return walletService.transferTo(new TransferToDto(fromWalletId, toWalletId, amount)).fold(Mono::error, Mono::just);
    }

    @GetMapping("get-all-wallets")
    public Flux<WalletDto> getAllWallets() {
        return walletService.getWallets().fold(Flux::error, Flux::fromIterable);
    }


    @PutMapping("new-wallet")
    public Mono<List<WalletDto>> newWallet(
            @RequestParam("wallet-nameId") @NotNull String name,
            @RequestParam("passphrase") @NotNull String passphrase) {
        return Mono.create((sink) -> {
            Try<List<WalletDto>> walletValidation = walletService.createWallet(name, passphrase);
            walletValidation
                    .onSuccess(sink::success)
                    .onFailure(sink::error);
        });
    }


}
