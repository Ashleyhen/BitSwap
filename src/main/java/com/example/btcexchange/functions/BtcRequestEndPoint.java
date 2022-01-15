package com.example.btcexchange.functions;

import com.example.btcexchange.DTO.TransferToDto;
import com.example.btcexchange.DTO.WalletDto;
import com.example.btcexchange.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@RestController
public record BtcRequestEndPoint(WalletService walletService) {

    @Bean
    @RouterOperation(
            operation = @Operation(description = "says hello", operationId = "hello", tags = "person",
                    responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = TransferToDto.class))))

    )
    Supplier<String> getEmployeeByIdRoute() {

        return () -> "hello";
    }


    @Bean
    public Supplier<Flux<WalletDto>> getAllWallets() {
        return () -> walletService.getWallets().fold(Flux::error, Flux::fromIterable);
    }

}
