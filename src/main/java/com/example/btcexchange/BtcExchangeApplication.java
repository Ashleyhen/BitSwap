package com.example.btcexchange;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import reactor.core.publisher.Flux;

import java.util.function.Supplier;

@SpringBootApplication
@EnableAsync
public class BtcExchangeApplication {
    public static void main(String[] args) {
        SpringApplication.run(BtcExchangeApplication.class, args);
    }

    ;

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("spring-cloud-function-webflux OpenAPI Demo").version(appVersion)
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }


//    @Bean
//    @RouterOperation(
//            operation = @Operation(description = "says hello", operationId = "hello", tags = "person",
//                    responses = @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = String.class))))
//
//    )
//    public Supplier<String> helloWorld() {
//        return () -> "hello";
//    }

    @Bean
    public Supplier<Flux<String>> words() {
        return () -> Flux.fromArray(new String[]{"foo", "bar"});
    }
//    http://localhost:8080/swagger-ui/index.html
}
