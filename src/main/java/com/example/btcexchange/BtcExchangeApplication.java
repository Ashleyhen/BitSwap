package com.example.btcexchange;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class BtcExchangeApplication {


    public static void main(String[] args) {
        SpringApplication.run(BtcExchangeApplication.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
        return new OpenAPI()
                .components(new Components())
                .info(new Info().title("spring-cloud-function-webflux OpenAPI Demo").version(appVersion)
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

//    https://github.com/bitcoinj/bitcoinj/tree/master/examples/src/main/java/org/bitcoinj/examples
//    http://localhost:8080/swagger-ui/index.html
}
