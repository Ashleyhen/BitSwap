package com.example.btcexchange.functions;

import com.example.btcexchange.DTO.WalletDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;
import java.util.function.Function;

@Configuration
public class testEndpoints {
    final static String testingTag = "testing-endpoints";

    @Bean
    @RouterOperation(
            operation = @Operation(description = "says hello", operationId = "getAllWallets", tags = testingTag,
                    responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = WalletDto.class))))

    )
    public Function<WalletDto, String> multiInputSingleOutput() {
        return tuple -> tuple.getNameId().toUpperCase(Locale.ROOT);
    }
}
