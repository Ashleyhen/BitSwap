package com.example.btcexchange;

import com.example.btcexchange.utils.IBitcoinNetParam;
import org.bitcoinj.core.Context;
import org.bitcoinj.params.AbstractBitcoinNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class NetworkConfig implements IBitcoinNetParam {

    @Primary
    @Bean
    public AbstractBitcoinNetParams btcNetParams() {
        return TestNet3Params.get();
    }

    @Bean
    public Context createContext() {
        return new Context(btcNetParams());
    }
}
