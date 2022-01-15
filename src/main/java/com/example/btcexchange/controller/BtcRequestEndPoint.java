//package com.example.btcexchange.controller;
//
//import com.example.btcexchange.DTO.TransferToDto;
//import com.example.btcexchange.DTO.WalletDto;
//import com.example.btcexchange.service.WalletService;
////import io.swagger.annotations.Api;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.servers.Server;
//import io.swagger.v3.oas.annotations.servers.Servers;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import io.vavr.control.Try;
//import org.jetbrains.annotations.NotNull;
//import org.springdoc.core.annotations.RouterOperation;
//import org.springframework.cloud.function.context.FunctionCatalog;
//import org.springframework.cloud.function.context.catalog.FunctionCatalogEvent;
//import org.springframework.cloud.function.context.config.RoutingFunction;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//import java.util.function.Supplier;
//
////
//@RestController
//public record BtcRequestEndPoint(WalletService walletService) {
//    final static String SwaggerTag = "exchange";
//
//    @GetMapping("transfer-to")
//    @Tag(name = SwaggerTag)
//    public Mono<String> transferTo(
//            @RequestParam("from-wallet") @NotNull String fromWalletId,
//            @RequestParam("to-wallet") @NotNull String toWalletId,
//            @RequestParam("amount") @NotNull Double amount
//    ) {
//        return walletService.transferTo(new TransferToDto(fromWalletId, toWalletId, amount)).fold(Mono::error, Mono::just);
//    }
//
//    @Bean
//    @RouterOperation(
//            operation = @Operation(description = "says hello", operationId = "getAllWallets", tags = SwaggerTag,
//                    responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WalletDto.class))))
//
//    )
//    public Supplier<Flux<WalletDto>> getAllWallets() {
//        return () -> walletService.getWallets().fold(Flux::error, Flux::fromIterable);
//    }
////
//
//    @PutMapping("new-wallet")
//    @Tag(name = SwaggerTag)
//    public Mono<List<WalletDto>> newWallet(
//            @RequestParam("wallet-nameId") @NotNull String name,
//            @RequestParam("passphrase") @NotNull String passphrase) {
//        return Mono.create((sink) -> {
//            Try<List<WalletDto>> walletValidation = walletService.createWallet(name, passphrase);
//            walletValidation
//                    .onSuccess(sink::success)
//                    .onFailure(sink::error);
//        });
//    }
//
//
//}
