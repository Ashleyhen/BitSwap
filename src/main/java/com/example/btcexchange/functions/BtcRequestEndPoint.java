package com.example.btcexchange.functions;

import com.example.btcexchange.DTO.TransferToDto;
import com.example.btcexchange.DTO.WalletDto;
import com.example.btcexchange.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.vavr.control.Try;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@RestController
public record BtcRequestEndPoint(WalletService walletService) {
    final static String swaggerTag = "exchange";


    @GetMapping("new-wallet")
    @Tag(name = swaggerTag)
    public Mono<WalletDto> newWallet(
            @RequestParam("name-id") String nameId,
            @RequestParam("passphrase") String passphrase
    ) {
        return Mono.create((sink) -> {
            Try<WalletDto> walletValidation = walletService.createWallet(nameId, passphrase);
            walletValidation
                    .onSuccess(sink::success)
                    .onFailure(sink::error);
        });
    }

    @Bean
    @RouterOperation(
            operation = @Operation(description = "returns of a list of all the current test wallets",
                    operationId = "getAllWallets", tags = swaggerTag,
                    responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WalletDto.class))))

    )
    public Supplier<Flux<WalletDto>> getAllWallets() {
        return () -> walletService.getWallets().fold(Flux::error, Flux::fromIterable);
    }

    @GetMapping("transfer-to")
    @Tag(name = swaggerTag)
    public Mono<String> transferTo(
            @RequestParam("from-wallet") String fromWalletId,
            @RequestParam("to-wallet") String toWalletId,
            @RequestParam("amount") Double amount
    ) {
        return walletService.transferTo(new TransferToDto(fromWalletId, toWalletId, amount)).fold(Mono::error, Mono::just);
    }


}
