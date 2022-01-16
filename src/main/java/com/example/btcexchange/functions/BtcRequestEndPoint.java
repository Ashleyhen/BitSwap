package com.example.btcexchange.functions;

import com.example.btcexchange.DTO.TransferToDto;
import com.example.btcexchange.DTO.WalletDto;
import com.example.btcexchange.service.WalletService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Try;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

@RestController
public record BtcRequestEndPoint(WalletService walletService) {
    final static String SwaggerTag = "exchange";


    @Bean
    public Function<WalletDto, String> multiInputSingleOutput() {
        return tuple -> tuple.getNameId().toUpperCase(Locale.ROOT);
    }

    @Bean
    public Function<Tuple2<String, String>, Mono<List<WalletDto>>> newWallet() {
        return func -> Mono.create((sink) -> {
            Try<List<WalletDto>> walletValidation = walletService.createWallet(func.getT1(), func.getT2());
            walletValidation
                    .onSuccess(sink::success)
                    .onFailure(sink::error);
        });
    }

    @Bean
    public Supplier<Flux<WalletDto>> getAllWallets() {
        return () -> walletService.getWallets().fold(Flux::error, Flux::fromIterable);
    }

    @GetMapping("transfer-to")
    @Tag(name = SwaggerTag)
    public Mono<String> transferTo(
            @RequestParam("from-wallet") String fromWalletId,
            @RequestParam("to-wallet") String toWalletId,
            @RequestParam("amount") Double amount
    ) {
        return walletService.transferTo(new TransferToDto(fromWalletId, toWalletId, amount)).fold(Mono::error, Mono::just);
    }


}
